# login/views.py
import codecs
import csv

from django.http import HttpResponse
from django.shortcuts import render, redirect
from django.utils import timezone
from django.contrib import messages

from login import forms, models, tools
import re
import os

from mysite.settings import MEDIA_ROOT

digit = re.compile("^\d{1,10}$")


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
            messages.error(request, "用户名未注册！")
            return render(request, 'login.html', locals())
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


def choose(request):
    if not request.session.get('is_admin', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    return render(request, 'choose.html', locals())


def release_task_1(request):
    if not request.session.get('is_admin', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    request.session['task_type'] = 1
    return render(request, 'release_task.html', locals())


def release_task_2(request):
    if not request.session.get('is_admin', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    request.session['task_type'] = 2
    return render(request, 'release_task.html', locals())


def release_task_3(request):
    if not request.session.get('is_admin', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    request.session['task_type'] = 3
    return render(request, 'release_task_1.html', locals())


def release_task_4(request):
    if not request.session.get('is_admin', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    request.session['task_type'] = 4
    return render(request, 'release_task_2.html', locals())


def release_task(request):
    if not request.session.get('is_admin', None) or not request.session.get('task_type', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")

    if request.method == "POST":
        print(request.POST)
        print(request.FILES)
        task_form = forms.TaskForm(request.POST, request.FILES)
        if not task_form.is_valid():
            messages.error(request, "表单信息有误，请重新填写！")
            return release_task_x(request)

        files = request.FILES.getlist('files')  # exception
        print(files)
        print(type(files))

        template = task_form.cleaned_data['template']
        name = task_form.cleaned_data['name']
        details = task_form.cleaned_data['details']
        employees_num = task_form.cleaned_data['employees_num']
        print('employees_num', employees_num, type(employees_num))
        # credit = task_form.cleaned_data['credit']
        # credit = int(credit)
        credit = 2
        current_user = models.User.objects.get(name=request.session['username'])
        if current_user.total_credits < credit * employees_num * len(files):
            messages.error(request, "您的信用积分不足，无法发布任务！")
            return release_task_x(request)

        new_task = models.Task.objects.create()
        new_task.type = request.session['task_type']
        new_task.name = name
        new_task.admin = current_user
        new_task.template = int(template)
        new_task.details = details
        new_task.max_tagged_num = employees_num
        new_task.credit = credit

        # save questions and answers
        i = 1
        content = ''
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

        # save images
        for f in files:
            sub_task = models.SubTask.objects.create()
            sub_task.file = f
            sub_task.task = new_task
            sub_task.save()

        current_user.total_credits -= credit * employees_num * len(files)
        current_user.save()


        if new_task.template == 1:
            request.session['task_id'] = new_task.id
            del request.session['task_type']
            return redirect('/confirm_to_upload_pictures/')
        elif new_task.template == 2 and new_task.type == 4:
            request.session['task_id'] = new_task.id
            return redirect('/video2pictures_slide/')
        del request.session['task_type']
        messages.success(request, "任务发布成功！")
        return redirect('/all_task/')

    return release_task_x(request)


def video2pictures_slide(request):
    if not request.session.get('is_admin', None) or not request.session.get('task_type', None) or not request.session.get('task_id', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    task_id = request.session['task_id']
    task = models.Task.objects.filter(pk=task_id).first()
    if not task:
        messages.error(request, '该任务不存在！')
        return redirect("/all_task/")

    if request.method == "POST":
        print(request.POST)
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
                del request.session['task_id']
                del request.session['frame']
                del request.session['task_type']
                messages.success(request, "任务发布成功！")
                return redirect("/all_task/")
        elif 'return' in request.POST:
            current_user = models.User.objects.get(name=request.session['username'])
            current_user.released_tasks.filter(pk=task_id).delete()
            del request.session['task_id']
            del request.session['frame']
            del request.session['task_type']
            return redirect("/all_task/")
        elif 'abandon' in request.POST and digit.match(request.POST.get('abandon')):
            screenshot = models.Screenshot.objects.filter(id=int(request.POST.get('abandon'))).first()
            if screenshot and screenshot.sub_task.task.id == task_id:
                screenshot.delete()

    return render(request, 'video2pictures_slide.html', locals())


def confirm_to_upload_pictures(request):
    if not request.session.get('is_admin', None) or not request.session.get('task_id', None):
        messages.error(request, "页面已过期！")
        return redirect("/all_task/")
    task_id = request.session['task_id']
    task = models.Task.objects.filter(pk=task_id).first()
    if not task:
        messages.error(request, '该任务不存在！')
        return redirect("/all_task/")

    if request.method == "POST":
        print(request.POST)
        if 'abandon' in request.POST and digit.match(request.POST.get('abandon')):
            sub_task_id = int(request.POST.get('abandon'))
            task.subtask_set.filter(pk=sub_task_id).delete()
        elif 'confirm' in request.POST:
            del request.session['task_id']
            messages.success(request, "任务发布成功！")
            return redirect("/all_task/")

    sub_tasks = task.subtask_set.all()
    return render(request, 'confirm_to_upload_pictures.html', locals())


def all_task(request):
    task_list = models.Task.objects.all()
    num_task = task_list.count()

    num_user = models.User.objects.count()
    task_templates = ['', '图片', '视频', '音频']
    task_types = ['', '单选式', '多选式', '问答式', '标注式']
    temp_excluded_list = []

    if request.method == "POST":
        print(request.POST)
        if 'task_sort' in request.POST or 'task_filter' in request.POST:
            if request.POST.get('order') == 'time_desc':
                task_list = task_list.order_by('-c_time')
            temp_excluded_list = request.POST.getlist('temp_excluded')
            if 'temp1' in temp_excluded_list:
                task_list = task_list.exclude(template=1, type=3)
            if 'temp2' in temp_excluded_list:
                task_list = task_list.exclude(template=1, type=4)
            if 'temp3' in temp_excluded_list:
                task_list = task_list.exclude(template=3, type=3)
            if request.POST.get('tagged_num') == 'single':
                task_list = task_list.filter(max_tagged_num=1)
            elif request.POST.get('tagged_num') == 'multi':
                task_list = task_list.exclude(max_tagged_num=1)
        elif 'collect' in request.POST:
            collect_task(request)
        elif 'remove' in request.POST:
            remove_task(request)
        elif 'enter' in request.POST:
            if digit.match(request.POST.get('enter')):
                request.session['task_id'] = int(request.POST.get('enter'))
                return redirect('/enter_task/')
        elif 'cancel_tasks' in request.POST:
            cancel_task(request)
            task_list = models.Task.objects.all()
            num_task = task_list.count()
        elif 'review' in request.POST:
            if digit.match(request.POST.get('review')):
                request.session['task_id'] = int(request.POST.get('review'))
                return redirect('/one_task/')
        # elif 'abandon' in request.POST:
        #     if digit.match(request.POST.get('abandon')) and request.session.get('is_login', None):
        #         task_id = int(request.POST.get('abandon'))
        #         current_user = models.User.objects.get(name=request.session['username'])
        #         current_user.taskuser_set.filter(task__id=task_id).delete()
        # elif 'redo' in request.POST:
        #     if digit.match(request.POST.get('redo')):
        #         request.session['task_id'] = int(request.POST.get('redo'))
        #         return redirect('/enter_task/')

        # num_task_unfinished = models.Task.objects.filter(is_closed=False).count()
    if request.session.get('is_login', None):
        current_user = models.User.objects.get(name=request.session['username'])
        favorite_task_list = current_user.favorite_tasks.all()
        num_favorite_task = favorite_task_list.count()

        released_task_list = current_user.released_tasks.all()
        num_released_task = released_task_list.count()

        rejected_task_list = current_user.favorite_tasks.filter(subtask__label__is_rejected=True).distinct()
        num_rejected_task = rejected_task_list.count()

        unreviewed_task_list = current_user.favorite_tasks.filter(subtask__label__is_unreviewed=True).distinct()
        num_unreviewed_task = unreviewed_task_list.count()

        current_user.login_time = timezone.now()
        current_user.save()
        num_updated_task = models.Task.objects.filter(c_time__gt=current_user.last_login_time).count()

    return render(request, 'all_task.html', locals())


def collect_task(request):
    if not request.session.get('is_login', None) or not digit.match(request.POST.get('collect')):
        messages.error(request, '用户未登录！')
        return
    task_id = int(request.POST.get('collect'))
    task = models.Task.objects.filter(pk=task_id).first()
    if not task:
        messages.error(request, '该任务不存在！')
        return
    if task.users.count() >= task.max_tagged_num:
        messages.error(request, '该任务已达到最大收藏人数，无法收藏！')
        return
    current_user = models.User.objects.get(name=request.session['username'])

    if models.TaskUser.objects.filter(task=task, user=current_user).exists():
        messages.error(request, '您已经收藏了该任务！')
        return

    models.TaskUser.objects.create(task=task, user=current_user)


def remove_task(request):
    if not request.session.get('is_login', None):
        return
    current_user = models.User.objects.get(name=request.session['username'])
    task_id_list = request.POST.getlist('removed_task_id_list')
    for task_id in task_id_list:
        if not digit.match(task_id):
            messages.error(request, '该task_id不合法！')
            continue
        task_id = int(task_id)
        current_user.taskuser_set.filter(task__id=task_id).delete()
        # task = models.Task.objects.filter(pk=task_id).first()
        # if not task:
        #     print('该任务不存在！')
        #     continue
        # models.TaskUser.objects.filter(task=task, user=current_user).delete()  # when not exist, no exception.


def cancel_task(request):
    if not request.session.get('is_admin', None):
        return
    current_user = models.User.objects.get(name=request.session['username'])
    task_id_list = request.POST.getlist('canceled_task_id_list')
    for task_id in task_id_list:
        if not digit.match(task_id):
            print('该task_id不合法！')
            continue
        task_id = int(task_id)
        current_user.released_tasks.filter(pk=task_id).delete()
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
    # current_user = models.User.objects.get(name=request.session['username'])
    task = models.Task.objects.get(id=request.session['task_id'])

    if not task.users.filter(name=request.session['username']).first():
        messages.error(request, '请先收藏该任务再开始标注！')
        return redirect('/all_task/')

    # task_templates = ['', '图片', '视频', '音频']
    # task_types = ['', '单选式', '多选式', '问答式', '标注式']

    if task.template == 1:
        return redirect('/picture_task/')
    if task.template == 2:
        return redirect('/video_task/')
    if task.template == 3:
        return redirect('/player_task/')
    return redirect('/all_task/')


def picture_task(request):
    if not request.session.get('is_login', None) or not request.session.get('task_id', None):
        return redirect('/all_task/')
    current_user = models.User.objects.get(name=request.session['username'])
    task = models.Task.objects.get(id=request.session['task_id'])
    if task.template != 1:
        messages.error(request, '该任务不是图片类任务！')
        return redirect('/all_task/')
    if not task.users.filter(name=request.session['username']).first():
        messages.error(request, '请先收藏该任务再开始标注！')
        return redirect('/all_task/')

    if request.method == "POST":
        print(request.POST)
        result = ''
        if task.type != 4:
            i = 1
            while 'q' + str(i) in request.POST:
                result += '|' + 'q' + str(i)
                answers = request.POST.getlist('q' + str(i))
                print(answers)
                for answer in answers:
                    result += '&' + answer
                i += 1
        else:
            result = request.POST.get('position').replace('\r\n', '|')

        sub_task_id = request.session.get('sub_task_id', None)
        if result == '' and task.type == 2:
            messages.error(request, '请至少选择一项结果！')
        elif sub_task_id:
            sub_task = models.SubTask.objects.get(pk=sub_task_id)
            print(sub_task)
            task_user = current_user.taskuser_set.filter(task=task).first()
            label = models.Label.objects.create()
            label.user = current_user
            label.sub_task = sub_task
            label.result = result
            label.task_user = task_user
            label.save()
            if task.type == 4:
                tools.picture_circle(label)
            del request.session['sub_task_id']

    sub_task = models.get_untagged_sub_task(task, current_user)
    if not sub_task:
        messages.success(request, "该任务已完成！")
        del request.session['task_id']
        return redirect('/all_task/')
    request.session['sub_task_id'] = sub_task.id

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


def video_task(request):
    if not request.session.get('is_login', None) or not request.session.get('task_id', None):
        return redirect('/all_task/')
    current_user = models.User.objects.get(name=request.session['username'])
    task = models.Task.objects.get(id=request.session['task_id'])
    if task.template != 2:
        messages.error(request, '该任务不是视频类任务！')
        return redirect('/all_task/')
    if not task.users.filter(name=request.session['username']).first():
        messages.error(request, '请先收藏该任务再开始标注！')
        return redirect('/all_task/')

    if request.method == "POST":
        print(request.POST)
        result = ''
        if task.type != 4:
            i = 1
            while 'q' + str(i) in request.POST:
                result += '|' + 'q' + str(i)
                answers = request.POST.getlist('q' + str(i))
                print(answers)
                for answer in answers:
                    result += '&' + answer
                i += 1
        else:
            result = request.POST.get('position').replace('\r\n', '|')

        sub_task_id = request.session.get('sub_task_id', None)
        if result == '' and task.type == 2:
            messages.error(request, '请至少选择一项结果！')
        elif sub_task_id:
            sub_task = models.SubTask.objects.get(pk=sub_task_id)
            print(sub_task)
            task_user = current_user.taskuser_set.filter(task=task).first()
            label = models.Label.objects.create()
            label.user = current_user
            label.sub_task = sub_task
            label.result = result
            label.task_user = task_user
            label.save()
            if task.type == 4:
                tools.video_circle(label)
            del request.session['sub_task_id']

    sub_task = models.get_untagged_sub_task(task, current_user)
    if not sub_task:
        messages.success(request, "该任务已完成！")
        del request.session['task_id']
        return redirect('/all_task/')
    request.session['sub_task_id'] = sub_task.id

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
        return render(request, 'video_circle.html', locals())


def player_task(request):
    if not request.session.get('is_login', None) or not request.session.get('task_id', None):
        return redirect('/all_task/')
    current_user = models.User.objects.get(name=request.session['username'])
    task = models.Task.objects.get(id=request.session['task_id'])
    if task.template != 3 or task.type >= 4:
        messages.error(request, '该任务不是音频类任务！')
        return redirect('/all_task/')
    if not task.users.filter(name=request.session['username']).first():
        messages.error(request, '请先收藏该任务再开始标注！')
        return redirect('/all_task/')

    if request.method == "POST":
        print(request.POST)
        i = 1
        result = ''
        while 'q' + str(i) in request.POST:
            result += '|' + 'q' + str(i)
            answers = request.POST.getlist('q' + str(i))
            print(answers)
            for answer in answers:
                result += '&' + answer
            i += 1

        sub_task_id = request.session.get('sub_task_id', None)
        if result == '' and task.type == 2:
            messages.error(request, '请至少选择一项结果！')
        elif sub_task_id:
            sub_task = models.SubTask.objects.get(pk=sub_task_id)
            print(sub_task)
            task_user = current_user.taskuser_set.filter(task=task).first()
            label = models.Label.objects.create()
            label.user = current_user
            label.sub_task = sub_task
            label.result = result
            label.task_user = task_user
            label.save()
            del request.session['sub_task_id']

    sub_task = models.get_untagged_sub_task(task, current_user)
    if not sub_task:
        messages.success(request, "该任务已标注完成！")
        del request.session['task_id']
        return redirect('/all_task/')
    request.session['sub_task_id'] = sub_task.id

    qa_list = []
    contents = task.content.split('|')
    for item in contents[1:]:
        qa = item.split('&')
        qa_list.append({'question': qa[0], 'answers': qa[1:]})

    if task.type == 1:
        return render(request, 'player_task.html', locals())
    elif task.type == 2:
        return render(request, 'player_task_multi_choice.html', locals())
    else:
        return render(request, 'player_task_qa.html', locals())


def reject_label(request):
    if not digit.match(request.POST.get('back')):
        messages.error(request, '该label_id不合法！')
        return
    sub_task = models.SubTask.objects.get(id=request.session['sub_task_id'])
    label_id = int(request.POST.get('back'))
    label = sub_task.label_set.filter(pk=label_id).first()
    if not label:
        messages.error(request, '该标签不存在！')
        return
    label.is_rejected = True
    label.is_unreviewed = False
    label.save()


def accept_label(request):
    if not digit.match(request.POST.get('pass')):
        messages.error(request, '该label_id不合法！')
        return
    sub_task = models.SubTask.objects.get(id=request.session['sub_task_id'])
    label_id = int(request.POST.get('pass'))
    label = sub_task.label_set.filter(pk=label_id).first()
    if not label:
        messages.error(request, '该标签不存在！')
        return
    # label.is_rejected = False
    label.is_unreviewed = False
    label.save()
    label.user.total_credits += label.sub_task.task.credit
    label.user.save()


def check_task(request):
    if not request.session.get('is_admin', None) or not request.session.get('task_id', None) or not request.session.get(
            'sub_task_id', None):
        return redirect('/all_task/')
    current_user = models.User.objects.get(name=request.session['username'])
    task = models.Task.objects.get(id=request.session['task_id'])
    sub_task = models.SubTask.objects.get(id=request.session['sub_task_id'])

    if request.method == "POST":
        print(request.POST)
        if 'pass' in request.POST:
            accept_label(request)
        elif 'back' in request.POST:
            reject_label(request)
        elif 'detail' in request.POST and task.type == 4:
            label = models.Label.objects.filter(id=int(request.POST.get('detail'))).first()
            contents = task.content.split('|')
            return render(request, 'picture_detail.html', locals())
        elif 'pass_all' in request.POST:
            label_list = sub_task.label_set.all()
            for label in label_list:
                label.is_rejected = False
                label.is_unreviewed = False
                label.save()
                label.user.total_credits += label.sub_task.task.credit
                label.user.save()

    label_list = sub_task.label_set.all()
    print(label_list)
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


def one_task(request):
    if not request.session.get('is_admin', None) or not request.session.get('task_id', None):
        return redirect('/all_task/')
    current_user = models.User.objects.get(name=request.session['username'])
    task = models.Task.objects.get(id=request.session['task_id'])

    if request.method == "POST":
        if 'enter' in request.POST and digit.match(request.POST.get('enter')):
            request.session['sub_task_id'] = int(request.POST.get('enter'))  # need some check
            return redirect('/check_task/')

    sub_task_list = task.subtask_set.all()
    num_favorite_task = current_user.favorite_tasks.count()
    num_released_task = current_user.released_tasks.count()
    num_updated_task = models.Task.objects.filter(c_time__gt=current_user.last_login_time).count()
    return render(request, 'one_task.html', locals())


def recharge(request):
    if not request.session.get('is_login', None):
        return redirect('/all_task/')
    current_user = models.User.objects.get(name=request.session['username'])

    if request.method == "POST":
        print(request.POST)

    return render(request, 'recharge.html', locals())


def picture(request):
    return render(request, 'picture.html', locals())


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
    print(label_list)
    contents = task.content.split('|')
    question_list = ['User']
    for i, content in enumerate(contents[1:]):
        question_list.append('Q{}:{}'.format(i + 1, content.split('&')[0]))
    writer.writerow(question_list)
    print(question_list)
    for label in label_list:
        answer_list = [label.user.name]

        if task.type == 4:
            for i in range(len(question_list) - 1):
                answer_list.append('')
            for i, content in enumerate(label.result.split('|')[:-1]):
                answer_list[int(content.split('&')[-1])] += content.split('&')[0] + ':' + content.split('&')[1] + ';'
        else:
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
        print(answer_list)

    return response
