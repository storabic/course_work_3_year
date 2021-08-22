import os

from django.contrib.auth.models import User, Group
from django.db import models


def get_image_path(instance, filename):
    return os.path.join('photos', str(instance.id), filename)


class Client(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    uid = models.UUIDField(default=None, editable=False, unique=True)


# Группа пользователей по одному сбору денег
# Может быть активной (см. is_active), когда пользователи могут добавлять комментарии и т.п., и неактивной,
# когда можно только получать информацию о группе, изменять информацию - нельзя
# Изменять параметр is_active может только создатель группы (см. creator)
class Pool(models.Model):
    group = models.OneToOneField(Group, on_delete=models.CASCADE)
    pid = models.UUIDField(primary_key=True, default=None, editable=False, unique=True)
    is_active = models.BooleanField(default=True)
    creator = models.ForeignKey(Client, on_delete=models.PROTECT, editable=False)
    comment = models.CharField(max_length=400)
    # image = models.ImageField(upload_to=get_image_path, blank=True, null=True)


# Объект оплаты, к которому линкуются сами переводы
class Subject(models.Model):
    pid = models.ForeignKey(Pool, on_delete=models.PROTECT)
    author = models.ForeignKey(Client, on_delete=models.PROTECT)
    name = models.CharField(max_length=50)
    comment = models.CharField(max_length=400)
    # image = models.ImageField(upload_to=get_image_path, blank=True, null=True)
    is_active = models.BooleanField(default=True)
    opened_time = models.DateTimeField(auto_now_add=True)


# Перевод/передача денег за объект оплаты, при closed_time != null считается не оплаченным
class Payment(models.Model):
    subject = models.ForeignKey(Subject, on_delete=models.CASCADE)
    payer = models.ForeignKey(Client, on_delete=models.PROTECT)
    sum = models.DecimalField(max_digits=42, decimal_places=2)
    closed_time = models.DateTimeField(null=True, blank=True)
    comment = models.CharField(max_length=400)
    # image = models.ImageField(upload_to=get_image_path, blank=True, null=True)
