package com.kiss.proto.rocketmq.batch;

import org.apache.rocketmq.common.message.Message;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author zhangziyao
 * @date 2020/12/29 9:36 下午
 */
public class MessageCutting implements Iterator<List<Message>> {

    private int maxSize = 100;

    private final List<Message> messages;

    private int currIndex;

    public MessageCutting(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public boolean hasNext() {
        return currIndex < messages.size();
    }

    @Override
    public List<Message> next() {
        int nextIndex = currIndex;
        int totalSize = 0;
        for (; nextIndex < messages.size(); nextIndex++) {
            Message message = messages.get(nextIndex);
            int tempSize = message.getTopic().length() + message.getBody().length;
            Map<String, String> properties = message.getProperties();
            for (String key : properties.keySet()) {
                tempSize = tempSize + (key.length() + properties.get(key).length());
            }
            tempSize = tempSize + 20;
            if (tempSize > maxSize) {
                //it is unexpected that single message exceeds the sizeLimit
                //here just let it go, otherwise it will block the splitting process
                if (nextIndex - currIndex == 0) {
                    //if the next sublist has no element, add this one and then break, otherwise just break
                    nextIndex++;
                }
                break;
            }
            if (tempSize + totalSize > maxSize) {
                break;
            } else {
                totalSize += tempSize;
            }

        }

        List<Message> subList = messages.subList(currIndex, nextIndex);
        currIndex = nextIndex;
        return subList;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not allowed to remove");
    }
}
