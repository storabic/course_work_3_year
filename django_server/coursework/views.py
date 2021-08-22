import json
import random
import re
import uuid
from string import ascii_uppercase, digits

import django.core.exceptions as djangoexceptions
import django.db.utils as utils
from django.contrib.auth import get_user_model, authenticate, login
from django.http import JsonResponse
from numpy.random import permutation
from rest_framework import status
from rest_framework.decorators import api_view

from coursework.models import Client, Payment, Pool, Subject, User


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


def login(request):
    data_json = json.loads(request.body)
    login = data_json['login']
    password = data_json['password']
    user = authenticate(request, username=login, password=password)
    if user is None:
        return JsonResponse({'reason': 'auth error'}, status=status.HTTP_401_UNAUTHORIZED)


@api_view(['POST'])
def create_pool(request):
    user = request.user
    if not user.is_authenticated:
        return JsonResponse({'reason': 'auth error'}, status=status.HTTP_401_UNAUTHORIZED)
    data_json = json.loads(request.body)
    comment = data_json['comment']
    client = Client.objects.get(user=user)
    pool = Pool(comment=comment, creator=client)
    client.pool_set.add(pool)
    flag = True
    while flag:
        try:
            pool.pool_token = generate_token()
            pool.save()
            flag = False
        except utils.IntegrityError:
            pass
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
    pool.id
