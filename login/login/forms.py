# login/forms.py
from django import forms

from login import models


class LoginForm(forms.Form):
    username = forms.CharField(label="用户名", max_length=128, widget=forms.TextInput(
        attrs={'class': 'form-control input-lg', 'placeholder': 'Your account', 'id': 'username'}))
    password = forms.CharField(label="密码", max_length=128, widget=forms.PasswordInput(
        attrs={'class': 'form-control input-lg', 'placeholder': 'Password', 'id': 'password'}))


class RegisterForm(forms.Form):
    username = forms.CharField(label="用户名", max_length=128, widget=forms.TextInput(
        attrs={'class': 'form-control', 'id': 'username', 'placeholder': 'Your Username',
               'style': 'margin-bottom: 20px;'}))
    password1 = forms.CharField(label="密码", max_length=128, widget=forms.PasswordInput(
        attrs={'class': 'form-control', 'id': 'password1', 'placeholder': 'Your Password',
               'style': 'margin-bottom: 20px;'}))
    password2 = forms.CharField(label="确认密码", max_length=128, widget=forms.PasswordInput(
        attrs={'class': 'form-control', 'id': 'password2', 'placeholder': 'Confirm Password',
               'style': 'margin-bottom: 20px;'}))
    email = forms.EmailField(label="邮箱地址", widget=forms.EmailInput(
        attrs={'class': 'form-control', 'id': 'email', 'placeholder': 'Your Email...',
               'style': 'margin-bottom: 40px;'}))


class TaskForm(forms.Form):
    templates = (
        (1, '问答式'),
        (2, '标记式'),
        (3, '书写式'),
    )
    template = forms.ChoiceField(label='任务模板', choices=templates, required=True, widget=forms.RadioSelect())

    name = forms.CharField(label="任务名", max_length=128, required=True, widget=forms.TextInput(
        attrs={'class': 'form-control', 'style': 'margin-top: 30px;', 'placeholder': 'your task name'}))

    files = forms.FileField(label='请选择文件', required=True,
                            widget=forms.ClearableFileInput({'multiple': True, 'style': 'font-size: 22px;'}))

    q1 = forms.CharField(label='问题1', max_length=128, required=True, widget=forms.TextInput())
    a1_q1 = forms.CharField(label='选项1', max_length=128, required=False, widget=forms.TextInput())
    a2_q1 = forms.CharField(label='选项2', max_length=128, required=False, widget=forms.TextInput())
    employees_num = forms.IntegerField(label='人数', max_value=99, min_value=1, required=True, widget=forms.TextInput())
    details = forms.CharField(label="任务详情", max_length=1024, required=False, widget=forms.Textarea(
        attrs={'class': 'form-control', 'style': 'margin-top: 30px;', 'placeholder': 'remarks'}))

    # users = forms.ModelMultipleChoiceField(label="选择用户", queryset=models.User.objects.all(),
    #                                        required=False, widget=forms.CheckboxSelectMultiple())
