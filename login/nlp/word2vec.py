import gensim
import jieba
import numpy as np
from scipy.linalg import norm

model_file = 'word2vec/news_12g_baidubaike_20g_novel_90g_embedding_64.bin'
print("加载模型开始")
model = gensim.models.KeyedVectors.load_word2vec_format(model_file, binary=True)
print("加载模型结束")

def vector_similarity(s1, s2):
    def sentence_vector(s):
        words = jieba.lcut(s)
      #  print("jieba分词结束")
        v = np.zeros(64)
        for word in words:
       #     print("循环")
            v += model[word]
        v /= len(words)
        return v

    v1, v2 = sentence_vector(s1), sentence_vector(s2)
   # print(v1, v2)
    return np.dot(v1, v2) / (norm(v1) * norm(v2))

#strings = ["我不喜欢上海", "上海是一个好地方", "北京是一个好地方", "上海好玩的在哪里", "上海是好地方", "上海路和上海人", "喜欢小吃", "我喜欢上海的小吃"]

strings = [ "框选出视频中的狗","请问您对这个网页的使用体验如何"]


target = "圈画图中的熊"

for string in strings:
    print(string, vector_similarity(string, target))