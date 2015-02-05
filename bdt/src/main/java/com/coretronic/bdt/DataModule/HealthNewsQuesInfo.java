package com.coretronic.bdt.DataModule;

/**
 * Created by changyuanyu on 14/9/13.
 */
public class HealthNewsQuesInfo {
    private String msgCode;
    private String status;

    private Result result;

    public void setMsgCode(String msgCode) {

        this.msgCode = msgCode;
    }
    public String getMsgCode() {

        return msgCode;
    }

    public void setStatus(String status) {

        this.status = status;
    }
    public String getStatus() {

        return status;
    }


    public void setResult(Result result)
    {
        this.result= result;
    }
    public Result getResult()
    {
        return result;
    }

    public class Result{
        private String question_id;
        private String question;
        private String item1;
        private String item2;
        private String item3;
        private String answer;

        public void setQuestionId(String question_id)
        {
            question_id = question_id;
        }
        public String getQuestionId()
        {
            return question_id;
        }

        public void setQuestion(String question)
        {
            this.question = question;
        }
        public String getQuestion()
        {
            return question;
        }

        public void setItem1(String item1)
        {
            this.item1 = item1;
        }
        public String getItem1()
        {
            return item1;
        }

        public void setItem2(String item2)
        {
            this.item2 = item2;
        }
        public String getItem2()
        {
            return item2;
        }

        public void setItem3(String item3)
        {
            this.item3 = item3;
        }
        public String getItem3()
        {
            return item3;
        }

        public void setAnswer(String answer)
        {
            this.answer = answer;
        }
        public String getAnswer()
        {
            return answer;
        }

    }
}
