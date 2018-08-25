package skiteapps.gkadda;

/**
 * Created by Abdelrahman Hesham on 10/27/2017.
 */

public class QuestionShortQA extends Quiz {

    private String questionId;
    private String questionNumber;
    private String question;
    private String answer;

    public QuestionShortQA(String topicId, String topicName, String quizName, String quizId, String questionId, String questionNumber, String question, String answer) {
        super(topicId, topicName, quizName, quizId);
        this.questionId = questionId;
        this.questionNumber = questionNumber;
        this.question = question;
        this.answer = answer;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getQuestionNumber() {
        return questionNumber;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
