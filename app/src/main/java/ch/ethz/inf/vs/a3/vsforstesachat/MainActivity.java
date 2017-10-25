package ch.ethz.inf.vs.a3.vsforstesachat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private int register(String username) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(PORT);
            socket.setSoTimeout(5000);

        } catch (SocketException se) {

        }
        if (socket != null) {
            UUID uuid = UUID.randomUUID();

            JSONObject register_message = new JSONObject();
            JSONObject header = new JSONObject();
            JSONObject body = new JSONObject();

            try {
                header.put("username", username);
                header.put("uuid", uuid);
                header.put("timestamp", "{}");
                header.put("type", "register");

                register_message.put("header", header);
                register_message.put("body", body);
            } catch (JSONException je) {

            }

            byte[] data = register_message.toString().getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length);

            int ack_length = 0;
            byte[] ack_buffer = new byte[ack_length];
            DatagramPacket ack = new DatagramPacket(ack_buffer, ack_length);
            try {
                socket.send(packet);
                socket.receive(ack);
            } catch (IOException ioe) {

            }
        }
        return 0;
    }

    public void onBtnJoin(View v) {
        EditText username_field = (EditText) findViewById(R.id.username_field);
        String username = username_field.getText().toString();
        System.out.println("DEBUG: username" + username);
        if (register(username) == 0) {
            this.startService(new Intent(this, ChatActivity.class));
        }
    }

    private static final int PORT = 4446;
}
