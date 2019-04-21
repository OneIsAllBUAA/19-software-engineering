import nltk
import traceback
import jieba
from nltk.corpus import stopwords
from nltk.stem.lancaster import LancasterStemmer
from collections import defaultdict

# 分词 - 英文
def tokenize(document):


        token_list = nltk.word_tokenize(document)

        #print "[INFO]: tokenize is finished!"
        return token_list



# 分词 - 中文
def tokenize_chinese(document):

        token_list = jieba.cut( document, cut_all=False )

        #print "[INFO]: tokenize_chinese is finished!"
        return token_list



# 去除停用词
def filtered_stopwords(token_list):


        token_list_without_stopwords = [ word for word in token_list
                                         if word not in stopwords.words("english")]


        #print "[INFO]: filtered_words is finished!"
        return token_list_without_stopwords


# 去除标点
def filtered_punctuations(token_list):
        punctuations = ['', '\n', '\t', ',', '.', ':', ';', '?', '(', ')', '[', ']', '&', '!', '*', '@', '#', '$', '%']
        token_list_without_punctuations = [word for word in token_list
                                                         if word not in punctuations]
        #print "[INFO]: filtered_punctuations is finished!"
        return token_list_without_punctuations



# 词干化
def stemming( filterd_token_list ):

        st = LancasterStemmer()
        stemming_token_list = [ st.stem(word) for word in filterd_token_list ]

        #print "[INFO]: stemming is finished"
        return stemming_token_list



# 去除低频单词
def low_frequence_filter( token_list ):

        word_counter = defaultdict(int)
        for word in token_list:
            word_counter[word] += 1

        threshold = 0
        token_list_without_low_frequence = [ word
                                             for word in token_list
                                             if word_counter[word] > threshold]

        #print "[INFO]: low_frequence_filter is finished!"
        return token_list_without_low_frequence


"""
功能：预处理
@ document: 文档
@ token_list: 预处理之后文档对应的单词列表
"""
def pre_process( document ):

        token_list = tokenize_chinese(document)
        #token_list = filtered_stopwords(token_list)
        token_list = filtered_punctuations(token_list)
        #token_list = stemming(token_list)
        #token_list = low_frequence_filter(token_list)

        #print "[INFO]: pre_process is finished!"
        return token_list



"""
功能：预处理
@ document: 文档集合
@ token_list: 预处理之后文档集合对应的单词列表
"""
def documents_pre_process( documents ):

        documents_token_list = []
        for document in documents:
            token_list = pre_process(document)
            documents_token_list.append(token_list)

        print ("[INFO]:documents_pre_process is finished!")
        return documents_token_list



#-----------------------------------------------------------------------
def test_pre_process():
    documents = ["标注出图片中的熊", "标注图中的狗", ""

                 ]
    documents_token_list = []
    for document in documents:
        token_list = pre_process(document)
        documents_token_list.append(token_list)

    for token_list in documents_token_list:
        print (token_list)

test_pre_process()

