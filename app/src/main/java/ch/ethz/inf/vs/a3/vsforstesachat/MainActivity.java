package ch.ethz.inf.vs.a3.vsforstesachat;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.view.View;
import android.widget.EditText;

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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private int register() {
        DatagramSocket socket;
        try {
            socket = new DatagramSocket(UDP_PORT);
            socket.setSoTimeout(SOCKET_TIMEOUT);
        } catch (SocketException se) {
            return 1; //error
        }
        UUID uuid = UUID.randomUUID();

        JSONObject register_message = makeMessage(username, uuid, "register");

        byte[] data = register_message.toString().getBytes();
        InetAddress addr;
        try {
            addr = Inet4Address.getByAddress(SERVER_ADDRESS.getBytes());
        } catch (UnknownHostException uhe) {
            return 1;
        }
        DatagramPacket packet = new DatagramPacket(data, PAYLOAD_SIZE, addr, UDP_PORT);

        byte[] ack_buffer = new byte[PAYLOAD_SIZE];
        DatagramPacket ack = new DatagramPacket(ack_buffer, PAYLOAD_SIZE);

        for (int k = 0; k < RETRIES; k++) {
            try {
                socket.send(packet);
                socket.receive(ack);
                break;
            } catch (SocketTimeoutException ste) {

            } catch (IOException ioe) {
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

    public void onBtnJoin(View v) {
        EditText username_field = (EditText) findViewById(R.id.username_field);
        username = username_field.getText().toString();
        System.out.println("DEBUG: username=" + username);
        int res = register();
        System.out.println("DEBUG: register_res="+res);
        if (res == 0) {
            this.startActivity(new Intent(this, ChatActivity.class));
        }
    }

    public void onBtnSettings(View v) {
        this.startActivity(new Intent(this, Settings.class));
    }

    private JSONObject makeMessage(String username, UUID uuid, String type) {
        JSONObject message = new JSONObject();
        JSONObject header = new JSONObject();
        JSONObject body = new JSONObject();

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
    private static String username;
}
