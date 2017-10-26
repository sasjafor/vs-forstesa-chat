package ch.ethz.inf.vs.a3.vsforstesachat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import static ch.ethz.inf.vs.a3.udpclient.NetworkFunctions.sendMessage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onBtnJoin(View v) {
        EditText username_field = (EditText) findViewById(R.id.username_field);
        username = username_field.getText().toString();
        System.out.println("DEBUG: username=" + username);
        uuid = UUID.randomUUID();
        int res = sendMessage(username, uuid, "register");
        System.out.println("DEBUG: register_res="+res);

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("uuid", uuid);
        if (res == 0) {
            this.startActivity(intent);
        }
    }

    public void onBtnSettings(View v) {
        this.startActivity(new Intent(this, SettingsActivity.class));
    }

    private static String username;
    private static UUID uuid;
}
