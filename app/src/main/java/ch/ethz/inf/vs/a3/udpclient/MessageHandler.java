package ch.ethz.inf.vs.a3.udpclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.UUID;

import ch.ethz.inf.vs.a3.queue.PriorityQueue;
import ch.ethz.inf.vs.a3.message.Message;
import ch.ethz.inf.vs.a3.message.MessageComparator;

import static ch.ethz.inf.vs.a3.message.MessageTypes.ACK_MESSAGE;
import static ch.ethz.inf.vs.a3.message.MessageTypes.CHAT_MESSAGE;
import static ch.ethz.inf.vs.a3.message.MessageTypes.DEREGISTER;
import static ch.ethz.inf.vs.a3.message.MessageTypes.REGISTER;
import static ch.ethz.inf.vs.a3.message.MessageTypes.RETRIEVE_CHAT_LOG;
import static ch.ethz.inf.vs.a3.udpclient.NetworkConsts.PAYLOAD_SIZE;
import static ch.ethz.inf.vs.a3.udpclient.NetworkConsts.SERVER_ADDRESS;
import static ch.ethz.inf.vs.a3.udpclient.NetworkConsts.SOCKET_TIMEOUT;
import static ch.ethz.inf.vs.a3.udpclient.NetworkConsts.UDP_PORT;

public class MessageHandler extends AsyncTask<Object, Object, Object>{

    public MessageHandler(Context context) {
        this.context = context;
    }

    private static PriorityQueue<Message> sendMessage(String username, UUID uuid, String type) {
        //create socket
        DatagramSocket socket;

        //try to create socket
        try {
            socket = new DatagramSocket(UDP_PORT);
            socket.setSoTimeout(SOCKET_TIMEOUT);
        } catch (SocketException se) {
            se.printStackTrace();
            return null; //error
        }

        //create message
        JSONObject message;
        try {
            message = makeMessage(username, uuid, type);
        } catch (JSONException je) {
            je.printStackTrace();
            socket.close();
            return null; //error
        }

        //convert to byte array
        byte[] data = message.toString().getBytes();

        //try to create address object
        InetAddress addr;
        try {
            addr = Inet4Address.getByName(SERVER_ADDRESS);
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
            socket.close();
            return null; //error
        }
        System.out.println("DEBUG: addr="+addr.getHostAddress());

        //create packet
        DatagramPacket packet = new DatagramPacket(data, data.length, addr, UDP_PORT);

        // handling different types of messages
        switch (type) {

            case REGISTER: case DEREGISTER:

                //create buffer for ack
                byte[] ack_buffer = new byte[PAYLOAD_SIZE];

                //create packet for receiving ack
                DatagramPacket ack = new DatagramPacket(ack_buffer, PAYLOAD_SIZE);

                //try to send message and receive ack RETRIES many times
                for (int k = 0; k < TRIES; k++) {
                    try {
                        socket.send(packet);
                        socket.receive(ack);
                        break;
                    } catch (SocketTimeoutException ste) {
                        //retry
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                        socket.close();
                        return null; //error
                    }
                }

                String json_ack_string;
                try {
                    json_ack_string = new String(ack.getData(), 0, ack.getLength(), "UTF-8");
                    System.out.println("DEBUG: ack=" + json_ack_string);
                } catch (UnsupportedEncodingException uee) {
                    socket.close();
                    return null; //error
                }
                JSONObject json_ack;
                try {
                    json_ack = new JSONObject(json_ack_string);
                    JSONObject header = json_ack.getJSONObject("header");
                    if (!(header.getString("type").equals(ACK_MESSAGE) &&
                            header.getString("username").equals("server"))) {
                        socket.close();
                        return null; //error
                    }
                } catch (JSONException je) {
                    je.printStackTrace();
                    socket.close();
                    return null; //error
                }

                //close socket
                socket.close();

                return new PriorityQueue<>(null);

            case RETRIEVE_CHAT_LOG:

                byte[] chat_message_buffer = new byte[PAYLOAD_SIZE];
                DatagramPacket chat_message = new DatagramPacket(chat_message_buffer, PAYLOAD_SIZE);

                // Send the retrieve_chat_log message once
                try {
                    socket.send(packet);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    socket.close();
                    return null; //error
                }

                // receive chat messages, create a JSONObject for each and add them to the priority queue until there is a socket timeout (all messages are sent)
                String json_message_string;
                JSONObject json_message;
                Message message_obj;

                MessageComparator comparator = new MessageComparator();
                PriorityQueue<Message> chat_log = new PriorityQueue<>(comparator);
                while (true) {
                    // receive
                    try {
                        socket.receive(chat_message);
                    } catch (SocketTimeoutException ste) {
                        // need break statement because else the last message received gets appended twice to the priority list
                        break;
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                        socket.close();
                        return null; //error
                    }

                    // create JSONObject
                    try {
                        json_message_string = new String(chat_message.getData(), 0, chat_message.getLength(), "UTF-8");
                    } catch (UnsupportedEncodingException uee) {
                        socket.close();
                        return null; //error
                    }

                    try {
                        json_message = new JSONObject(json_message_string);
                        JSONObject header = json_message.getJSONObject("header");
                        JSONObject body = json_message.getJSONObject("body");
                        if (!(header.getString("type").equals(CHAT_MESSAGE))) {
                            socket.close();
                            return null; //error
                        }
                        // create Message object from JSONObject
                        message_obj = new Message(header.getString("username"), header.getString("uuid"),
                                header.getString("timestamp"), header.getString("type"), body.getString("content"));
                        chat_log.add(message_obj);

                    } catch (JSONException je) {
                        je.printStackTrace();
                        socket.close();
                        return null; //error
                    }

                }

                //close socket
                socket.close();

                return chat_log;

            default: return null;
        }


    }

    private static JSONObject makeMessage(String username, UUID uuid, String type) throws JSONException{
        //create json objects
        JSONObject message = new JSONObject();
        JSONObject header = new JSONObject();
        JSONObject body = new JSONObject();

        //insert data into message json object
        header.put("username", username);
        header.put("uuid", uuid);
        header.put("timestamp", "{}");
        header.put("type", type);

        message.put("header", header);
        message.put("body", body);
        return message;
    }



    @Override
    protected PriorityQueue<Message> doInBackground(Object[] params) {
        return sendMessage((String) params[0], (UUID) params[1], (String) params[2]);
    }

    @Override
    protected void onPostExecute(Object result) {
        Intent intent = new Intent("COMMUNICATION_FINISHED");
        context.sendBroadcast(intent);
    }

    private static final int TRIES = 6;
    @SuppressLint("StaticFieldLeak")
    private Context context;
}
