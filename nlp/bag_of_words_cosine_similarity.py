from nltk import word_tokenize
from math import sqrt

sent1 = "I love sky, I love sea."
sent2 = "I like running, I love reading."
sents = [sent1, sent2]
texts = [[word for word in word_tokenize(sent)] for sent in sents]
all_list = []
for text in texts:
    all_list += text
corpus = set(all_list)
print(corpus)
corpus_dict = dict(zip(corpus, range(len(corpus))))
print(corpus_dict)
# 建立句子的向量表示
def vector_rep(text, corpus_dict):
    vec = []
    for key in corpus_dict.keys():
        if key in text:
            vec.append((corpus_dict[key], text.count(key)))
        else:
            vec.append((corpus_dict[key], 0))

    vec = sorted(vec, key= lambda x: x[0])

    return vec

vec1 = vector_rep(texts[0], corpus_dict)
vec2 = vector_rep(texts[1], corpus_dict)
print(vec1)
print(vec2)
def similarity_with_2_sents(vec1, vec2):
    inner_product = 0
    square_length_vec1 = 0
    square_length_vec2 = 0
    for tup1, tup2 in zip(vec1, vec2):
        inner_product += tup1[1]*tup2[1]
        square_length_vec1 += tup1[1]**2
        square_length_vec2 += tup2[1]**2

    return (inner_product/sqrt(square_length_vec1*square_length_vec2))


cosine_sim = similarity_with_2_sents(vec1, vec2)
print('两个句子的余弦相似度为： %.4f。'%cosine_sim)


sent1 = "I love sky, I love sea."
sent2 = "I like running, I love reading."

from nltk import word_tokenize
sents = [sent1, sent2]
texts = [[word for word in word_tokenize(sent)] for sent in sents]
print(texts)

from gensim import corpora
from gensim.similarities import Similarity
import warnings
warnings.filterwarnings(action='ignore',category=UserWarning,module='gensim')
warnings.filterwarnings(action='ignore',category=FutureWarning,module='gensim')
#  语料库
dictionary = corpora.Dictionary(texts)

# 利用doc2bow作为词袋模型
corpus = [dictionary.doc2bow(text) for text in texts]
similarity = Similarity('-Similarity-index', corpus, num_features=len(dictionary))
print(similarity)
# 获取句子的相似度
new_sensence = sent1
test_corpus_1 = dictionary.doc2bow(word_tokenize(new_sensence))

cosine_sim = similarity[test_corpus_1][1]
print("利用gensim计算得到两个句子的相似度： %.4f。"%cosine_sim)