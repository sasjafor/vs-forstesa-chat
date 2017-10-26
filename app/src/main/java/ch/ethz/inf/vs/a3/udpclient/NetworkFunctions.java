package ch.ethz.inf.vs.a3.udpclient;

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

import static ch.ethz.inf.vs.a3.udpclient.NetworkConsts.PAYLOAD_SIZE;
import static ch.ethz.inf.vs.a3.udpclient.NetworkConsts.SERVER_ADDRESS;
import static ch.ethz.inf.vs.a3.udpclient.NetworkConsts.SOCKET_TIMEOUT;
import static ch.ethz.inf.vs.a3.udpclient.NetworkConsts.UDP_PORT;

public class NetworkFunctions {

    public static int sendMessage(String username, UUID uuid, String type) {
        //create socket
        DatagramSocket socket;

        //try to create socket
        try {
            socket = new DatagramSocket(UDP_PORT);
            socket.setSoTimeout(SOCKET_TIMEOUT);
        } catch (SocketException se) {
            se.printStackTrace();
            return 1; //error
        }

        //create message
        JSONObject message = makeMessage(username, uuid, type);

        //convert to byte array
        byte[] data = message.toString().getBytes();

        //try to create address object
        InetAddress addr;
        try {
            addr = Inet4Address.getByName(SERVER_ADDRESS);
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
            return 1;
        }

        //create packet
        DatagramPacket packet = new DatagramPacket(data, PAYLOAD_SIZE, addr, UDP_PORT);

        //create buffer for ack
        byte[] ack_buffer = new byte[PAYLOAD_SIZE];

        //create packet for receiving ack
        DatagramPacket ack = new DatagramPacket(ack_buffer, PAYLOAD_SIZE);

        //try to send message and receive ack RETRIES many times
        for (int k = 0; k < RETRIES; k++) {
            try {
                socket.send(packet);
                socket.receive(ack);
                break;
            } catch (SocketTimeoutException ste) {

            } catch (IOException ioe) {
                ioe.printStackTrace();
                return 1; //error
            }
        }

        System.out.println("DEBUG: raw_ack" + ack);
        try {
            System.out.println("DEUBG: ack=" + new String(ack.getData(), "UTF-8"));
        } catch (UnsupportedEncodingException uee) {

        }
        return 0;
    }

    private static JSONObject makeMessage(String username, UUID uuid, String type) {
        //create json objects
        JSONObject message = new JSONObject();
        JSONObject header = new JSONObject();
        JSONObject body = new JSONObject();

        //insert data into message json object
        try {
            header.put("username", username);
            header.put("uuid", uuid);
            header.put("timestamp", "{}");
            header.put("type", type);

            message.put("header", header);
            message.put("body", body);
        } catch (JSONException je) {

        }
        return message;
    }

    private static final int RETRIES = 5;
}
