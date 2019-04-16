#-*- coding:utf-8
'''
test_lda_main.py
这个文件的作用是汇总前面各部分代码，对文档进行基于lda的相似度计算

'''
from lda_model import get_lda_model
from similarity import lda_similarity_corpus
from save_result import save_similarity_matrix
import nltk
nltk.download('punkt')
import traceback

INPUT_PATH = ""
OUTPUT_PATH = "lda_simi_matrix.txt"

def main():
        # 语料
     #   documents = ["我不喜欢上海", "上海是一个好地方","北京是一个好地方","上海好吃的在哪里","上海好玩的在哪里","上海是好地方", "上海路和上海人", "喜欢小吃","我喜欢上海的小吃"

      #  ]
        documents = ["标注出图中的狗","框选出狗","这张图中显示的是猫还是狗","请录入一段上海话","这是一件上衣还是一条长裤","您对这张网页的美观度评价如何","以下哪个是‘请用英文再说一遍’的翻译？"

                     ]
        # 训练lda模型
        K = 6 # number of topics
        lda_model, _, _,corpus_tf, _ = get_lda_model(documents, K)

        # 计算语聊相似度
        lda_similarity_matrix = lda_similarity_corpus( corpus_tf, lda_model )

        # 保存结果
        save_similarity_matrix( lda_similarity_matrix, OUTPUT_PATH )


main()