package ch.ethz.inf.vs.a3.vsforstesachat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import ch.ethz.inf.vs.a3.queue.PriorityQueue;
import ch.ethz.inf.vs.a3.message.Message;
import ch.ethz.inf.vs.a3.udpclient.MessageHandler;

import static ch.ethz.inf.vs.a3.message.MessageTypes.DEREGISTER;
import static ch.ethz.inf.vs.a3.message.MessageTypes.REGISTER;
import static ch.ethz.inf.vs.a3.message.MessageTypes.RETRIEVE_CHAT_LOG;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        uuid = (UUID) extras.get("uuid");

        textView = (TextView) findViewById(R.id.messages);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }

    public void onBtnRetrieve(View v) {

        textView.setText("");

        /*AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                return sendMessage((String) params[0], (UUID) params[1], (String) params[2]);
            }
        };*/
        handler = new MessageHandler(this);
        handler.execute(new Object[]{username, uuid, RETRIEVE_CHAT_LOG});
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("COMMUNICATION_FINISHED");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context p_context, Intent p_intent) {
                PriorityQueue<Message> chat_log = null;

                try {
                    chat_log = (PriorityQueue<Message>) handler.get();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                } catch (ExecutionException ee) {
                    ee.printStackTrace();
                }

                if (chat_log == null) {
                    Toast toast = Toast.makeText(ChatActivity.this, R.string.chat_log_error,Toast.LENGTH_LONG);
                    toast.show();
                } else if (chat_log.isEmpty()) {
                    Toast toast = Toast.makeText(ChatActivity.this, R.string.no_messages, Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    int length = chat_log.size();
                    for (int i = 0; i < length; i++) {
                        String text = chat_log.poll().content.toString();
                        textView.append(text + "\n");
                    }
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();

        handler = new MessageHandler(this);
        handler.execute(new Object[]{username, uuid, DEREGISTER});
    }

    @Override
    public void onRestart() {
        super.onRestart();

        handler = new MessageHandler(this);
        handler.execute(new Object[]{username, uuid, REGISTER});
    }

    private static String username;
    private static UUID uuid;
    private TextView textView;
    private BroadcastReceiver broadcastReceiver;
    private static MessageHandler handler;
}
