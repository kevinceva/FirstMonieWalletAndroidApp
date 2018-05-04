package com.ceva.ubmobile.models;

/**
 * Created by brian on 02/03/2017.
 */

public class SecurityQuestionsModel {
    public static final String KEY_QUESTIONS = "secquestions";
    public static final String KEY_QST = "SQS";
    public static final String KEY_QST_ID = "SQ_ID";
    public static final String KEY_CUSTMOBILE = "customermobile";
    public static final String KEY_ACCOUNT = "accountnumber";
    public static final String KEY_USERNAMES = "usernames";
    public static final String KEY_STATUS = "userstatus";
    public static final String KEY_PIN = "pin";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_SELID = "usernameSelected";


    private String question;
    private String questionID;

    public SecurityQuestionsModel(String question, String questionID) {
        this.question = question;
        this.questionID = questionID;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }
}
