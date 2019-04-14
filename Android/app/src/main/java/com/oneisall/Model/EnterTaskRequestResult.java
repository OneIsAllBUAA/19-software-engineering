package com.oneisall.Model;

import java.util.List;

public class EnterTaskRequestResult {
    private List<SubTaskFields> subTasks;
    private List<QA> qaList;

    private static class QA{
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

    public List<SubTaskFields> getSubTasks() {
        return subTasks;
    }

    public List<QA> getQaList() {
        return qaList;
    }

    @Override
    public String toString() {
        return "EnterTaskRequestResult{" +
                "subTasks=" + subTasks +
                ", qaList=" + qaList +
                '}';
    }
}
