



# One Is All Api 文档





[TOC]



------

## 概述

本文档为One Is All 一站式众包数据标注平台的移动端api文档。

api根目录为/api，每小节的api路径位于标题右方，所有api访问应使用post方法。

------

## 安全验证方式

TODO：使用登录时返回的Token进行验证

------

## 错误处理

若服务器在处理请求时遇到问题，应在请求结果中加入error_message字段，类型为String。例如：`Invalid Token`。若不包含此字段，则默认服务器正常处理请求，规定的各字段应存在且符合格式。

TODO：规定错误代码和错误信息



## 接口使用介绍	



### 任务管理	

------

#### 获取任务列表	/all_tasks

用于获取该用户可见的任务列表。

##### 参数列表

- username 用户名或邮箱，类型为String。例如：`test@buaa.edu.cn`。
- TODO：规定filter 筛选时间、参与人数、任务悬赏积分 

##### 返回格式

- resultArray:
  - [model
  - pk
  - fields:{
    - type
    - template
    - content
    - name
    - admin
    - details
    - c_time
    - max_tagged_num
    - is_closed
    - credit
    - user_level}]

------

#### 获取任务信息	/enter_task

用于获取特定任务的信息

##### 参数列表

- task_id

##### 返回格式

- subTasks:
  - [id
  - file
  - task
  - result]
- qa_list:[
  - question
  - answers: 选项String数组]

