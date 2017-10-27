package ch.ethz.inf.vs.a3.vsforstesachat;

import android.os.AsyncTask;
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

        /*AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                return sendMessage((String) params[0], (UUID) params[1], (String) params[2]);
            }
        };
        task.execute(new Object[]{username, uuid, "deregister"});*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                return sendMessage((String) params[0], (UUID) params[1], (String) params[2]);
            }
        };
        task.execute(new Object[]{username, uuid, "deregister"});
    }

    private static String username;
    private static UUID uuid;
}
