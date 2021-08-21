"""coursework URL Configuration

The `urlpatterns` list routes URLs to view. For more information please see:
    https://docs.djangoproject.com/en/3.2/topics/http/urls/
Examples:
Function view
    1. Add an import:  from my_app import view
    2. Add a URL to urlpatterns:  path('', view.home, name='home')
Class-based view
    1. Add an import:  from other_app.view import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from view import views

urlpatterns = [
    path('admin/', admin.site.urls),
    path(r'api/create_lobby', views.create_lobby, name='create_lobby'),
    path(r'api/join_lobby', views.join_lobby, name='join_lobby'),
    path(r'api/disconnect', views.disconnect, name='disconnect'),
    path(r'api/start_game', views.start_game, name='start_game'),
    path(r'api/select_player', views.select_player, name='select_player'),
    path(r'api/get_info', views.get_info, name='get_info'),
    path(r'api/send_msg', views.send_message, name='send_message'),
]
