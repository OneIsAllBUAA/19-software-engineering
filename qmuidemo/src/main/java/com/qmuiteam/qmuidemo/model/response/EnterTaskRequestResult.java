package com.qmuiteam.qmuidemo.model.response;

import java.util.List;

public class EnterTaskRequestResult {
    private List<SubTask> subTasks;
    private List<QA> qa_list;

    public static class QA{
        private String question;
        private List<String> answers;

        @Override
        public String toString() {
            return "QA{" +
                    "question='" + question + '\'' +
                    ", answers=" + answers +
                    '}';
        }

        public String getQuestion() {
            return question;
        }

        public List<String> getAnswers() {
            return answers;
        }

        public QA(String question, List<String> answers) {
            this.question = question;
            this.answers = answers;
        }
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public List<QA> getQa_list() {
        return qa_list;
    }

    @Override
    public String toString() {
        return "EnterTaskRequestResult{" +
                "subTasks=" + subTasks +
                ", qa_list=" + qa_list +
                '}';
    }

    public EnterTaskRequestResult(List<SubTask> subTasks, List<QA> qaList) {
        this.subTasks = subTasks;
        this.qa_list = qaList;
    }
}
