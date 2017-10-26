package ch.ethz.inf.vs.a3.vsforstesachat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import static ch.ethz.inf.vs.a3.udpclient.NetworkConsts.SERVER_ADDRESS;
import static ch.ethz.inf.vs.a3.udpclient.NetworkConsts.UDP_PORT;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        address = (EditText) findViewById(R.id.server_address);
        port = (EditText) findViewById(R.id.server_port);

        address.setText(SERVER_ADDRESS);
        port.setText(Integer.toString(UDP_PORT));
    }

    public void onBtnSave(View v) {
        SERVER_ADDRESS = address.getText().toString();
        UDP_PORT = Integer.parseInt(port.getText().toString());
    }

    private EditText address, port;
}
