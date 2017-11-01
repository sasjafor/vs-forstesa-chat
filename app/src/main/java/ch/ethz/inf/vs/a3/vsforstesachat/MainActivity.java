package ch.ethz.inf.vs.a3.vsforstesachat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import ch.ethz.inf.vs.a3.queue.PriorityQueue;
import ch.ethz.inf.vs.a3.message.Message;
import ch.ethz.inf.vs.a3.udpclient.MessageHandler;

import static ch.ethz.inf.vs.a3.message.MessageTypes.REGISTER;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentFilter = new IntentFilter("COMMUNICATION_FINISHED");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context p_context, Intent p_intent) {
                PriorityQueue<Message> res = null;
                try {
                    res = (PriorityQueue<Message>) handler.get();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                } catch (ExecutionException ee) {
                    ee.printStackTrace();
                }
                System.out.println("DEBUG: register_res="+res);

                //launch chat activity
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("uuid", uuid);
                if (res == null) {
                    Toast toast = Toast.makeText(MainActivity.this, R.string.register_error,Toast.LENGTH_LONG);
                    toast.show();
                } else if (res.isEmpty()){
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, R.string.register_error,Toast.LENGTH_LONG);
                    toast.show();
                }
                unregisterReceiver(broadcastReceiver);
                registered = false;
                Button btn = (Button) findViewById(R.id.btn_join);
                btn.setEnabled(true);
            }
        };
    }

    public void onBtnJoin(View v) {
        Button btn = (Button) v;
        btn.setEnabled(false);

        //retrieve username
        EditText username_field = (EditText) findViewById(R.id.username_field);
        username = username_field.getText().toString();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {
            Toast toast = Toast.makeText(this, R.string.network_error, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        if (username.isEmpty()) {
            Toast toast = Toast.makeText(this, R.string.username_error, Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        Log.d("onBtnJoin", "username=" + username);

        //generate uuid
        uuid = UUID.randomUUID();

        //register user using async task
        /*AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                return sendMessage((String) params[0], (UUID) params[1], (String) params[2]);
            }
        };*/
        handler = new MessageHandler(this);
        handler.execute(new Object[]{username, uuid, REGISTER});

        registerReceiver(broadcastReceiver, intentFilter);
        registered = true;
    }

    public void onBtnSettings(View v) {
        //launch settings activity
        this.startActivity(new Intent(this, SettingsActivity.class));
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (registered) {
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registered) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    private static String username;
    private static UUID uuid;
    private static BroadcastReceiver broadcastReceiver;
    private static IntentFilter intentFilter;
    private static MessageHandler handler;
    private static boolean registered;
}
