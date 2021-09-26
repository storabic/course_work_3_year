from django.contrib import admin
from django.urls import path
from coursework import views

urlpatterns = [
    path('admin/', admin.site.urls),
    path(r'register', views.register, name='register'),
    path(r'create_pool', views.create_pool, name='create_pool'),
    path(r'get_allowed_pools', views.get_allowed_pools, name='get_allowed_pools'),
    path(r'join_pool', views.join_pool, name='join_pool'),
    path(r'change_pool', views.change_pool, name='change_pool'),
    path(r'get_group', views.get_group, name='get_group'),
    path(r'get_clients', views.get_clients, name='get_clients'),
    path(r'create_subject', views.create_subject, name='create_subject'),
    path(r'get_subject', views.get_subject, name='get_subject'),
    path(r'change_subject', views.change_subject, name='change_subject'),
    path(r'get_payment', views.get_payment, name='get_payment'),
    path(r'change_payment', views.change_payment, name='change_payment'),
]
