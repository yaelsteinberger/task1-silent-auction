package client.entity;


public class Question {
    private final String question;
    private final String answerLabel;
    private final Class answerTypeClass;

    public Question(String question, String answerLabel, Class answerTypeClass) {
        this.question = question;
        this.answerLabel = answerLabel;
        this.answerTypeClass = answerTypeClass;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswerLabel() {
        return answerLabel;
    }

    public Class getAnswerTypeClass() {
        return answerTypeClass;
    }
}