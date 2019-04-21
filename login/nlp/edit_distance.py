import distance


#编辑距离
def edit_distance(s1, s2):
    return distance.levenshtein(s1, s2)
s1 = '你在干啥'
s2 = '你在干什么'
print("编辑距离是"+str(edit_distance(s1, s2))+"\n")

#编辑距离阈值法
def edit_distance(s1, s2):
    return distance.levenshtein(s1, s2)
strings = [
    '你在干什么',
    '你在干啥',
    '你在做什么',
    '你好啊',
    '我喜欢吃西瓜'
]
target = '你在干啥'
results = list(filter(lambda x: edit_distance(x, target) <= 2, strings))
print("编辑距离阈值法获得的结果有：")
for i in results:
    print(i)
print("\n")