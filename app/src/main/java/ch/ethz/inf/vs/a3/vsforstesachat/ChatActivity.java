package ch.ethz.inf.vs.a3.vsforstesachat;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import ch.ethz.inf.vs.a3.queue.PriorityQueue;
import ch.ethz.inf.vs.a3.message.Message;
import static ch.ethz.inf.vs.a3.udpclient.NetworkFunctions.sendMessage;

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

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                return sendMessage((String) params[0], (UUID) params[1], (String) params[2]);
            }
        };
        task.execute(new Object[]{username, uuid, "retrieve_chat_log"});

        PriorityQueue<Message> chat_log = null;

        try {
            chat_log = (PriorityQueue<Message>) task.get();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (ExecutionException ee) {
            ee.printStackTrace();
        }

        if (chat_log == null) {
            Toast toast = Toast.makeText(this, R.string.chat_log_error,Toast.LENGTH_LONG);
            toast.show();
        } else if (chat_log.isEmpty()) {
            Toast toast = Toast.makeText(this, R.string.no_messages, Toast.LENGTH_LONG);
            toast.show();
        } else {
            int length = chat_log.size();
            for (int i = 0; i < length; i++) {
                String text = chat_log.poll().content.toString();
                textView.append(text + "\n");
            }
        }

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
    private TextView textView;
}
