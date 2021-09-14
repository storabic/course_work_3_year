import json
import random
import re
import uuid
from math import isclose
from datetime import datetime
from string import ascii_uppercase, digits

import django.core.exceptions as djangoexceptions
import django.db.utils as utils
from django.contrib.auth import get_user_model, authenticate, login
from django.http import JsonResponse
from numpy.random import permutation
from rest_framework import status
from rest_framework.decorators import api_view

from coursework.models import Client, Payment, Pool, Subject, User, ClientPoolLink


# Generates random key from range (AAAAAA - 999999)
def generate_token(length=6 + 1):
    return ''.join(random.choice(ascii_uppercase + digits) for i in range(length))


def validate_login(login):
    return re.match("^(?=[a-zA-Z0-9._]{4,20}$)(?!.*[_.]{2})[^_.].*[^_.]$", login)


@api_view(['POST'])
def register(request):
    try:
        data_json = json.loads(request.body)
        login = data_json['login']
        password = data_json['password']
        if not validate_login(login):
            return JsonResponse({'reason': 'login is invalid'}, status=status.HTTP_400_BAD_REQUEST)
        user = User.objects.create_user(username=login, password=password)
        client = Client.objects.create(user=user, user_token=uuid.uuid4())
        flag = True
        while flag:
            try:
                client.user_token = generate_token()
                client.save()
                flag = False
            except utils.IntegrityError:
                pass
        return JsonResponse({'user_token': client.user_token}, status=status.HTTP_200_OK)
    except KeyError:
        return JsonResponse(
            {'reason': 'pass json map with login and password in body'},
            status=status.HTTP_400_BAD_REQUEST
        )
    except utils.IntegrityError:
        return JsonResponse({'reason': 'login is not unique'}, status=status.HTTP_400_BAD_REQUEST)
    except djangoexceptions.ValidationError:
        return JsonResponse({})


@api_view(['POST'])
def login(request):
    data_json = json.loads(request.body)
    login = data_json['login']
    password = data_json['password']
    user = authenticate(request, username=login, password=password)
    if user is None:
        return JsonResponse({'reason': 'auth error'}, status=status.HTTP_401_UNAUTHORIZED)
    return JsonResponse({'client': Client.objects.get(user=user)}, status=status.HTTP_401_UNAUTHORIZED)


@api_view(['POST'])
def create_pool(request):
    user = request.user
    if not user.is_authenticated:
        return JsonResponse({'reason': 'auth error'}, status=status.HTTP_401_UNAUTHORIZED)
    data_json = json.loads(request.body)
    name = data_json['name']
    comment = data_json['comment']
    client = Client.objects.get(user=user)
    pool = Pool(name=name, comment=comment, creator=client)
    flag = True
    while flag:
        try:
            pool.pool_token = generate_token()
            pool.save()
            flag = False
        except utils.IntegrityError:
            pass
    ClientPoolLink.objects.create(ClientPoolLink(client=client, pool=pool))
    return JsonResponse({'user_token': pool.pool_token}, status=status.HTTP_200_OK)


@api_view(['PUT'])
def join_pool(request):
    user = request.user
    if not user.is_authenticated:
        return JsonResponse({'reason': 'auth error'}, status=status.HTTP_401_UNAUTHORIZED)
    data_json = json.loads(request.body)
    pool_token = data_json['pool_token']
    try:
        pool = Pool.objects.get(pool_token=pool_token)
    except Pool.DoesNotExist:
        return JsonResponse({'reason': 'pool not found'}, status=status.HTTP_400_BAD_REQUEST)
    client = Client.objects.get(user=user)
    ClientPoolLink.objects.create(ClientPoolLink(client=client, pool=pool))
    return JsonResponse({'pool_id': pool.id}, status=status.HTTP_200_OK)


@api_view(['POST'])
def create_subject(request):
    data_json = json.loads(request.body)
    name = data_json['name']
    comment = data_json['comment']
    pool = Pool.objects.get(data_json['pool_id'])
    equal_sum = data_json['equal_sum']
    payments_per_client = data_json['payments']
    sum = data_json['sum']
    # Проверка на правильность сумм платежей
    if not equal_sum:
        summ = 0
        for pay in payments_per_client:
            summ += pay.sum
        if not isclose(summ, sum):
            raise ArithmeticError('Parts of each client pay sum don\'t sum up to present sum')
    client = Client.objects.get(user=request.user)
    subject = Subject.objects.create(Subject(pool=pool, author=client, name=name, comment=comment))
    if equal_sum:
        # Делим сумму платежа поровну между всеми плательщиками
        eq_sum = sum / len(payments_per_client)
        for client_id in payments_per_client:
            payer = Client.objects.get(id=client_id)
            subject.payment_set.create(subject=subject, client=payer, sum=eq_sum)
    else:
        # Проставляем каждому плательщику его долю
        for pay in payments_per_client:
            payer = Client.objects.get(id=pay.client_id)
            subject.payment_set.create(subject=subject, client=payer, sum=pay.sum)
    return JsonResponse({'pool_id': pool.id, 'subject_id': subject.id})


@api_view(['GET'])
def get_subject(request):
    subject = Subject.objects.get(id=request.subject_id)
    return JsonResponse({
        'subject': subject,
        'payments': Payment.objects.filter(subject=subject)
    })


@api_view(['GET'])
def get_allowed_pools(request):
    client = Client.objects.get(id=request.client_id)
    return JsonResponse({
        'pools': client.pool_set.all()
    })


@api_view(['GET'])
def get_subjects(request):
    pool = Pool.objects.get(id=request.pool_id)
    return JsonResponse({
        'subjects': pool.subject_set.all()
    })


@api_view(['PUT'])
def make_pay(request):
    data_json = json.loads(request.body)
    payment = Payment.objects.get(data_json['payment_id'])
    payment.comment = data_json['comment']
    payment.closed_time = datetime.now()
    return JsonResponse({'ok': 1}, status=status.HTTP_200_OK)
