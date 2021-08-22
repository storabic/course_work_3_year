import json
import random
from string import ascii_uppercase, digits

import django.core.exceptions as djangoexceptions
import django.db.utils as utils
from django.http import JsonResponse
from numpy.random import permutation
from rest_framework.decorators import api_view

from .models import Game, Player


@api_view(['POST'])
def register(request):
    data_json = json.loads(request.body)
    login = data_json['login']
    password = data_json['password']

