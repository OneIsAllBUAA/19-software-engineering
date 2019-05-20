"""mysite URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/2.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path, re_path
from login import views
from django.conf import settings
from django.conf.urls.static import static

admin.autodiscover()
urlpatterns = [
    path('admin/', admin.site.urls),

    path('', views.index),
    path('index/', views.index),
    path('login/', views.login),
    path('regist/', views.register),
    path('logout/', views.logout),

    path('choose/', views.choose),
    path('release_task/', views.release_task),
    path('release_task_1/', views.release_task_1),
    path('release_task_2/', views.release_task_2),
    path('release_task_3/', views.release_task_3),
    path('release_task_4/', views.release_task_4),
    path('video2pictures_slide/', views.video2pictures_slide),
    path('confirm_to_upload_pictures/', views.confirm_to_upload_pictures),

    path('all_task/', views.all_task),

    path('enter_task/', views.enter_task),
    # path('picture_task/', views.picture_task),
    # path('video_task/', views.video_task),
    # path('player_task/', views.player_task),

    path('one_task/', views.one_task),
    path('check_task/', views.check_task),
    path('picture_detail/', views.picture_detail),

    path('recharge/', views.recharge),
    path('download_data_set/', views.download_data_set),
    re_path(r'^download_other_files/(?P<task_id>\d+)/$', views.download_other_files),

    path('FindPassword/', views.send),
    path('ResetPassword/', views.PwdReset),

    path('choice_questions_result/',views.choice_questions_result),
    path('check_pic/', views.check_pic),
    path('chart/', views.chart),
    path('test/', views.test),
    # path('task/get_all_tasks/', views.get_all_tasks, name='get_all_tasks'),
    # path('task/get_user_tasks/', views.get_user_tasks, name='get_user_tasks'),
    re_path(r'^(?P<room_name>[^/]+)/(?P<user_name>[^/]+)/$', views.room, name='room'),

    ###########
    ## api  ###
    ###########
    # tasks
    path('api/all_tasks', views.api_all_tasks),
    path('api/enter_task', views.api_enter_task),
    path('api/favorite_tasks', views.api_favorite_tasks),
    path('api/favorite_task', views.api_favorite_task),
    path('api/undo_favorite', views.api_undo_favorite),
    path('api/grab_task', views.api_grab_task),
    path('api/undo_grab', views.api_undo_grab),
    path('api/check_task', views.api_check_task),
    path('api/submit_check_result',views.api_submit_check_result),
    # users
    path('api/login', views.api_login),
    path('api/logout', views.api_logout),
    path('api/sign_up', views.api_sign_up),
    path('api/user_info', views.api_user_info),
    path('api/my_task', views.api_my_task),
    path('api/submit_task', views.api_submit_task),
    path('api/task_user', views.api_get_task_user),
    path('api/recommend_tasks', views.api_recommend_tasks),

    path('api/recover_password', views.api_recover_password),
    path('api/reset_password', views.api_reset_password),

] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)

