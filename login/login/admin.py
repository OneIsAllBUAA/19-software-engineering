# login/admin.py

from django.contrib import admin
from . import models

admin.site.register(models.User)
admin.site.register(models.Task)
admin.site.register(models.SubTask)
admin.site.register(models.Label)
