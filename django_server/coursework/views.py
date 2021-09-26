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

from coursework.models import Payment, Pool, Subject, User, ClientPoolLink


# Generates random key from range (AAAAAA - 999999)
def generate_token(length=6):
    return ''.join(random.choice(ascii_uppercase + "1357") for _ in range(length))


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
        client = User.objects.create(user=user)
        flag = True
        while flag:
            try:
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
    return JsonResponse({'client': User.objects.get(user=user)}, status=status.HTTP_401_UNAUTHORIZED)


@api_view(['POST'])
def create_pool(request):
    data_json = json.loads(request.body)
    client_id = data_json['client_id']
    name = data_json['name']
    comment = data_json['comment']
    client = User.objects.get(id=client_id)
    pool = Pool(name=name, comment=comment, creator=client)
    flag = True
    while flag:
        try:
            pool.pool_token = generate_token()
            pool.save()
            flag = False
        except utils.IntegrityError:
            pass
    ClientPoolLink.objects.create(client=client, pool=pool)
    return JsonResponse({'group_id': pool.id}, status=status.HTTP_200_OK)


@api_view(['POST'])
def change_pool(request):
    data_json = json.loads(request.body)
    group_id = data_json['group_id']
    name = data_json['name']
    comment = data_json['comment']
    pool = Pool.objects.get(id=group_id)
    pool.name = name
    pool.comment = comment
    pool.save()
    return JsonResponse({'success': 'ok'})


@api_view(['POST'])
def change_subject(request):
    data_json = json.loads(request.body)
    subject_id = data_json['subject_id']
    name = data_json['name']
    comment = data_json['comment']
    is_active = data_json['is_active']
    subject = Subject.objects.get(id=subject_id)
    subject.name = name
    subject.comment = comment
    subject.is_active = is_active
    subject.save()
    return JsonResponse({'success': 'ok'})


@api_view(['PUT'])
def join_pool(request):
    data_json = json.loads(request.body)
    pool_token = data_json['pool_token']
    try:
        pool = Pool.objects.get(pool_token=pool_token)
    except Pool.DoesNotExist:
        return JsonResponse({'reason': 'pool not found'}, status=status.HTTP_400_BAD_REQUEST)
    client = User.objects.get(id=data_json['client_id'])
    if ClientPoolLink.objects.filter(client=client).filter(pool=pool).count() > 0:
        return JsonResponse({'pool_id': pool.id}, status=status.HTTP_200_OK)
    ClientPoolLink.objects.create(client=client, pool=pool)
    return JsonResponse({'pool_id': pool.id}, status=status.HTTP_200_OK)


@api_view(['POST'])
def create_subject(request):
    data_json = json.loads(request.body)
    name = data_json['name']
    comment = data_json['comment']
    pool = Pool.objects.get(id=data_json['pool_id'])
    equal_sum = data_json['equal_sum']
    payments_per_client = data_json['payments']
    sum = data_json['sum']
    client = User.objects.get(id=data_json['client_id'])
    subject = Subject.objects.create(pool=pool, author=client, name=name, comment=comment, sum=sum)
    if equal_sum:
        # Делим сумму платежа поровну между всеми плательщиками
        eq_sum = sum / len(payments_per_client)
        for client_id in payments_per_client:
            payer = User.objects.get(id=client_id)
            subject.payment_set.create(subject=subject, payer=payer, sum=eq_sum, comment=comment)
    else:
        summ = 0
        # Проставляем каждому плательщику его долю
        for pay in payments_per_client:
            payer = User.objects.get(id=pay['client_id'])
            subject.payment_set.create(subject=subject, payer=payer, sum=pay['sum'])
            summ += pay['sum']
        subject.sum = summ
        subject.save()
    return JsonResponse({'pool_id': pool.id, 'subject_id': subject.id})


@api_view(['POST'])
def get_subject(request):
    data_json = json.loads(request.body)
    subject = Subject.objects.get(id=data_json['subject_id'])
    payments = []
    for pay in subject.payment_set.all():
        payments.append({'payment_id': pay.id,
                         'client_id': pay.payer.id,
                         'login': pay.payer.username,
                         'sum': pay.sum,
                         'closed_date': str(pay.closed_time),
                         'comment': pay.comment})
    return JsonResponse({
        'name': subject.name,
        'comment': subject.comment,
        'creator_login': subject.author.username,
        'creator_client_id': subject.author.id,
        'is_active': subject.is_active,
        'payments': payments
    })


@api_view(['POST'])
def get_group(request):
    data_json = json.loads(request.body)
    pool = Pool.objects.get(id=data_json['group_id'])
    subjects = []
    for subject in pool.subject_set.all():
        subjects.append({
            'id': subject.id,
            'name': subject.name,
            'comment': subject.comment,
            'author_login': subject.author.username,
            'author_client_id': subject.author.id,
            'sum': subject.sum,
            'is_active': subject.is_active})
    return JsonResponse({
        'id': pool.id,
        'name': pool.name,
        'token': pool.pool_token,
        'is_active': pool.is_active,
        'creator': {'client_id': pool.creator.id, 'login': pool.creator.username},
        'comment': pool.comment,
        'subjects': subjects
    })


@api_view(['POST'])
def get_clients(request):
    data_json = json.loads(request.body)
    pool = Pool.objects.get(id=data_json['group_id'])
    clientlink_set = ClientPoolLink.objects.filter(pool=pool)
    users = []
    for clientlink in clientlink_set:
        users.append({'client_id': clientlink.client.id, 'login': clientlink.client.username})
    return JsonResponse({'clients': users})


@api_view(['PUT'])
def make_pay(request):
    data_json = json.loads(request.body)
    payment = Payment.objects.get(data_json['payment_id'])
    payment.comment = data_json['comment']
    payment.closed_time = datetime.now()
    return JsonResponse({'ok': 1}, status=status.HTTP_200_OK)


@api_view(['POST'])
def get_allowed_pools(request):
    data_json = json.loads(request.body)
    client = User.objects.get(id=data_json['client_id'])
    pools = []
    for link in ClientPoolLink.objects.filter(client=client).all():
        pools.append({'group_id': link.pool.id, 'name': link.pool.name, 'token': link.pool.pool_token})
    return JsonResponse({'pools': pools})


@api_view(['POST'])
def get_payment(request):
    data_json = json.loads(request.body)
    pay = Payment.objects.get(id=data_json['payment_id'])
    return JsonResponse({
        'id': pay.id,
        'sum': pay.sum,
        'comment': pay.comment,
        'payer_login': pay.payer.username,
        'payer_client_id': pay.payer.id,
        'is_active': pay.is_active
    })


@api_view(['POST'])
def change_payment(request):
    data_json = json.loads(request.body)
    pay = Payment.objects.get(id=data_json['payment_id'])
    pay.comment = data_json['comment']
    pay.is_active = data_json['is_active']
    pay.sum = data_json['sum']
    subject = pay.subject
    pay.save()
    summ = 0
    for x in subject.payment_set.all():
        summ += x.sum
    subject.sum = summ
    subject.save()
    return JsonResponse({'ok': 'ok'})
