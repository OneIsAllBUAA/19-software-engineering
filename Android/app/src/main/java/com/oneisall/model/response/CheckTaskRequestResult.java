package com.oneisall.model.response;

import java.io.Serializable;
import java.util.List;

public class CheckTaskRequestResult implements Serializable {
    private List<SubTask> subTasks;
    private List<Statistic> statistics;

    public static class Statistic implements Serializable{
        private List<CheckQA> qa_list;

        public List<CheckQA> getCheckQa_list() {
            return qa_list;
        }
        @Override
        public String toString(){
            String s = "";
            for(CheckQA cqa: qa_list){
                s += cqa.toString();
            }
            return s;
        }
    }
    public static class CheckQA implements Serializable{
        private String question;
        private List<AnswerDetail> answers;
        private List<UserAnswer> details;

        @Override
        public String toString() {
            return "QA{" +
                    "question='" + question + '\'' +
                    ", answers=" + answers +
                    ", details=" + details +
                    '}';
        }

        public String getQuestion() {
            return question;
        }

        public List<AnswerDetail> getAnswers() {
            return answers;
        }

        public List<UserAnswer> getDetails(){ return details; }

        public CheckQA(String question, List<AnswerDetail> answers) {
            this.question = question;
            this.answers = answers;
        }
    }
    public class AnswerDetail implements Serializable{
        private String answer;
        private double proportion;
        private List<Integer> label_list;
        private int vote_num;
        private List<String> user_list;
        private List<Integer> accept_num_list;

        public String getAnswer() {
            return answer;
        }

        public double getProportion() {
            return proportion;
        }

        public List<Integer> getLabel_list() {
            return label_list;
        }

        public int getVote_num(){ return vote_num; }

        public List<Integer> getAccept_num_list() {
            return accept_num_list;
        }

        public List<String> getUser_list() {
            return user_list;
        }

        @Override
        public String toString(){
            String s = "";
            for(int i: label_list) s+= i+",";
            return "answer:"+answer+",proportion:"+proportion+",label_list:"+s;
        }
    }
    public class UserAnswer implements Serializable{
        private String user;
        private List<String> user_answer;
        private int label_id;
        private int state;
        public String getUserName(){
            return user;
        }
        public List<String> getUser_answer(){ return user_answer; }
        public int getLabel_id(){ return label_id; }
        public int getState(){ return state; }
        @Override
        public String toString(){
            String s="";
            for(String ans: user_answer) s+= ans;
            return "user:"+user+", user_answer:"+s + ", lable_id:" + label_id;
        }
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public List<Statistic> getStatistics() {
        return statistics;
    }

    @Override
    public String toString() {
        return "CheckTaskRequestResult{" +
                "subTasks=" + subTasks +
                ", qa_list=" + statistics +
                '}';
    }

    public CheckTaskRequestResult(List<SubTask> subTasks, List<Statistic> statistics) {
        this.subTasks = subTasks;
        this.statistics = statistics;
    }
}
