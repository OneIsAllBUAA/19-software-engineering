# login/views.py
import codecs
import csv
import filetype

from django.http import HttpResponse, StreamingHttpResponse
from django.shortcuts import render, redirect
from django.utils import timezone
from django.contrib import messages
from django.utils.encoding import escape_uri_path

from login import forms, models, tools
from login.recommend_system import user, itempre
import re
import os
import shutil
#import nsfw_predict
import baiduapi
from django.core.mail import send_mail
from django.conf import settings
from django.db.models import Q

from django.core import serializers
import simplejson
from django.contrib.messages import get_messages
from django.forms.models import model_to_dict

from django.utils.safestring import mark_safe
from django.core.files.base import ContentFile
from django.core.files.storage import default_storage
import json
import random
import zipfile

digit = re.compile("^\d{1,10}$")


class member:
    def __init__(self):
        self.name = ''
        self.history = 0


class choice:
    def __init__(self):
        self.content = ''
        self.choice_value = ''
        self.members = []


def index(request):
    return render(request, 'index.html')


def login(request):
    if request.session.get('is_login', None):
        messages.error(request, "请勿重复登录！")
        return redirect("/index/")

    if request.method == "POST":
        login_form = forms.LoginForm(request.POST)
        if not login_form.is_valid():
            messages.error(request, "表单信息有误！")
            render(request, 'login.html', locals())

        username = login_form.cleaned_data['username']
        password = login_form.cleaned_data['password']

        user = models.User.objects.filter(name=username).first()
        if not user:
            # Email login
            user = models.User.objects.filter(email=username).first()
            if not user:
                messages.error(request, "用户名未注册！")
                return render(request, 'login.html', locals())
            else:
                username = user.name
        if user.password != models.gen_md5(password, username):
            messages.error(request, "密码错误！")
            return render(request, 'login.html', locals())

        request.session['is_login'] = True
        # request.session['is_admin'] = user.is_admin
        request.session['is_admin'] = True
        request.session['username'] = username
        # messages.success(request, "登录成功！")
        request.session.set_expiry(3600)
        user.last_login_time = user.login_time
        user.login_time = timezone.now()
        user.save()
        return redirect('/all_task/')

    login_form = forms.LoginForm()
    return render(request, 'login.html', locals())


def register(request):
    if request.session.get('is_login', None):
        messages.error(request, "请先退出后再注册！")
        return redirect("/index/")

    if request.method == "POST":
        register_form = forms.RegisterForm(request.POST)
        if not register_form.is_valid():
            messages.error(request, "表单信息有误！")
            return render(request, 'regist.html', locals())

        username = register_form.cleaned_data['username']
        password1 = register_form.cleaned_data['password1']
        password2 = register_form.cleaned_data['password2']
        email = register_form.cleaned_data['email']

        if (username=="" or password1 == "" or password2=="" or email==""):
            messages.error(request, "不允许提交空内容！")
            return render(request, 'regist.html', locals())
        elif not(len(username)>=1 and len(username)<=30):
            messages.error(request, "用户名只能由1-30个字符组成！")
            return render(request, 'regist.html', locals())
        elif not (len(password1) >= 1 and len(password1) <= 30):
            messages.error(request, "密码只能由1-30个字符组成！")
            return render(request, 'regist.html', locals())
        elif not (len(password2) >= 1 and len(password2) <= 30):
            messages.error(request, "密码只能由1-30个字符组成！")
            return render(request, 'regist.html', locals())
        elif not username.isalnum():
            messages.error(request, "用户名只能由字母和数字组成！")
            return render(request, 'regist.html', locals())
        elif not (password1.isalnum() and password2.isalnum()):
            messages.error(request, "密码只能由字母和数字组成！")
            return render(request, 'regist.html', locals())
        if password1 != password2:  # 两次密码是否相同
            messages.error(request, "两次输入的密码不一致！")
            return render(request, 'regist.html', locals())
        if models.User.objects.filter(name=username).exists():  # 用户名是否唯一
            messages.error(request, "该用户名已注册！")
            return render(request, 'regist.html', locals())
        if models.User.objects.filter(email=email).exists():  # 邮箱地址是否唯一
            messages.error(request, "该邮箱已注册！")
            return render(request, 'regist.html', locals())

        new_user = models.User.objects.create()
        new_user.name = username
        new_user.password = models.gen_md5(password1, username)
        new_user.email = email
        new_user.is_admin = False  # 只能注册普通用户
        new_user.save()

        request.session['is_login'] = True  # 注册后自动登录
        # request.session['is_admin'] = user.is_admin
        request.session['is_admin'] = True
        request.session['username'] = username
        request.session.set_expiry(3600)
        messages.success(request, "注册成功！")
        new_user.last_login_time = new_user.login_time = timezone.now()
        new_user.save()
        return redirect('/index/')

    register_form = forms.RegisterForm()
    return render(request, 'regist.html', locals())


def logout(request):
    if not request.session.get('is_login', None):
        messages.error(request, "您尚未登录！")
        return redirect("/index/")

    request.session.flush()
    messages.success(request, "退出成功！")
    return redirect("/index/")


# 找回密码
def send(request):
    if request.method == 'POST':
        email = request.POST.get('email')
        if (email=="" ):
            messages.error(request, "不允许提交空内容！")
        if not models.User.objects.filter(email=email).exists():
            messages.error(request, '邮箱未注册!')
            return render(request, 'FindPassword.html')
        else:
            # print(email)
            msg = '你收到这封邮件是因为你请求重置你在网站OneIsAll上的用户账户密码。请访问该页面并选择一个新密码：http://101.132.71.247:8092/ResetPassword/?email=' + email + '\n感谢使用我们的站点！\nOneIsAll团队'
            send_mail('密码重置邮件',
                      msg,
                      settings.EMAIL_FROM,
                      [email])
            return render(request, 'SendEmailDone.html')
    return render(request, 'FindPassword.html')


# 密码重置
def PwdReset(request):
    email = request.GET['email']
    if not email:
        messages.error(request, '无效的密码重置请求!')
    else:
        if request.method == 'POST':
            newPwd = request.POST.get('pwd')

            cPwd = request.POST.get('cpwd')
            if (newPwd == "" or cPwd == ""):
                messages.error(request, "不允许提交空内容！")
                return  render(request, 'PwdReset.html')
            elif not (len(newPwd) >= 1 and len(newPwd) <= 30):
                messages.error(request, "密码只能由1-30个字符组成！")
                return render(request, 'PwdReset.html')
            elif not (len(cPwd) >= 1 and len(cPwd) <= 30):
                messages.error(request, "密码只能由1-30个字符组成！")
                render(request, 'PwdReset.html')
            elif not newPwd.isalnum() or cPwd.isalnum():
                messages.error(request, "密码只能由字母和数字组成！")
                render(request, 'PwdReset.html')
            if newPwd != cPwd:
                messages.error('两次输入不一致！')
                return render(request, 'PwdReset.html')
            else:
                user = models.User.objects.filter(email=email).first()
                user.password = models.gen_md5(newPwd, user.name)
                user.save()
                return render(request, 'PwdResetDone.html')
        return render(request, 'PwdReset.html')


