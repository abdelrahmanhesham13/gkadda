package skiteapps.gkadda;

/**
 * Created by Abdelrahman Hesham on 10/26/2017.
 */

public class Question extends Quiz {

    private String questionId;
    private String questionNumber;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;
    private String explain;
    private String correctAnswer;

    public Question(String topicId, String topicName, String quizName, String quizId, String questionId, String questionNumber, String question, String optionA, String optionB, String optionC, String optionD, String answer, String explain, String correctAnswer) {
        super(topicId, topicName, quizName, quizId);
        this.questionId = questionId;
        this.questionNumber = questionNumber;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.answer = answer;
        this.explain = explain;
        this.correctAnswer = correctAnswer;
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

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public String getAnswer() {
        return answer;
    }

    public String getExplain() {
        return explain;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
