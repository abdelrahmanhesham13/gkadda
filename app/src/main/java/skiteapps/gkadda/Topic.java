package skiteapps.gkadda;

import java.io.Serializable;

/**
 * Created by Abdelrahman Hesham on 10/23/2017.
 */

public class Topic extends Subject implements Serializable {
    private String topicId;
    private String topicName;
    private String topicStatue;

    public Topic(int subjectId, String subjectName, String topicId, String topicName, String topicStatue) {
        super(subjectId, subjectName);
        this.topicId = topicId;
        this.topicName = topicName;
        this.topicStatue = topicStatue;
    }

    public String getTopicId() {
        return topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getTopicStatue() {
        return topicStatue;
    }
}
