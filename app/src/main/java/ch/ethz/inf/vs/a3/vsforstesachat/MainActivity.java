package ch.ethz.inf.vs.a3.vsforstesachat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.UUID;

import static ch.ethz.inf.vs.a3.udpclient.NetworkFunctions.sendMessage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onBtnJoin(View v) {
        //retrieve username
        EditText username_field = (EditText) findViewById(R.id.username_field);
        username = username_field.getText().toString();
        System.out.println("DEBUG: username=" + username);

        //generate uuid
        uuid = UUID.randomUUID();

        //register user
        int res = sendMessage(username, uuid, "register");
        System.out.println("DEBUG: register_res="+res);

        //launch chat activity
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("uuid", uuid);
        if (res == 0) {
            this.startActivity(intent);
        }
    }

    public void onBtnSettings(View v) {
        //launch settings activity
        this.startActivity(new Intent(this, SettingsActivity.class));
    }

    private static String username;
    private static UUID uuid;
}
