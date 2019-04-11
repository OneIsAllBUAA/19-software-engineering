#!/usr/bin/env python
# _*_ coding:utf-8 _*_

import cv2
import os

from mysite.settings import MEDIA_ROOT
from login import models


def video2pictures(task, frame_interval=10):
    # 初始化一个VideoCapture对象
    cap = cv2.VideoCapture()

    # 遍历所有文件
    for sub_task in task.subtask_set.all():
        sub_task.screenshot_set.all().delete()
        file_path = os.sep.join([MEDIA_ROOT, sub_task.file.name])
        print(file_path)
        frame_path = file_path.split('.')[0] + '_frame'
        print(frame_path)
        if not os.path.exists(frame_path):
            os.mkdir(frame_path)

        # VideoCapture::open函数可以从文件获取视频
        cap.open(file_path)

        # 获取视频帧数
        n_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))

        # 为了避免视频头几帧质量低下，黑屏或者无关等
        for i in range(42):
            cap.read()

        cnt = 1
        for i in range(n_frames - 42):
            ret, img = cap.read()

            # 每隔frame_interval帧进行一次截屏操作
            if i % frame_interval == 0:
                image_name = '{:0>6d}.jpg'.format(cnt)
                cnt += 1
                image_path = os.sep.join([frame_path, image_name])
                print('exported {}!'.format(image_path))
                cv2.imwrite(image_path, img)
                screenshot = models.Screenshot.objects.create()
                screenshot.sub_task = sub_task
                screenshot.image = image_path
                screenshot.save()

    # 执行结束释放资源
    cap.release()


def picture_circle(label):
    label.screenshot_set.all().delete()
    img_path = os.sep.join([MEDIA_ROOT, label.sub_task.file.name])
    print(img_path)
    label_dir_path = img_path.split('.')[0] + '_label'
    print(label_dir_path)
    if not os.path.exists(label_dir_path):
        os.mkdir(label_dir_path)

    img = cv2.imread(img_path)
    result_list = label.result.split('|')[:-1]
    for pos in result_list:
        p = pos.split('&')[1].split(',')
        cv2.rectangle(img, (int(p[0]), int(p[1])), (int(p[2]), int(p[3])), (0, 255, 0), 1)

    new_img_path = os.sep.join([label_dir_path, '{}.jpg'.format(label.id)])
    cv2.imwrite(new_img_path, img)
    screenshot = models.Screenshot.objects.create()
    screenshot.label = label
    screenshot.image = new_img_path
    screenshot.result = label.result
    screenshot.save()


def video_circle(label):
    label.screenshot_set.all().delete()
    video_path = os.sep.join([MEDIA_ROOT, label.sub_task.file.name])
    img_dir_path = video_path.split('.')[0] + '_frame'
    print(img_dir_path)
    label_dir_path = video_path.split('.')[0] + '_label'
    print(label_dir_path)
    if not os.path.exists(label_dir_path):
        os.mkdir(label_dir_path)
    label_dir_path = os.sep.join([label_dir_path, '{}'.format(label.id)])
    print(label_dir_path)
    if not os.path.exists(label_dir_path):
        os.mkdir(label_dir_path)

    result_list = label.result.split('|')[:-1]
    result_list.sort()
    img_path = os.sep.join([img_dir_path, result_list[0].split('&')[0]])
    img = cv2.imread(img_path)
    content = ''
    for i in range(len(result_list)):
        pos = result_list[i].split('&')
        if i > 0 and pos[0] != result_list[i - 1].split('&')[0]:
            img_path = os.sep.join([img_dir_path, pos[0]])
            img = cv2.imread(img_path)
            content = ''

        content += result_list[i] + '|'
        p = pos[1].split(',')
        cv2.rectangle(img, (int(p[0]), int(p[1])), (int(p[2]), int(p[3])), (0, 255, 0), 1)

        if i == len(result_list) - 1 or pos[0] != result_list[i + 1].split('&')[0]:
            new_img_path = os.sep.join([label_dir_path, pos[0]])
            cv2.imwrite(new_img_path, img)
            screenshot = models.Screenshot.objects.create()
            screenshot.label = label
            screenshot.image = new_img_path
            screenshot.result = content
            screenshot.save()
