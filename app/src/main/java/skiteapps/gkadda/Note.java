package skiteapps.gkadda;

/**
 * Created by Abdelrahman Hesham on 10/30/2017.
 */

public class Note {
    private String topicId;
    private String topicName;
    private String title;
    private String paragraphId;
    private String head;
    private String content;

    public Note(String topicId, String topicName, String title, String paragraphId, String head, String content) {
        this.topicId = topicId;
        this.topicName = topicName;
        this.title = title;
        this.paragraphId = paragraphId;
        this.head = head;
        this.content = content;
    }

    public String getTopicId() {
        return topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getTitle() {
        return title;
    }

    public String getParagraphId() {
        return paragraphId;
    }

    public String getHead() {
        return head;
    }

    public String getContent() {
        return content;
    }
}
