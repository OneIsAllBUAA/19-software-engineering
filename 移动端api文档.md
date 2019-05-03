



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

## 	

### 用户管理

------

#### 用户登录	/login

用于用户登录。

##### 参数列表

- username 用户名或邮箱，类型为String。例如：`test@buaa.edu.cn`。
- password 密码,类型为string。例如：·`123456`

##### 返回格式

- message  登录信息
  - 登陆成功
  - 无此用户名
  - 密码错误

------

#### 用户注销	/logout

用于用户注销。

##### 参数列表

- username 用户名或邮箱，类型为String。例如：`test@buaa.edu.cn`。

##### 返回格式

- message  登录信息
  - 注销成功：succeed
  - 无此用户名：invalid username
  - 密码错误：invalid password

------

#### 用户信息	/user_info

用于获取当前用户信息。

##### 参数列表

- username

##### 返回格式

- username 用户名，类型为String。
- user_id
- email 邮箱，类型为String。
- total_credits用户积分，类型为int。

------

### 任务管理	

------

#### 任务模型

task:{

- model
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
  - user_level}

}



#### 获取任务列表	/all_tasks

用于获取该用户可见的任务列表。

##### 参数列表

- username 用户名或邮箱，类型为String。例如：`test@buaa.edu.cn`。
- TODO：规定filter 筛选时间、参与人数、任务悬赏积分 

##### 返回格式

- resultArray:
  - [task]

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

    
------

#### 获取审核任务信息  /check_task

返回任务选项及每个选项被选择的比例

##### 参数列表

- task_id

##### 返回格式

- subTasks:
  - [id
  - file
  - task
  - result]
- statistics:[
  - qa_list:[
    - question
    - answers: [
      - answer
      - proportion
      - vote_num
      - user_list:[]
      - accept_num_list:[]
      - label_list:标注结果的id，用于提交审核结果]]
    - details: [
      - user
      - user_answer：string数组
      - label_id
      - state(0:未审核，1：通过，2：退回）
  ]

------

#### 提交审核结果 /submit_check_result

每个子任务的 每个子问题 单独提交

##### 参数列表

- accept_list[]
- reject_list[]

##### 返回格式

- messages：返回信息

------

#### 获取已收藏任务列表	/favorite_tasks

用于获取该用户收藏的任务列表。

##### 参数列表

- username 用户名或邮箱，类型为String。例如：`test@buaa.edu.cn`。
- TODO：规定filter 筛选时间、参与人数、任务悬赏积分 

##### 返回格式

- favorite_tasks:[task]

------

#### 任务抢位	/grab_task

用于任务抢位

##### 参数列表

- username
- task_id

##### 返回格式

- message: 返回信息

- 

------

#### 任务提交	/submit_task

用于任务抢位

##### 参数列表

- username
- task_id
- [answers]

##### 返回格式

- message: 返回信息

- 

------

#### 我的任务	/my _task

参数列表

- username

##### 返回格式

- favorate:[task]
- grabbed:[task]
- released:task[]
- rejected:[task]
- unreviewed:[task]
- invited:[task]
