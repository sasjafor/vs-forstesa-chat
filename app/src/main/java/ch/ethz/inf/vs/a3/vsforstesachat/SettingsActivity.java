package ch.ethz.inf.vs.a3.vsforstesachat;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static ch.ethz.inf.vs.a3.udpclient.NetworkConsts.SERVER_ADDRESS;
import static ch.ethz.inf.vs.a3.udpclient.NetworkConsts.UDP_PORT;

public class SettingsActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //get text fields
        address_field = (EditText) findViewById(R.id.server_address);
        port_field= (EditText) findViewById(R.id.server_port);

        //set default values
        address_field.setText(SERVER_ADDRESS);
        port_field.setText(Integer.toString(UDP_PORT));
    }

    public void onBtnSave(View v) {

        //get address from text field
        String address = address_field.getText().toString();

        //check format and assign to constant if it matches
        if(Patterns.IP_ADDRESS.matcher(address).matches()) {
            SERVER_ADDRESS = address;
        } else {
            Toast toast = Toast.makeText(this, R.string.address_format_error, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        //check port range and get port from text field and assign to constant
        int port = Integer.parseInt(port_field.getText().toString());
        if (port >= 0 && port <= 65535) {
            UDP_PORT = port;
        }

    }

    private EditText address_field, port_field;
}
