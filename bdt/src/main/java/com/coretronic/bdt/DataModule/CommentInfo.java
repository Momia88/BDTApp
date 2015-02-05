package com.coretronic.bdt.DataModule;

import java.util.List;

/**
 * Created by james on 2014/8/21.
 */
public class CommentInfo {

    private String msgCode;
    private String status;
    private String doctor_id;
    private String doctor_name;
    private String division;
    private String person_num;
    private List<Comment> comment;
    private String appraise;


    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public String getDivision()
    {
        return division;
    }

    public String getPersonNum()
    {
        return person_num;
    }

    public String getAppraise()
    {
        return appraise;
    }

    public List<Comment>  getComment()
    {
        return comment;
    }


    public class Comment
    {
        private String question;
        private List<String> answers;

        public String getQuetsion()
        {
            return question;
        }

        public List<String> getAnswers()
        {
            return answers;
        }

    }

}
