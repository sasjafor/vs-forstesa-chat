package ch.ethz.inf.vs.a3.message;

import java.util.UUID;

import ch.ethz.inf.vs.a3.clock.VectorClock;

/**
 * Created by Christian on 28.10.17.
 */

public class Message {

    public Message(String username, String uuid, String timestamp, String type, String content) {
        this.username = username;
        this.uuid = UUID.fromString(uuid);
        this.type = type;
        this.timestamp = new VectorClock();

        // If the message is a chat message, read the timestamp into a VectorClock
        if (type.equals("message")) {
            this.timestamp.setClockFromString(timestamp);
            this.content = content;
        }
        else {
            this.timestamp = null;
            this.content = null;
        }
    }

    public String username, type, content;
    public VectorClock timestamp;
    public UUID uuid;
}
