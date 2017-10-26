package ch.ethz.inf.vs.a3.vsforstesachat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.UUID;

import static ch.ethz.inf.vs.a3.udpclient.NetworkFunctions.sendMessage;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        uuid = (UUID) extras.get("uuid");
    }

    @Override
    public void onPause() {
        super.onPause();

        sendMessage(username, uuid, "deregister");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sendMessage(username, uuid, "deregister");
    }

    private static String username;
    private static UUID uuid;
}
