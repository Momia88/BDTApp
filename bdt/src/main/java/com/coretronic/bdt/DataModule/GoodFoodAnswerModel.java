package com.coretronic.bdt.DataModule;

import java.util.List;
//A
public class GoodFoodAnswerModel {
    private String udid;
    private String time;
    private List<AnswerList> answerListContainer;

    public void setUid(String udid) {
        this.udid = udid;
    }

    public String getUid() {
        return udid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<AnswerList> getAnswerListContainer() {
        return answerListContainer;
    }

    public void setAnswerListContainer(List<AnswerList> answerListContainer) {
        this.answerListContainer = answerListContainer;
    }

    public static class AnswerList {
        private Answer answer;

        public Answer getAnswer() {
            return answer;
        }

        public void setAnswer(Answer answer) {
            this.answer = answer;
        }
    }

    public static class Answer {
        private String question_id = "";
        private String answer_id = "";

        public String getQuestionID() {
            return question_id;
        }

        public void setQuestionID(String question_id) {
            this.question_id = question_id;
        }

        public String getAnswerID() {
            return answer_id;
        }

        public void setAnswerID(String answer_id) {
            this.answer_id = answer_id;
        }

    }
}