def choose(request):
    if not request.session.get('is_admin', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    return render(request, 'choose.html', locals())


def get_img_namelist():
    # User_list
    User_list = []
    all_user = models.User.objects.all()
    for u in all_user:
        User_list.append("<font>" + u.name + "</font>")
    return User_list

def release_task_1(request):
    if not request.session.get('is_admin', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    request.session['task_type'] = 1
    user = models.User.objects.get(name=request.session['username'])
    User_list = mark_safe(get_img_namelist())
    print(User_list)
    return render(request, 'release_task.html', locals())


def release_task_2(request):
    if not request.session.get('is_admin', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    request.session['task_type'] = 2
    user = models.User.objects.get(name=request.session['username'])
    User_list = mark_safe(get_img_namelist())
    print(User_list)
    return render(request, 'release_task.html', locals())


def release_task_3(request):
    if not request.session.get('is_admin', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    request.session['task_type'] = 3
    user = models.User.objects.get(name=request.session['username'])
    User_list = mark_safe(get_img_namelist())
    print(User_list)
    return render(request, 'release_task_1.html', locals())


def release_task_4(request):
    if not request.session.get('is_admin', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    request.session['task_type'] = 4
    user = models.User.objects.get(name=request.session['username'])
    User_list = mark_safe(get_img_namelist())
    print(User_list)
    return render(request, 'release_task_2.html', locals())


def release_task(request):
    print("trun to release_task")
    if not request.session.get('is_admin', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    if not request.session.get('task_type', None):
        return redirect("/choose/")

    if request.method == "POST":

        print(request.POST)
        #print(request.FILES)

        task_form = forms.TaskForm(request.POST, request.FILES)
        if not task_form.is_valid():
         #   print('sssssssssssssssssssssssssss')
            messages.error(request, "表单信息有误，请重新填写！")
            return release_task_x(request)

        files = request.FILES.getlist('files')  # exception
        print(files)
        # print(type(files))

        template = task_form.cleaned_data['template']
        
        #文件类型判断
        for f in files:
            ftype = f.content_type.split('/')[0]
            print(ftype)
            if (template=='1' and ftype!='image') or (template=='2' and ftype!='video') or (template=='3' and ftype!='audio') or (ftype is None):
                messages.error(request, "文件类型有误，请重新提交！")
                return release_task_x(request)

       # print(template)
        name = task_form.cleaned_data['name']
        details = task_form.cleaned_data['details']
        employees_num = task_form.cleaned_data['employees_num']
        # print('employees_num', employees_num, type(employees_num))
        credit = task_form.cleaned_data['credit']
        user_level = task_form.cleaned_data['user_level']
        if not user_level:
            user_level = 0
        current_user = models.User.objects.get(name=request.session['username'])
        if current_user.total_credits < credit * employees_num * len(files):
            messages.error(request, "您的信用积分不足，请先进行充值！")
            return recharge(request)

        new_task = models.Task.objects.create()
        new_task.type = request.session['task_type']
        print(new_task.type)
        new_task.name = name
        new_task.admin = current_user
        new_task.template = int(template)
        new_task.details = details
        new_task.max_tagged_num = employees_num
        new_task.credit = credit
        new_task.user_level = user_level
        # save questions and answers
        i = 1
        content = ''
        if(len(new_task.name)>15):
            messages.error(request, "任务名超长，请控制在15个字以内！")
            return release_task_x(request)
        while 'q' + str(i) in request.POST:
            question = request.POST.get('q' + str(i))
            if len(question) <= 0 or len(question) > 128:
                messages.error(request, "表单信息有误，请重新填写！")
                return release_task_x(request)
            content += '|' + question
            j = 1
            while 'a' + str(j) + '_q' + str(i) in request.POST:
                choice = request.POST.get('a' + str(j) + '_q' + str(i))
                if len(choice) <= 0 or len(choice) > 128:
                    messages.error(request, "表单信息有误，请重新填写！")
                    return release_task_x(request)
                content += '&' + choice
                j += 1
            i += 1
        new_task.content = content
        new_task.save()

        i = 1
        while 'm' + str(i) in request.POST:
            member = request.POST.get('m' + str(i))
            if member!='':
                if not models.User.objects.filter(name=member).exists():
                    messages.error(request, member + "用户不存在")
                else:
                    u = models.User.objects.filter(name=member).first()
                    u.tasks_to_examine.add(new_task)
                    u.save()
            i += 1

        # save images
        for f in files:
            sub_task = models.SubTask.objects.create()
            sub_task.file = f
            sub_task.task = new_task
            sub_task.save()

        #附件存储
        otherfiles = request.FILES.getlist('other_files', None)
        if len(otherfiles) != 0:
            for of in otherfiles:
                of_path = default_storage.save('task_'+str(new_task.id)+'/otherfiles1/'+of.name,ContentFile(of.read()))

            zipfile_name = new_task.name + "-附件.zip"
            os.mkdir(os.path.join(settings.MEDIA_ROOT, "task_" + str(new_task.id), "otherfiles"))
            zip_path = os.path.join(settings.MEDIA_ROOT, "task_" + str(new_task.id), "otherfiles",zipfile_name)

            z = zipfile.ZipFile(zip_path, 'w', zipfile.ZIP_DEFLATED)
            ziproot = os.path.join(settings.MEDIA_ROOT, "task_" + str(new_task.id), "otherfiles1")
            for parent, dirnames, filenames in os.walk(ziproot):
                for file in filenames:
                    z.write(ziproot + os.sep + file, file)
            z.close()

            shutil.rmtree(os.path.join(settings.MEDIA_ROOT, "task_" + str(new_task.id), "otherfiles1"))
        # print(template)
        # if(new_task.template==1):
        #     imagelist = os.listdir('./media/task_' + str(new_task.id))
        #     illegallist = []
        #     for f in imagelist:
        #         legal = nsfw_predict.predict(f, 'media/task_' + str(new_task.id))
        #         if (legal == 1):
        #             illegallist.append('../media/task_' + str(new_task.id) + '/' + str(f))
        #     #print('../media/task_' + str(new_task.id)+'/'+str(f))
        #     if (legal == 1):
        #         return render(request, 'check_pic.html', locals())

        current_user.total_credits -= credit * employees_num * len(files)
        current_user.save()

        del request.session['task_type']
        request.session['new_task_id'] = new_task.id
        if new_task.template == 1:
            return redirect('/confirm_to_upload_pictures/')
        elif new_task.template == 2 and new_task.type == 4:
            return redirect('/video2pictures_slide/')
        else:
            del request.session['new_task_id']
            messages.success(request, "任务发布成功！")
            return redirect('/all_task/')

    return release_task_x(request)


def release_task_x(request):
    if request.session['task_type'] == 1:
        return redirect('/release_task_1/')
    elif request.session['task_type'] == 2:
        return redirect('/release_task_2/')
    elif request.session['task_type'] == 3:
        return redirect('/release_task_3/')
    elif request.session['task_type'] == 4:
        return redirect('/release_task_4/')
    return redirect('/all_task/')


def video2pictures_slide(request):
    if not request.session.get('is_admin', None) or not request.session.get('new_task_id', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    task_id = request.session['new_task_id']
    task = models.Task.objects.filter(pk=task_id).first()
    if not task or task.type != 4 or task.template != 2:
        messages.error(request, '页面已过期！')
        return redirect("/all_task/")

    if request.method == "POST":
        # print(request.POST)
        if 'frame' in request.POST and 'frame_interval' in request.POST:
            if not digit.match(request.POST.get('frame_interval')):
                messages.error(request, "请输入合法的帧数间隔！")
            else:
                frame_interval = int(request.POST.get('frame_interval'))
                tools.video2pictures(task, frame_interval)
                request.session['frame'] = frame_interval
        elif 'confirm' in request.POST:
            if not request.session.get('frame', None):
                messages.error(request, "请先输入合法的帧数间隔！")
            else:
                del request.session['new_task_id']
                del request.session['frame']
                messages.success(request, "任务发布成功！")
                return redirect("/all_task/")
        elif 'return' in request.POST:
            current_user = models.User.objects.get(name=request.session['username'])
            current_user.released_tasks.filter(pk=task_id).delete()
            del request.session['new_task_id']
            del request.session['frame']
            return redirect("/all_task/")
        elif 'abandon' in request.POST and digit.match(request.POST.get('abandon')):
            screenshot = models.Screenshot.objects.filter(id=int(request.POST.get('abandon'))).first()
            if screenshot and screenshot.sub_task.task.id == task_id:
                screenshot.delete()

    return render(request, 'video2pictures_slide.html', locals())


def confirm_to_upload_pictures(request):
    if not request.session.get('is_admin', None) or not request.session.get('new_task_id', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    task_id = request.session['new_task_id']
    task = models.Task.objects.filter(pk=task_id).first()
    if not task or task.template != 1:
        messages.error(request, '页面已过期！')
        return redirect("/all_task/")

    if request.method == "POST":
        # print(request.POST)
        if 'abandon' in request.POST and digit.match(request.POST.get('abandon')):
            sub_task_id = int(request.POST.get('abandon'))
            task.subtask_set.filter(pk=sub_task_id).delete()
            current_user = models.User.objects.get(name=request.session['username'])
            current_user.total_credits += task.credit * task.max_tagged_num
            current_user.save()
        elif 'confirm' in request.POST:
            del request.session['new_task_id']
            messages.success(request, "任务发布成功！")
            return redirect("/all_task/")

    sub_tasks = task.subtask_set.all()
    return render(request, 'confirm_to_upload_pictures.html', locals())

def getDic1(request):
    if not request.session.get('is_admin', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    username = request.session['username']

    dic = {}
    users = models.User.objects.all()
    for usr in users:
        name = usr.name
        tasks_names=[]
        tasks_1=usr.claimed_tasks.all()
        tasks_2=usr.favorite_tasks.all()
        tu = models.TaskUser.objects.filter(user=usr, status__in=('grabbed','grabbing'))
        for unit in tasks_1:
            #if unit.name not in tasks_names:
            tasks_names.append(unit.name)
        for unit in tasks_2:
            #if unit.name not in tasks_names:
            tasks_names.append(unit.name)
        for unit in tu:
            #if unit.task.name not in tasks_names:
            tasks_names.append(unit.task.name)
        dic[name] = tasks_names
    return dic

def getDic2(request):
    if not request.session.get('is_admin', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    username = request.session['username']

    dic = []
    users = models.User.objects.all()
    for usr in users:
        name = usr.name
        tasks_1=usr.claimed_tasks.all()
        tasks_2=usr.favorite_tasks.all()
        tu = models.TaskUser.objects.filter(user=usr, status__in=('grabbed','grabbing'))
        for unit in tasks_1:
            #if unit.name not in tasks_names:
            str = name + ',1,'
            str += unit.name
            dic.append(str)
        for unit in tasks_2:
            #if unit.name not in tasks_names:
            str = name + ',1,'
            str += unit.name
            dic.append(str)
        for unit in tu:
            #if unit.task.name not in tasks_names:
            str = name + ',1,'
            str += unit.task.name
            dic.append(str)
    return dic

def getDic3(request):
    if not request.session.get('is_admin', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    username = request.session['username']

    dic = {}
    users = models.User.objects.all()
    for usr in users:
        name = usr.name
        tasks_types=[]
        tasks_1=usr.claimed_tasks.all()
        tasks_2=usr.favorite_tasks.all()
        tu = models.TaskUser.objects.filter(user=usr, status__in=('grabbed','grabbing'))
        for unit in tasks_1:
            #if unit.name not in tasks_names:
            tasks_types.append(str(unit.type))
        for unit in tasks_2:
            #if unit.name not in tasks_names:
            tasks_types.append(str(unit.type))
        for unit in tu:
            #if unit.task.name not in tasks_names:
            tasks_types.append(str(unit.task.type))
        dic[name] = tasks_types
    return dic

def getDic4(request):
    if not request.session.get('is_admin', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    username = request.session['username']

    dic = []
    users = models.User.objects.all()
    for usr in users:
        name = usr.name
        tasks_1=usr.claimed_tasks.all()
        tasks_2=usr.favorite_tasks.all()
        tu = models.TaskUser.objects.filter(user=usr, status__in=('grabbed','grabbing'))
        for unit in tasks_1:
            #if unit.name not in tasks_names:
            string = name + ',1,'
            string += str(unit.type)
            dic.append(string)
        for unit in tasks_2:
            #if unit.name not in tasks_names:
            string = name + ',1,'
            string += str(unit.type)
            dic.append(string)
        for unit in tu:
            #if unit.task.name not in tasks_names:
            string = name + ',1,'
            string += str(unit.task.type)
            dic.append(string)
    return dic


# api 提出的一个功能函数
def get_task_list(request):
    task_list = models.Task.objects.all()
    temp_excluded_list = []
    if request.method == "POST":
        print(request.POST)
        if 'task_sort' in request.POST or 'task_filter' in request.POST or 'task_value' in request.POST:
            temp_excluded_list = request.POST.getlist('temp_excluded')
            if 'temp1' in temp_excluded_list:
                task_list = task_list.exclude(template=1, type=1).exclude(template=1, type=2)
            if 'temp2' in temp_excluded_list:
                task_list = task_list.exclude(template=1, type=3)
            if 'temp3' in temp_excluded_list:
                task_list = task_list.exclude(template=1, type=4)
            if 'temp4' in temp_excluded_list:
                task_list = task_list.exclude(template=3, type=1).exclude(template=3, type=2)
            if 'temp5' in temp_excluded_list:
                task_list = task_list.exclude(template=3, type=3)
            if 'temp6' in temp_excluded_list:
                task_list = task_list.exclude(template=2, type=1).exclude(template=2, type=2)
            if 'temp7' in temp_excluded_list:
                task_list = task_list.exclude(template=2, type=3)
            if 'temp8' in temp_excluded_list:
                task_list = task_list.exclude(template=2, type=4)
            print(task_list)
            if request.POST.get('tagged_num') == 'single':
                task_list = task_list.filter(max_tagged_num=1)
            elif request.POST.get('tagged_num') == 'multi':
                task_list = task_list.exclude(max_tagged_num=1)
            if request.POST.get('order') == 'time_desc':
                task_list = task_list.order_by('-c_time')
            elif request.POST.get('order') == 'num_asc':
                task_list = task_list.order_by('max_tagged_num')
            elif request.POST.get('order') == 'num_desc':
                task_list = task_list.order_by('-max_tagged_num')
            # 筛选任务积分高于某值的任务
            if request.POST.get('value') != '':
                print("value:" + request.POST.get('value'))
                if not request.POST.get('value').isdigit():
                    messages.error(request, "value 中包含非数字")
                else:
                    task_list = task_list.filter(credit__gt=int(request.POST.get('value'))).order_by('-credit')
            # 任务关键词筛选
            if request.POST.get('KeyWord') != '':
                print("Key:" + request.POST.get('KeyWord'))
                task_list = task_list.filter(name__contains=request.POST.get('KeyWord'))

    return task_list


def all_task(request):
    if not request.session.get('is_admin', None):
        messages.error(request, "页面已过期！请重新登录")
        return redirect("/login/")
    username = request.session['username']
    dic1 = getDic1(request)
    dic2 = getDic2(request)
    # dic3 = getDic3(request)
    # dic4 = getDic4(request)
    Last_Rank_list=[]
    Last_Rank2_list=[]
    print(dic1)
    W3 = user.Usersim(dic1)
    Last_Rank = user.Recommend(username, dic1, W3, 3)
    for key in Last_Rank:
        if key not in Last_Rank_list:
            Last_Rank_list.append(key)
    data = itempre.loadData(dic2)
    W = itempre.similarity(data)
    Last_Rank2 = itempre.recommandList(data,W,username,3,10)
    for item in Last_Rank2:
        if item[0] not in Last_Rank2_list:
            Last_Rank2_list.append(item[0])

    FinalRecommand = list(set(Last_Rank_list).union(set(Last_Rank2_list)))
    recommand_tasks = []
    for name in FinalRecommand:
        recommand_tasks += models.Task.objects.filter(name=name)


    task_list = models.Task.objects.all()
    num_task = task_list.count()
    num_user = models.User.objects.count()
    task_templates = ['', '图片', '视频', '音频']
    task_types = ['', '单选式', '多选式', '问答式', '标注式']
    temp_excluded_list = []
    if request.method == "POST":
        print(request.POST)
        # 任务关键词筛选
        if 'task_keyword' in request.POST:
            if request.POST.get('KeyWord') != '':
                print("Key:" + request.POST.get('KeyWord'))
                task_list = task_list.filter(name__contains=request.POST.get('KeyWord'))

        if  'task_filter' in request.POST:
            if request.POST.get('task_temp1') == 'all' and request.POST.get('task_temp2') == '-1':
                pass
            elif request.POST.get('task_temp1') == 'all' and request.POST.get('task_temp2') == '0':
                task_list = task_list.filter(Q(type=1)|Q(type=2))
            elif request.POST.get('task_temp1') == 'all' and request.POST.get('task_temp2') == '1':
                task_list = task_list.filter(type=3)
            elif request.POST.get('task_temp1') == 'all' and request.POST.get('task_temp2') == '2':
                task_list = task_list.filter(type=4)
            elif request.POST.get('task_temp1') == 'pic' and request.POST.get('task_temp2') == '-1':
                task_list = task_list.filter(template=1)
            elif request.POST.get('task_temp1') == 'pic' and request.POST.get('task_temp2') == '0':
                task_list = task_list.filter(Q(type=1)|Q(type=2),template=1)
            elif request.POST.get('task_temp1') == 'pic' and request.POST.get('task_temp2') == '1':
                task_list = task_list.filter(template=1, type=3)
            elif request.POST.get('task_temp1') == 'aud' and request.POST.get('task_temp2') == '-1':
                task_list = task_list.filter(template=3)    
            elif request.POST.get('task_temp1') == 'aud' and request.POST.get('task_temp2') == '0':
                task_list = task_list.filter(Q(type=1)|Q(type=2),template=3)
            elif request.POST.get('task_temp1') == 'aud' and request.POST.get('task_temp2') == '1':
                task_list = task_list.filter(template=3, type=3)
            elif request.POST.get('task_temp1') == 'ved' and request.POST.get('task_temp2') == '-1':
                task_list = task_list.filter(template=2)    
            elif request.POST.get('task_temp1') == 'ved' and request.POST.get('task_temp2') == '0':
                task_list = task_list.filter(Q(type=1)|Q(type=2),template=2)
            elif request.POST.get('task_temp1') == 'ved' and request.POST.get('task_temp2') == '1':
                task_list = task_list.filter(template=2, type=3)
            elif request.POST.get('task_temp1') == 'ved' and request.POST.get('task_temp2') == '2':
                task_list = task_list.filter(template=2, type=4)
            # print(task_list)
            if request.POST.get('tagged_num') == 'single':
                task_list = task_list.filter(max_tagged_num=1)
            elif request.POST.get('tagged_num') == 'multi':
                task_list = task_list.exclude(max_tagged_num=1)
            if request.POST.get('order') == 'time_desc':
                task_list = task_list.order_by('-c_time')
            elif request.POST.get('order') == 'time_asc':
                task_list = task_list.order_by('c_time')
            elif request.POST.get('order') == 'num_asc':
                task_list = task_list.order_by('max_tagged_num')
            elif request.POST.get('order') == 'num_desc':
                task_list = task_list.order_by('-max_tagged_num')
            # 筛选任务积分高于某值的任务
            if request.POST.get('task_value') == '11':
                task_list = task_list.filter(credit__gt=0,credit__lt=11).order_by('-credit')
            elif request.POST.get('task_value') == '21':
                task_list = task_list.filter(credit__gt=10,credit__lt=21).order_by('-credit')
            elif request.POST.get('task_value') == '31':
                task_list = task_list.filter(credit__gt=20,credit__lt=31).order_by('-credit')
            elif request.POST.get('task_value') == '41':
                task_list = task_list.filter(credit__gt=30,credit__lt=41).order_by('-credit')
            elif request.POST.get('task_value') == '51':
                task_list = task_list.filter(credit__gt=40,credit__lt=51).order_by('-credit')
            elif request.POST.get('task_value') == '50':
                task_list = task_list.filter(credit__gt=50).order_by('-credit')

    if not request.session.get('is_login', None):
        return render(request, 'all_task.html', locals())

    current_user = models.User.objects.get(name=request.session['username'])
    rank = models.User.objects.filter(num_label_accepted__gt=current_user.num_label_accepted).count() + 1

    #邀请通话人员名单
    callers = current_user.callers.split('|')
    callers.remove('')
    callers_num = len(callers)

    if request.method == "POST":
        #接受聊天邀请
        if 'chat_in' in request.POST:
            caller_name = request.POST.get('chat_in')
            all_callers = current_user.callers.split('|')
            all_callers.remove(caller_name)
            current_user.callers = '|'.join(all_callers)
            current_user.save()
        if 'chat_req' in request.POST:
            admin_name = request.POST.get('chat_req')
            task_admin = models.User.objects.filter(name = admin_name).first()
            task_admin.callers = task_admin.callers + current_user.name + '|'
            task_admin.save()
        # print(request.POST)
        if 'collect' in request.POST:
            collect_task(request, current_user)
        elif 'remove' in request.POST:
            remove_task(request, current_user)
        elif 'enter' in request.POST:
            if digit.match(request.POST.get('enter')):
                request.session['task_id'] = int(request.POST.get('enter'))
                return redirect('/enter_task/')
        elif 'cancel_tasks' in request.POST:
            cancel_task(request)
            task_list = models.Task.objects.all()
            num_task = task_list.count()
        elif 'review' in request.POST and digit.match(request.POST.get('review')):
            request.session['task_id'] = int(request.POST.get('review'))
            return redirect('/one_task/')
        elif 'close_task' in request.POST and digit.match(request.POST.get('close_task')):
            task_id = int(request.POST.get('close_task'))
            task = current_user.released_tasks.filter(id=task_id).first()
            if task:
                task.is_closed = True
                task.save()

        elif 'redo_unreviewed_task' in request.POST and digit.match(request.POST.get('redo_unreviewed_task')):
            request.session['task_id'] = int(request.POST.get('redo_unreviewed_task'))
            task_user = models.TaskUser.objects.filter(task_id=request.session['task_id'],
                                                       user=current_user, status='unreviewed').first()
            if not task_user:
                return redirect('/enter_task/')
            task_user.status = 'doing'
            task_user.label_set.filter(status='unreviewed').update(status='untagged')
            task_user.save()
            return redirect('/enter_task/')
        elif 'abandon_unreviewed_task' in request.POST and digit.match(request.POST.get('abandon_unreviewed_task')):
            task_id = int(request.POST.get('abandon_unreviewed_task'))
            task_user = models.TaskUser.objects.filter(Q(status='doing') | Q(status='unreviewed'), task_id=task_id,
                                                       user=current_user).first()
            if not task_user:
                return redirect('/enter_task/')
            task_user.status = 'abandoned'
            task_user.label_set.filter(status='unreviewed').update(status='untagged')
            task_user.task.num_worker -= 1
            task_user.task.save()
            task_user.save()
        elif 'redo_rejected_task' in request.POST and digit.match(request.POST.get('redo_rejected_task')):
            request.session['task_id'] = int(request.POST.get('redo_rejected_task'))
            task_user = models.TaskUser.objects.filter(task_id=request.session['task_id'],
                                                       user=current_user, status='rejected').first()
            if not task_user:
                return redirect('/enter_task/')
            rejected_labels = task_user.label_set.filter(status='rejected')
            task_user.num_label_unreviewed += rejected_labels.count()
            task_user.num_label_rejected = 0
            task_user.status = 'redoing'
            task_user.save()
            rejected_labels.update(status='untagged')
            return redirect('/enter_task/')

        elif 'abandon_rejected_task' in request.POST and digit.match(request.POST.get('abandon_rejected_task')):
            task_id = int(request.POST.get('abandon_rejected_task'))
            task_user = models.TaskUser.objects.filter(Q(status='rejected') | Q(status='redoing'), task_id=task_id,
                                                       user=current_user).first()
            if not task_user:
                return redirect('/enter_task/')
            rejected_labels = task_user.label_set.filter(status='rejected')
            task_user.num_label_unreviewed += rejected_labels.count()
            task_user.num_label_rejected = 0
            task_user.status = 'abandoned'
            task_user.task.num_worker -= 1
            task_user.task.save()
            task_user.save()
            rejected_labels.update(status='untagged')

        elif 'take_a_position' in request.POST and digit.match(request.POST.get('take_a_position')):
            task_id = int(request.POST.get('take_a_position'))
            grab_task(request, current_user, task_id)

    favorite_task_list = current_user.favorite_tasks.all()
    num_favorite_task = favorite_task_list.count()

    released_task_list = current_user.released_tasks.all()
    num_released_task = released_task_list.count()

    rejected_task_list = current_user.claimed_tasks.filter(
        Q(taskuser__status='rejected') | Q(taskuser__status='redoing'), taskuser__user=current_user).distinct()
    num_rejected_task = rejected_task_list.count()

    unreviewed_task_list = current_user.claimed_tasks.filter(taskuser__status='unreviewed',
                                                             taskuser__user=current_user).distinct()
    num_unreviewed_task = unreviewed_task_list.count()

    get_position_task_list = current_user.claimed_tasks.filter(taskuser__status='doing', taskuser__has_grabbed=True,
                                                               taskuser__user=current_user).distinct()

    # 审核组后台数据获取
    Tasks_to_examine = current_user.tasks_to_examine.filter(taskuser__num_label_unreviewed__gt=0).distinct()
    num_tasks_to_examine = Tasks_to_examine.count()

    current_user.login_time = timezone.now()
    current_user.save()
    num_updated_task = models.Task.objects.filter(c_time__gt=current_user.last_login_time).count()
    label_accepted_new = current_user.label_set.filter(status='accepted',
                                                       m_time__gt=current_user.last_login_time)
    num_label_accepted_new = label_accepted_new.count()
    credits_new = 0
    for label in label_accepted_new:
        credits_new += label.sub_task.task.credit
    return render(request, 'all_task.html', locals())


def grab_task(request, current_user, task_id):
    task = models.Task.objects.filter(id=task_id).first()
    if not task:
        messages.error(request, '任务不存在！')
        return

    level_list = [0, 0, 200, 500, 1000, 2000]
    if current_user.num_label_accepted < level_list[task.user_level]:
        messages.error(request, '您的等级不足，无法抢该任务！')
        return
    if task.is_closed:
        messages.error(request, '该任务已关闭！')
        return

    if task.num_worker < task.max_tagged_num:
        messages.error(request, '该任务未满员，可直接进入！')
        return

    task_user = models.TaskUser.objects.filter(task=task, user=current_user).first()
    if task_user:
        if task_user.status == 'doing' or task_user.status == 'redoing':
            messages.error(request, '您可以直接进入该任务！')
            return
        elif task_user.status == 'grabbing':
            messages.success(request, '已为您预约抢位！')
            return
        else:
            messages.error(request, '您已经做过该任务！')
            return

    task_user = models.TaskUser.objects.filter(task_id=task_id, status='rejected').first()
    if task_user:
        task_user.status = 'grabbed'
        task_user.save()
        rejected_labels = task_user.label_set.filter(status='rejected')
        new_task_user = models.TaskUser.objects.create(task=task, user=current_user, status='doing', has_grabbed=True,
                                                       num_label_unreviewed=rejected_labels.count())
        rejected_labels.update(user=current_user, task_user=new_task_user, status='untagged')
        messages.success(request, '抢位成功！')
    else:
        models.TaskUser.objects.create(task=task, user=current_user, status='grabbing')
        messages.success(request, '已为您预约抢位！')


def collect_task(request, current_user):
    if not request.session.get('is_login', None) or not digit.match(request.POST.get('collect')):
        messages.error(request, '用户未登录！')
        return
    task_id = int(request.POST.get('collect'))
    task = models.Task.objects.filter(pk=task_id).first()
    if not task:
        messages.error(request, '该任务不存在！')
        return
    current_user.favorite_tasks.add(task)


def remove_task(request, current_user):
    if not request.session.get('is_login', None):
        messages.error(request, '用户未登录！')
        return
    task_id_list = request.POST.getlist('removed_task_id_list')
    for task_id in task_id_list:
        if not digit.match(task_id):
            continue
        task = current_user.favorite_tasks.filter(id=task_id).first()
        if task:
            current_user.favorite_tasks.remove(task)


def cancel_task(request):
    if not request.session.get('is_admin', None):
        return
    current_user = models.User.objects.get(name=request.session['username'])
    task_id_list = request.POST.getlist('canceled_task_id_list')
    for task_id in task_id_list:
        if not digit.match(task_id):
            # print('该task_id不合法！')
            continue
        task_id = int(task_id)
        task = current_user.released_tasks.filter(pk=task_id).first()
        if task:
            task.is_closed = True
            task.save()
        # task = current_user.released_tasks.filter(pk=task_id).first()
        # if not task:
        #     print('该任务不存在！')
        #     continue
        # task.delete()
        # task.is_closed = True
        # task.save()


def enter_task(request):
    if not request.session.get('is_login', None) or not request.session.get('task_id', None):
        messages.error(request, '用户未登录！')
        return redirect('/all_task/')
    current_user = models.User.objects.get(name=request.session['username'])
    task = models.Task.objects.filter(id=request.session['task_id']).first()
    if not task:
        messages.error(request, '任务不存在！')
        return redirect('/all_task/')

    level_list = [0, 0, 200, 500, 1000, 2000]
    if task.is_closed:
        messages.error(request, '该任务已关闭！')
        return redirect('/all_task/')
    if current_user.num_label_accepted < level_list[task.user_level]:
        messages.error(request, '您的等级不足，无法进入该任务！')
        return redirect('/all_task/')

    task_user = models.TaskUser.objects.filter(task=task, user=current_user).first()
    if not task_user:
        if task.num_worker >= task.max_tagged_num:
            messages.error(request, '该任务已满员，无法进入！')
            return redirect('/all_task/')
        task_user = models.TaskUser.objects.create(task=task, user=current_user)
        task.num_worker += 1
        task.save()
        abandoned_task_user = task.taskuser_set.filter(status='abandoned').first()
        if abandoned_task_user:
            abandoned_labels = abandoned_task_user.label_set.filter(status='untagged')
            task_user.num_label_unreviewed = abandoned_labels.count()
            task_user.save()
            abandoned_task_user.status = 'gotten'
            abandoned_task_user.save()
            abandoned_labels.update(task_user=task_user, user=current_user)
        else:
            sub_task_set = task.subtask_set.all()
            task_user.num_label_unreviewed = sub_task_set.count()
            task_user.save()
            for sub_task in sub_task_set:
                models.Label.objects.create(sub_task=sub_task, task_user=task_user, user=current_user)
    if task_user.status != 'doing' and task_user.status != 'redoing':
        if task_user.status == 'grabbing':
            messages.error(request, '已为您预约抢位！')
            return redirect('/all_task/')
        else:
            print("my test:", task_user.status)
            messages.error(request, '您已经做过该任务！')
            return redirect('/all_task/')

    # task_templates = ['', '图片', '视频', '音频']
    # task_types = ['', '单选式', '多选式', '问答式', '标注式']
    if task.template == 1:
        return picture_task(request, current_user, task, task_user)
    elif task.template == 2:
        return video_task(request, current_user, task, task_user)
    elif task.template == 3:
        return player_task(request, current_user, task, task_user)
    return redirect('/all_task/')

def get_qa_list(task):
    qa_list = []
    contents = task.content.split('|')
    for item in contents[1:]:
        qa = item.split('&')
        qa_list.append({'question': qa[0], 'answers': qa[1:]})
    return qa_list

def picture_task(request, current_user, task, task_user):
    if request.method == "POST":
        # print(request.POST)
        result = ''
        if task.type != 4:
            i = 1
            while 'q' + str(i) in request.POST:
                result += '|' + 'q' + str(i)
                answers = request.POST.getlist('q' + str(i))
                # print(answers)
                for answer in answers:
                    result += '&' + answer
                i += 1
        else:
            result = request.POST.get('position').replace('\r\n', '|')

        label_id = request.session.get('label_id', None)
        if result == '' and task.type == 2:
            messages.error(request, '请至少选择一项结果！')
        elif label_id:
            label = models.Label.objects.get(pk=label_id)
            print(label.sub_task)
            label.status = 'unreviewed'
            label.result = result
            label.save()
            if task.type == 4:
                tools.picture_circle(label)
            del request.session['label_id']

    label = task_user.label_set.filter(status='untagged').first()
    if not label:
        messages.success(request, "该任务已完成！")
        task_user.status = 'unreviewed'
        task_user.save()
        del request.session['task_id']
        return redirect('/all_task/')
    request.session['label_id'] = label.id
    sub_task = label.sub_task

    qa_list = []
    contents = task.content.split('|')
    for item in contents[1:]:
        qa = item.split('&')
        qa_list.append({'question': qa[0], 'answers': qa[1:]})

    if task.type == 1:
        return render(request, 'picture_task.html', locals())
    elif task.type == 2:
        return render(request, 'picture_task_multi_choice.html', locals())
    elif task.type == 3:
        return render(request, 'picture_task_qa.html', locals())
    else:
        return render(request, 'picture_circle.html', locals())


def video_task(request, current_user, task, task_user):
    if request.method == "POST":
        # print(request.POST)
        result = ''
        if task.type != 4:
            i = 1
            while 'q' + str(i) in request.POST:
                result += '|' + 'q' + str(i)
                answers = request.POST.getlist('q' + str(i))
                # print(answers)
                for answer in answers:
                    result += '&' + answer
                i += 1
        else:
            result = request.POST.get('position').replace('\r\n', '|')

        label_id = request.session.get('label_id', None)
        if result == '' and task.type == 2:
            messages.error(request, '请至少选择一项结果！')
        elif label_id:
            label = models.Label.objects.get(pk=label_id)
            print(label.sub_task)
            label.status = 'unreviewed'
            label.result = result
            label.save()
            if task.type == 4:
                tools.video_circle(label)
            del request.session['label_id']

    label = task_user.label_set.filter(status='untagged').first()
    if not label:
        messages.success(request, "该任务已完成！")
        task_user.status = 'unreviewed'
        task_user.save()
        del request.session['task_id']
        return redirect('/all_task/')
    request.session['label_id'] = label.id
    sub_task = label.sub_task

    qa_list = []
    contents = task.content.split('|')
    for item in contents[1:]:
        qa = item.split('&')
        qa_list.append({'question': qa[0], 'answers': qa[1:]})

    if task.type == 1:
        return render(request, 'video_task.html', locals())
    elif task.type == 2:
        return render(request, 'video_task_multi_choice.html', locals())
    elif task.type == 3:
        return render(request, 'video_task_qa.html', locals())
    else:
        img_name_list = ''
        for screen_shot in sub_task.screenshot_set.all():
            img_name_list += '#' + screen_shot.image.name.split('\\')[-1]
        return render(request, 'video_circle.html', locals())


def player_task(request, current_user, task, task_user):
    if request.method == "POST":
        # print(request.POST)
        i = 1
        result = ''
        while 'q' + str(i) in request.POST:
            result += '|' + 'q' + str(i)
            answers = request.POST.getlist('q' + str(i))
            # print(answers)
            for answer in answers:
                result += '&' + answer
            i += 1

        label_id = request.session.get('label_id', None)
        if result == '' and task.type == 2:
            messages.error(request, '请至少选择一项结果！')
        elif label_id:
            label = models.Label.objects.get(pk=label_id)
            print(label.sub_task)
            label.status = 'unreviewed'
            label.result = result
            label.save()
            if task.type == 4:
                tools.video_circle(label)
            del request.session['label_id']

    label = task_user.label_set.filter(status='untagged').first()
    if not label:
        messages.success(request, "该任务已完成！")
        task_user.status = 'unreviewed'
        task_user.save()
        del request.session['task_id']
        return redirect('/all_task/')
    request.session['label_id'] = label.id
    sub_task = label.sub_task



    qa_list = []
    contents = task.content.split('|')
    for item in contents[1:]:
        qa = item.split('&')
        qa_list.append({'question': qa[0], 'answers': qa[1:]})

    wav_content = []
    s='.'+sub_task.file.url
    print(s)
    if s in request.session:
        wav_content.append(request.session[s])
    elif (s.endswith('.wav') or s.endswith('.amr') or  s.endswith('.pcm') ):
        a = baiduapi.api_use('.'+sub_task.file.url)
        wav_content.append(baiduapi.api_use('.'+sub_task.file.url))
        request.session[s]=baiduapi.api_use('.'+sub_task.file.url)
    else:
        wav_content = ["不支持的格式"]
    if task.type == 1:
        return render(request, 'player_task.html', locals())
    elif task.type == 2:
        return render(request, 'player_task_multi_choice.html', locals())
    else:
        return render(request, 'player_task_qa.html', locals())


def reject_label(label, task):
    label.status = 'rejected'
    task_user = label.task_user
    task_user.num_label_unreviewed -= 1
    task_user.num_label_rejected += 1
    if task_user.num_label_unreviewed == 0:
        new_task_user = task.taskuser_set.filter(status='grabbing').first()
        if new_task_user:
            rejected_labels = task_user.label_set.filter(status='rejected')
            rejected_labels.update(user=new_task_user.user, task_user=new_task_user, status='untagged')
            new_task_user.status = 'doing'
            new_task_user.has_grabbed = True
            new_task_user.num_label_unreviewed = rejected_labels.count()
            new_task_user.save()
            task_user.status = 'grabbed'
        else:
            task_user.status = 'rejected'
    task_user.save()
    label.save()


def accept_label(label, task):
    label.status = 'accepted'
    label.user.total_credits += label.sub_task.task.credit
    label.user.num_label_accepted += 1
    label.user.save()
    task_user = label.task_user
    task_user.num_label_unreviewed -= 1
    if task_user.num_label_unreviewed == 0:
        if task_user.num_label_rejected > 0:
            new_task_user = task.taskuser_set.filter(status='grabbing').first()
            if new_task_user:
                rejected_labels = task_user.label_set.filter(status='rejected')
                rejected_labels.update(user=new_task_user.user, task_user=new_task_user, status='untagged')
                new_task_user.status = 'doing'
                new_task_user.has_grabbed = True
                new_task_user.num_label_unreviewed = rejected_labels.count()
                new_task_user.save()
                task_user.status = 'grabbed'
            else:
                task_user.status = 'rejected'
        else:
            task_user.status = 'accepted'
    task_user.save()
    label.save()


def check_task(request):
    if not request.session.get('is_admin', None) or not request.session.get('task_id', None) or not request.session.get(
            'sub_task_id', None):
        return redirect('/all_task/')
    current_user = models.User.objects.get(name=request.session['username'])
    task = current_user.released_tasks.filter(id=request.session['task_id']).first()
    if not task:
        return redirect('/all_task/')
    sub_task = task.subtask_set.filter(id=request.session['sub_task_id']).first()
    if not sub_task:
        return redirect('/all_task/')

    if request.method == "POST":
        # print(request.POST)
        if 'pass' in request.POST:
            if not digit.match(request.POST.get('pass')):
                messages.error(request, '该label_id不合法！')
                return redirect('/all_task/')
            label = sub_task.label_set.filter(pk=request.POST.get('pass')).first()
            if not label:
                messages.error(request, '该标签不存在！')
                return redirect('/all_task/')
            accept_label(label, task)
        elif 'back' in request.POST:
            if not digit.match(request.POST.get('back')):
                messages.error(request, '该label_id不合法！')
                return redirect('/all_task/')
            label = sub_task.label_set.filter(pk=request.POST.get('back')).first()
            if not label:
                messages.error(request, '该标签不存在！')
                return redirect('/all_task/')
            reject_label(label, task)
        elif 'detail' in request.POST and task.type == 4:
            request.session['label_id'] = int(request.POST.get('detail'))
            return redirect('/picture_detail/')
        elif 'pass_all' in request.POST:
            label_list = sub_task.label_set.filter(status='unreviewed')
            for label in label_list:
                accept_label(label, task)

    label_list = sub_task.label_set.exclude(status='untagged')
    # print(label_list)
    qa_list = []
    contents = task.content.split('|')

    if task.type == 1 or task.type == 2:
        for i, item in enumerate(contents[1:]):
            qa = item.split('&')
            answers = []
            for ans in qa[1:]:
                answers.append([ans, 0])
            for label in label_list:
                ans_list = label.result.split('|')[i + 1].split('&')[1:]
                for ans in ans_list:
                    answers[int(ans) - 1][1] += 1
            qa_list.append({'question': qa[0], 'answers': answers})
        return render(request, 'choice_questions_result.html', locals())
    elif task.type == 3:
        return render(request, 'qa_result.html', locals())
    else:
        return render(request, 'picture_result.html', locals())


def picture_detail(request):
    if not request.session.get('is_admin', None) or \
            not request.session.get('task_id', None) or \
            not request.session.get('sub_task_id', None) or \
            not request.session.get('label_id', None):
        return redirect('/all_task/')

    current_user = models.User.objects.get(name=request.session['username'])
    task = current_user.released_tasks.filter(id=request.session['task_id']).first()
    if not task:
        return redirect('/all_task/')
    sub_task = task.subtask_set.filter(id=request.session['sub_task_id']).first()
    if not sub_task:
        return redirect('/all_task/')
    label = sub_task.label_set.filter(id=request.session['label_id']).first()
    if not label:
        return redirect('/all_task/')

    if request.method == "POST":
        print(request.POST)
        if 'pass' in request.POST:
            if not digit.match(request.POST.get('pass')):
                messages.error(request, '该label_id不合法！')
                return redirect('/all_task/')
            label = sub_task.label_set.filter(pk=request.POST.get('pass')).first()
            if not label:
                messages.error(request, '该标签不存在！')
                return redirect('/all_task/')
            accept_label(label, task)
        elif 'back' in request.POST:
            if not digit.match(request.POST.get('back')):
                messages.error(request, '该label_id不合法！')
                return redirect('/all_task/')
            label = sub_task.label_set.filter(pk=request.POST.get('back')).first()
            if not label:
                messages.error(request, '该标签不存在！')
                return redirect('/all_task/')
            reject_label(label, task)

    contents = task.content.split('|')
    return render(request, 'picture_detail.html', locals())


def one_task(request):
    if not request.session.get('is_admin', None) or not request.session.get('task_id', None):
        return redirect('/all_task/')
    current_user = models.User.objects.get(name=request.session['username'])
    task = current_user.released_tasks.filter(id=request.session['task_id']).first()
    if not task:
        return redirect('/all_task/')

    if request.method == "POST":
        if 'enter' in request.POST and digit.match(request.POST.get('enter')):
            request.session['sub_task_id'] = int(request.POST.get('enter'))  # need some check
            return redirect('/check_task/')
    rank = models.User.objects.filter(num_label_accepted__gt=current_user.num_label_accepted).count() + 1
    sub_task_list = task.subtask_set.all()
    num_favorite_task = current_user.favorite_tasks.count()
    num_released_task = current_user.released_tasks.count()

    rejected_task_list = current_user.claimed_tasks.filter(
        Q(taskuser__status='rejected') | Q(taskuser__status='redoing'), taskuser__user=current_user).distinct()
    num_rejected_task = rejected_task_list.count()

    unreviewed_task_list = current_user.claimed_tasks.filter(taskuser__status='unreviewed',
                                                             taskuser__user=current_user).distinct()
    num_unreviewed_task = unreviewed_task_list.count()

    num_updated_task = models.Task.objects.filter(c_time__gt=current_user.last_login_time).count()
    label_accepted_new = current_user.label_set.filter(status='accepted',
                                                       m_time__gt=current_user.last_login_time)
    num_label_accepted_new = label_accepted_new.count()
    credits_new = 0
    for label in label_accepted_new:
        credits_new += label.sub_task.task.credit
    return render(request, 'one_task.html', locals())


def recharge(request):
    if not request.session.get('is_login', None):
        return redirect('/all_task/')
    current_user = models.User.objects.get(name=request.session['username'])

    if request.method == "POST" and 'docVlGender' in request.POST:
        # print(request.POST)
        if request.POST.get('docVlGender') == '10':
            current_user.total_credits += 10
            current_user.save()
        elif request.POST.get('docVlGender') == '20':
            current_user.total_credits += 20
            current_user.save()
        elif request.POST.get('docVlGender') == '50':
            current_user.total_credits += 50
            current_user.save()
        elif request.POST.get('docVlGender') == 'other' and 'other_amount' in request.POST and digit.match(
                request.POST.get('other_amount')):
            current_user.total_credits += int(request.POST.get('other_amount'))
            current_user.save()
        else:
            messages.error(request, "充值失败！")
            return render(request, 'recharge.html', locals())
        messages.success(request, "充值成功！")
        if request.session.get('task_type'):
            return release_task_x(request)
        else:
            return redirect('/all_task/')
    return render(request, 'recharge.html', locals())


def download_data_set(request):
    if not request.session.get('is_admin', None) or not request.session.get('task_id', None) or not request.session.get(
            'sub_task_id', None):
        return redirect('/all_task/')
    # current_user = models.User.objects.get(name=request.session['username'])
    task = models.Task.objects.get(id=request.session['task_id'])
    sub_task = models.SubTask.objects.get(id=request.session['sub_task_id'])

    response = HttpResponse(content_type='text/csv')
    response.write(codecs.BOM_UTF8)
    response['Content-Disposition'] = 'attachment; filename="task_{}_sub_task_{}.csv"'.format(task.id, sub_task.id)

    writer = csv.writer(response)
    label_list = sub_task.label_set.filter()
    # print(label_list)
    contents = task.content.split('|')

    if task.type == 4:
        writer.writerow(['User', 'Picture', 'Position', 'Type'])
        label = models.Label.objects.filter(id=request.session['label_id']).first()
        for result in label.result.split('|')[:-1]:
            results = result.split(' & ')
            answer_list = [label.user.name, results[0], results[1], contents[int(results[-1])]]
            writer.writerow(answer_list)
            # print(answer_list)
        return response

    question_list = ['User']
    for i, content in enumerate(contents[1:]):
        question_list.append('Q{}:{}'.format(i + 1, content.split('&')[0]))
    writer.writerow(question_list)
    # print(question_list)
    for label in label_list:
        answer_list = [label.user.name]

        for i, content in enumerate(label.result.split('|')[1:]):
            answer = ''
            if task.type == 1 or task.type == 2:
                answers = content.split('&')[1:]
                for ans in answers:
                    answer += '{}.{};'.format(chr(64 + int(ans)), contents[i + 1].split('&')[int(ans)])
            elif task.type == 3:
                answer = content.split('&')[-1]
            answer_list.append(answer)

        writer.writerow(answer_list)
        # print(answer_list)
    return response


def file_iterator(file_name, chunk_size=512):
    with open(file_name, 'rb') as f:
        while True:
            c = f.read(chunk_size)
            if c:
                yield c
            else:
                break

def download_other_files(request, task_id):
    if not request.session.get('is_admin', None):
        return redirect('/all_task/')
    curtask = models.Task.objects.filter(pk=task_id).first()
    zipfile_name = curtask.name+'-附件.zip'
    zip_path = os.path.join(settings.MEDIA_ROOT, "task_" + str(task_id), "otherfiles", zipfile_name)

    response = StreamingHttpResponse(file_iterator(zip_path))
    response['Content-Type'] = 'application/zip'
    response['Content-Disposition'] = "attachment; filename*=utf-8''{}".format(escape_uri_path(zipfile_name))
    return response


def check_pic(request):
    return render(request, 'check_pic.html', locals())


def choice_questions_result(request):
    print(request.POST)
    if not request.session.get('task_id', None) or not request.session.get('sub_task_id', None):
        return redirect('/all_task/')
    task = models.Task.objects.filter(id=request.session['task_id']).first()
    if not task:
        return redirect('/all_task/')
    sub_task = task.subtask_set.filter(id=request.session['sub_task_id']).first()
    if not sub_task:
        return redirect('/all_task/')

    label_list = sub_task.label_set.exclude(status='untagged')
    # print(label_list)
    qa_list = []
    contents = task.content.split('|')
    sum = len(label_list)
    choice_list = []

    for i, item in enumerate(contents[1:]):
        qa = item.split('&')
        answers = []
        for ans in qa[1:]:
            answers.append([ans, 0, []])
        for label in label_list:
            ans_list = label.result.split('|')[i + 1].split('&')[1:]
            for ans in ans_list:
                answers[int(ans) - 1][1] += 1
                answers[int(ans) - 1][2].append((label.user.name, label.user.num_label_accepted))
        qa_list.append({'question': qa[0], 'answers': answers})

    if request.method == 'POST':
        qa_num = len(qa_list)
        for i in range(1, qa_num + 1):
            if 'q' + str(i) in request.POST:
                index = int(request.POST.get('q' + str(i)))

        c_list = qa_list[index - 1]
        a_list = c_list['answers']
        i = 1
        for item in a_list:
            c = choice()
            c.content += chr(64 + i) + '.'
            c.content += item[0]
            if sum == 0:
                c.choice_value = "%.1f%%" % 0
            else:
                c.choice_value = "%.1f%%" % (item[1] / sum * 100)
            for unit in item[2]:
                m = member()
                m.name = unit[0]
                m.history = unit[1]
                c.members.append(m)
            choice_list.append(c)
            i += 1
        request.session['answers_data'] = (a_list, sum, task.type)
        return render(request, 'chart.html', locals())


def getIntersection(uinList):
    while len(uinList) > 1:
        list_a = []
        list_b = []
        list_a = uinList.pop()
        list_b = uinList.pop()
        list_c = list(set(list_a).intersection(set(list_b)))
        if len(list_c) > 0:
            uinList.append(list_c)
    return uinList[0]


def chart(request):
    if not request.session.get('task_id', None) or not request.session.get('sub_task_id',
        None) or not request.session.get('answers_data', None):
        return redirect('/all_task/')
    task = models.Task.objects.filter(id=request.session['task_id']).first()
    if not task:
        return redirect('/all_task/')
    sub_task = task.subtask_set.filter(id=request.session['sub_task_id']).first()
    if not sub_task:
        return redirect('/all_task/')

    if request.method == 'POST':
        print(request.POST)
        threshold = int(request.POST.get('value_'))
        info = request.session['answers_data']
        for nlist in info[0]:
            for i in range(0,len(nlist[2])):
                nlist[2][i] = tuple(nlist[2][i])
        sum = info[1]
        if info[2] == 1 and sum != 0:
            a_list = info[0]
            for item in a_list:
                if item[1] / sum * 100 > threshold:
                    for member in item[2]:
                        m = models.User.objects.get(name=member[0])
                        label = sub_task.label_set.filter(user=m).first()
                        # Pass
                        accept_label(label, label.sub_task.task)
        if info[2] == 2 and sum != 0:
            a_list = info[0]
            choices_mem_list = []
            mem_list = []
            final = []
            i = 0
            while i < len(a_list):
                if a_list[i][1] / sum * 100 > threshold:
                    choices_mem_list.append(a_list[i][2])
                    a_list.remove(a_list[i])
                    i -= 1
                i += 1
            # for item in a_list:
            #     if item[1] / sum * 100 > threshold:
            #         choices_mem_list.append(item[2])
            #         a_list.remove(item)
            mem_list = getIntersection(choices_mem_list)
            for mem in mem_list:
                flag = 0
                for item in a_list:
                    if mem in item[2]:
                        flag = 1
                        break
                if flag == 0:
                    final.append(mem)
            for mem in final:
                m = models.User.objects.get(name=mem[0])
                label = sub_task.label_set.filter(user=m).first()
                # Pass
                accept_label(label, label.sub_task.task)
    del request.session['answers_data']
    messages.success(request, '批量通过成功!')
    return redirect("/check_task/")


def test(request):
    return render(request, 'test.html', locals())

def room(request, room_name, user_name):
    return render(request, 'chat.html', {
        'room_name': mark_safe(json.dumps(room_name)),
        'user_name':mark_safe(json.dumps(user_name)),
    })

# android api


# tasks

def get_user(username):
    user = models.User.objects.filter(name=username).first()
    if not user:
        user = models.User.objects.filter(email=username).first()
    if not user:
        user = models.User.objects.filter(id=username).first()
    return user


def toObject(string, name):
    if string.startswith('['):
        return '{ \"' + name + '\":' + string + '}'
    else:
        return string


def get_return_json(response, name):
    seralized = serializers.serialize("json", response)
    return HttpResponse(toObject(seralized, name), content_type="application/json, charset=utf-8")


def api_all_tasks(request):
    req = simplejson.loads(request.body)
    print(request.method, req)
    task_list = get_task_list(request)
    if req['KeyWord'] != '':
        print("Key:" + req['KeyWord'])
        task_list = task_list.filter(name__contains=req['KeyWord'])
    return get_return_json(list(task_list), "resultArray")


def decode_escape_sequence(s):
    return bytes(s, "utf-8").decode("unicode_escape").replace("\": \"[{", "\": [{").replace("}]\",", "}],")


def api_enter_task(request):
    task = models.Task.objects.filter(id=simplejson.loads(request.body)['task_id']).first()
    subTasks = models.SubTask.objects.filter(task=task)
    qa_list = get_qa_list(task)
    response = {
        "subTasks": [subTask.to_dict() for subTask in subTasks],
        "qa_list": qa_list
    }
    print(response)
    return HttpResponse(json.dumps(response), content_type="application/json, charset=utf-8")

#获取收藏列表
def api_favorite_tasks(request):
    user = get_user(simplejson.loads(request.body)['user_id'])
    return get_return_json(list(user.favorite_tasks.all()), "favorite_tasks")


def api_favorite_task(request):
    req = simplejson.loads(request.body)
    user = get_user(req['username'])
    task = models.Task.objects.filter(pk=req['task_id']).first()
    message = "收藏成功!"
    if not task:
        message = '该任务不存在'
    if user.favorite_tasks.filter(pk=req['task_id']).first():
        message = '已收藏!'
    else:
        user.favorite_tasks.add(task)
    return HttpResponse(json.dumps({
        "message": message
    }), content_type="application/json, charset=utf-8")

def api_undo_favorite(request):
    req = simplejson.loads(request.body)
    user = get_user(req['username'])
    task = models.Task.objects.filter(pk=req['task_id']).first()
    message = "已移出收藏"
    if not task:
        message = '该任务不存在'
    if not user.favorite_tasks.filter(pk=req['task_id']).first():
        message = '未收藏!'
    else:
        user.favorite_tasks.remove(task)
    return HttpResponse(json.dumps({
        "message": message
    }), content_type="application/json, charset=utf-8")

def api_grab_task(request):
    req = simplejson.loads(request.body)
    task_id = req['task_id']
    username = req['username']
    user = get_user(username)
    request.session['username'] = user.name
    try:
        grab_task(request, user, task_id)
    except AttributeError:
        return HttpResponse(json.dumps({
            "message": "参数错误"
        }), content_type="application/json, charset=utf-8")
    finally:
        storage = get_messages(request)
        for message in storage:
            return HttpResponse(json.dumps({
                "message": str(message)
            }), content_type="application/json, charset=utf-8")

def api_undo_grab(request):
    req = simplejson.loads(request.body)
    task_id = req['task_id']
    username = req['username']
    user = get_user(username)
    task = models.Task.objects.filter(id=task_id).first()
    try:
        task_user=models.TaskUser.objects.filter(user = user, task = task).delete()
    except AttributeError:
        return HttpResponse(json.dumps({
            "message": "参数错误"
        }), content_type="application/json, charset=utf-8")
    finally:
        storage = get_messages(request)
        for message in storage:
            return HttpResponse(json.dumps({
                "message": str(message)
            }), content_type="application/json, charset=utf-8")

def api_get_task_user(request):
    req = simplejson.loads(request.body)
    print(req)
    user = get_user(req['username'])
    task = models.Task.objects.filter(id=req['task_id']).first()
    task_user = models.TaskUser.objects.filter(user = user, task = task).first()
    fav = 0
    # 0-未抢位，1-开启抢位，2-抢到位置,3-抢位失败
    grab = 0
    if(task_user):
        if task.is_closed and not task_user.has_grabbed:
            grab = 3
        elif task_user.status=="grabbing":
            grab = 1
        elif task_user.status == "grabbed":
            grab=2

    if user.favorite_tasks.filter(pk=req['task_id']).first():
        fav = 1
    status = 0
    #0-未做过，1-待审核/退回，2-审核中，3-完成,4-还没有人做任务
    task_user_dict = None
    if(task_user):
        if(task_user.status=="unreviewed" or task_user.status=="rejected"):
            status = 1
        elif task_user.status=="reviewing":
            status = 2
        elif task_user.status=="accepted":
            status = 3
        task_user_dict = model_to_dict(task_user)
    #
    zipfile_name = task.name + '-附件.zip'
    zip_path = os.path.join(settings.MEDIA_ROOT, "task_" + str(req['task_id']), "otherfiles", zipfile_name)
    try:
        zip = (zipfile.ZipFile(zip_path, 'r', zipfile.ZIP_DEFLATED)).namelist()
    except:
        zip = []
    #
    return HttpResponse(json.dumps({
        "isFavorite": fav,
        "isGrab": grab,
        "status": status,
        "zip":zip,
        "num_worker":task.num_worker,
        "task_user":task_user_dict
    }), content_type="application/json, charset=utf-8")

# params: username, task_id, [answers]
def api_submit_task(request):
    req = simplejson.loads(request.body)
    current_task = models.Task.objects.filter(id=req['task_id']).first()
    current_user = get_user(req['username'])
    subtasks = current_task.subtask_set.all()
    #
    task_user = models.TaskUser.objects.filter(user=current_user, task=current_task).first()
    # new task_user
    if not task_user:
        current_task.num_worker += 1
        current_task.save()
        task_user = models.TaskUser.objects.create(task=current_task, user=current_user)
    task_user.num_label_unreviewed = current_task.subtask_set.all().count()
    task_user.num_label_rejected = 0
    ind = 0
    try:
        for ans in req["answer"]:
            sub_task = subtasks[ind]
            label = sub_task.label_set.filter(user=current_user).first()
            if not label and task_user.status!="grabbing" and task_user.status!="redoing":
                label = models.Label.objects.create()
                label.user = current_user
                label.sub_task = sub_task
                label.task_user = task_user
            print("have label:")
            print(label)
            # print(model_to_dict(label))
            ind = ind + 1
            label.status = 'unreviewed'
            label.result = ans
            label.save()
        task_user.status = "unreviewed"
        task_user.save()
    except BaseException:
        return HttpResponse(json.dumps({
            "message": "信息提交失败"
        }), content_type="application/json, charset=utf-8")
    # successful
    return HttpResponse(json.dumps({
        "message": "任务已完成"
    }), content_type="application/json, charset=utf-8")

#for task type 1,2,3
def get_check_detail(contents, task, subTask,label_list):
    qa_list=[]
    for i, item in enumerate(contents[1:]):
        qa = item.split('&')
        answers = []
        details = []
        if task.type <= 2:
            for ans in qa[1:]:
                answers.append({"answer": ans, "proportion": 0, "vote_num": 0, "label_list": [], "user_list": [],
                                "accept_num_list": []})
        for label in label_list:
            ans_list = label.result.split('|')[i + 1].split('&')[1:]  # 用户完成的该qa[i]的答案列表
            # details [state: 0-unreviewed, 1-accept, 2-rejected]
            l_state = 0;
            if (label.status == "accepted"):
                l_state = 1;
            elif (label.status == "rejected"):
                l_state = 2;
            details.append({"user": label.user.name, "user_answer": [], "label_id": label.id, "state": l_state})
            for ans in ans_list:
                # 只有单选和多选需要生成统计报告
                if task.type <= 2:
                    answers[int(ans) - 1]['vote_num'] += 1
                    answers[int(ans) - 1]['label_list'].append(label.id)
                    answers[int(ans) - 1]['user_list'].append(label.user.name)
                    answers[int(ans) - 1]['accept_num_list'].append(label.user.num_label_accepted)
                # details
                if task.type <= 2:
                    details[len(details) - 1]["user_answer"].append(chr(64 + int(ans)) + ":" + qa[int(ans)])
                elif task.type == 3:
                    details[len(details) - 1]["user_answer"].append(ans)

        # 将proportion转换为比例
        tagged_sum = len(subTask.label_set.exclude(status='untagged').values('user').distinct())
        for j in range(len(answers)):
            if (tagged_sum != 0):
                answers[j]['proportion'] = answers[j]['vote_num'] / float(tagged_sum)
        qa_list.append({'question': qa[0], 'answers': answers, 'details': details})
    return qa_list
#for task type 4
def get_label_detail(contents,label_list):
    #eg:contents = [问题1，问题2，...]
    # for i, item in enumerate(contents[1:]):
    #     details.append({"user":"","user_answer": [], "label_id": label.id, "state": l_state})
    # pass
    statistics = []
    img_list = []
    for label in label_list:
        qa_list = []
        details = []
        alist = label.result.split('|')[:-1]
        # details [state: 0-unreviewed, 1-accept, 2-rejected]
        l_state = 0;
        if (label.status == "accepted"):
            l_state = 1;
        elif (label.status == "rejected"):
            l_state = 2;
        details.append({"user": label.user.name, "user_answer": [], "label_id": label.id, "state": l_state})
        #为每个选项添加用户答案
        img_list.append({"file":label.screenshot_set.first().image.url})
        qa_list.append({'question': "", 'answers': [], 'details': []})
        for i in contents[1:]:
            details[len(details)-1]["user_answer"].append(i+":")
        #lable.result: 37.jpg & 77,3,209,184 & 1|37.jpg & 3,119,66,172 & 2|
        for ans in alist:
            print(ans)
            tmp = ans.split(' & ')
            details[len(details)-1]["user_answer"][int(tmp[-1])-1] += '\n\t'+tmp[1]
        qa_list[0]['details']=details
        statistics.append({'qa_list':qa_list})
    return img_list, statistics
def api_check_task(request):
    req = simplejson.loads(request.body)
    task = models.Task.objects.filter(id=req['task_id']).first()
    subTasks = models.SubTask.objects.filter(task=task)
    #statistics:[qa_list]
    statistics = []
    r_subtasks = [] #subtasks needed return
    for subTask in subTasks:
        if(task.type!=4):
            r_subtasks.append(subTask.to_dict())
        contents = task.content.split('|')
        label_list = subTask.label_set.exclude(status='untagged')#该子任务所有已完成的result
        if(task.type != 4):
            qa_list = get_check_detail(contents,task, subTask,label_list)
            statistics.append({"qa_list":qa_list})
        else:
            img_list, statis = get_label_detail(contents,label_list)
            r_subtasks.extend(img_list)
            statistics.extend(statis)
    response = {
        "subTasks": r_subtasks,
        "statistics": statistics
    }
    print(response)
    return HttpResponse(json.dumps(response), content_type="application/json, charset=utf-8")

def api_submit_check_result(request):
    req = simplejson.loads(request.body)
    accept_list = req['accept_list']
    reject_list = req['reject_list']
    print("check_result:",req,accept_list,reject_list)
    #
    for lid in accept_list:
        label = models.Label.objects.filter(id=lid).first()
        if(label.status=='unreviewed'):
            accept_label(label, label.sub_task.task)
    for lid in reject_list:
        label = models.Label.objects.filter(id=lid).first()
        if(label.status == 'unreviewed'):
            reject_label(label, label.sub_task.task)
    return HttpResponse(json.dumps({
        "message": "审核信息提交成功"
    }), content_type="application/json, charset=utf-8")
# users

def api_login(request):
    req = simplejson.loads(request.body)
    username = req['username']
    password = req['password']
    user = get_user(username)
    print(req,models.gen_md5(password,username))
    message = ""
    if not user:
        message = "无此用户名"
        return HttpResponse(json.dumps({
            "message": message
        }), content_type="application/json, charset=utf-8")
    else:
        username = user.name
    if user.password != models.gen_md5(password, username):
        message = "密码错误"
    else:
        user.last_login_time = user.login_time
        user.login_time = timezone.now()
        user.save()
        message = "登陆成功"
    return HttpResponse(json.dumps({
        "user_id": user.id,
        "message": message
    }), content_type="application/json, charset=utf-8")


def api_logout(request):
    message = ""
    req = simplejson.loads(request.body)
    try:
        username = req['username']
        password = req['password']
        user = get_user(username)
        if not user:
            message = "无此用户名"
        else:
            username = user.name
        if user.password != models.gen_md5(password, username):
            message = "密码错误"
        else:
            message = "注销成功"
    finally:
        print(message)
        return HttpResponse(json.dumps({
            "message": message
        }), content_type="application/json, charset=utf-8")


def api_user_info(request):
    username = simplejson.loads(request.body)['username']
    user = get_user(username)
    if not user:
        user = models.User.objects.filter(email=username).first()

    return HttpResponse(json.dumps(user.to_dict()), content_type="application/json, charset=utf-8")
    # return HttpResponse(model_to_dict(user), content_type="application/json, charset=utf-8")


def api_get_tasks_of_status(status,user):
    tasks = []
    tids = models.TaskUser.objects.filter(user=user, status=status).values("task")
    print("my task:....",tids)
    for tid in tids:
        t = models.Task.objects.filter(id=tid['task']).first()
        # 不是被管理员删除的任务
        if (t != None):
            tasks.append(t)
    tasks = json.loads(serializers.serialize("json", tasks))
    print("my task:----",tasks)
    return tasks
def api_my_task(request):
    print("my task:",request.body)
    req = simplejson.loads(request.body)
    user = get_user(req['username'])
    # favorite
    f_tasks = json.loads(serializers.serialize("json", user.favorite_tasks.all()))
    # grabbed+doing=待完成
    g_tasks = api_get_tasks_of_status("grabbed",user)
    #doing
    doing = api_get_tasks_of_status("doing",user)
    #done
    done = api_get_tasks_of_status("accepted",user)
    # released
    r_tasks = json.loads(serializers.serialize("json", models.Task.objects.filter(admin=user)))
    #rejected
    rejected_tasks = api_get_tasks_of_status("rejected",user)
    #unreviewed
    unreviewed_tasks = json.loads(serializers.serialize("json",user.claimed_tasks.filter(taskuser__status='unreviewed',
                                                             taskuser__user=user).distinct()))
    #invited
    invited_tasks = json.loads(serializers.serialize("json",user.tasks_to_examine.filter(taskuser__num_label_unreviewed__gt=0).distinct()))
    for task in user.tasks_to_examine.all():
        print("invited:",task)
    # invited_tasks = json.loads(serializers.serialize("json",user.tasks_to_examine.all()))
    # for task in invited_tasks:
    #     # if(task.num_label_unre)
    #     pass
    response = {
        "favorite": f_tasks,
        "grabbed": g_tasks,
        "released": r_tasks,
        "rejected": rejected_tasks,
        "unreviewed": unreviewed_tasks,
        "invited": invited_tasks,
        "doing":doing,
        "done":done
    }
    return HttpResponse(json.dumps(response), content_type="application/json, charset=utf-8")

def api_recover_password(request):
    req = simplejson.loads(request.body)
    email = req['email']
    message = "邮箱未注册！"
    try:
        if models.User.objects.filter(email=email).exists():
            msg='你收到这封邮件是因为你请求重置你在网站OneIsAll上的用户账户密码。请访问该页面并选择一个新密码：http://10.135.197.13/ResetPassword/?email='+email+'\n感谢使用我们的站点！\nOneIsAll团队'
            send_mail('密码重置邮件',
                        msg,
                        settings.EMAIL_FROM,
                        [email])
            message = "密码重置邮件已发送！"
    except:
        message="邮箱不合法"
    return HttpResponse(json.dumps({
                "message":message
            }),content_type="application/json, charset=utf-8")


def api_sign_up(request):
    req = simplejson.loads(request.body)
    username = req['username']
    email = req['email']
    password = req['password']
    message = "注册成功！"
    if models.User.objects.filter(name=username).exists():
        message = "该用户名已注册！"
    elif models.User.objects.filter(email=email).exists():
        message = "该邮箱已注册！"
    else:
        new_user = models.User.objects.create()
        new_user.name = username
        new_user.password = models.gen_md5(password, username)
        new_user.email = email
        new_user.is_admin = False  # 只能注册普通用户
        new_user.save()
    return HttpResponse(json.dumps({
                "message":message
            }),content_type="application/json, charset=utf-8")


def api_reset_password(request):
    req = simplejson.loads(request.body)
    email = req['email']
    password = req['password']
    message = '无效的密码重置请求!'
    if email:
        user = models.User.objects.filter(email=email).first()
        user.password = models.gen_md5(password, user.name)
        user.save()
        message = '密码重置成功！'
    return HttpResponse(json.dumps({
                "message":message
            }),content_type="application/json, charset=utf-8")

def api_recommend_tasks(request):
    req = simplejson.loads(request.body)
    username = req['username']
    request.session['username'] = username
    request.session['is_admin'] = 1
    dic1 = getDic1(request)
    dic2 = getDic2(request)
    # dic3 = getDic3(request)
    # dic4 = getDic4(request)
    Last_Rank_list = []
    Last_Rank2_list = []
    print(dic1)
    W3 = user.Usersim(dic1)
    Last_Rank = user.Recommend(username, dic1, W3, 3)
    for key in Last_Rank:
        if key not in Last_Rank_list:
            Last_Rank_list.append(key)
    data = itempre.loadData(dic2)
    W = itempre.similarity(data)
    Last_Rank2 = itempre.recommandList(data, W, username, 3, 10)
    for item in Last_Rank2:
        if item[0] not in Last_Rank2_list:
            Last_Rank2_list.append(item[0])

    FinalRecommand = list(set(Last_Rank_list).union(set(Last_Rank2_list)))
    recommand_tasks = []
    for name in FinalRecommand:
        recommand_tasks += models.Task.objects.filter(name=name)
    return get_return_json(list(recommand_tasks), "resultArray")
