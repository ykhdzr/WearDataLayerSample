package sample.trimegah.com.weardatalayersample;

import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import jp.android.a.akira.library.okwear.OkWear;
import jp.android.a.akira.library.okwear.listener.SendResultListener;
import jp.android.a.akira.library.okwear.listener.WearReceiveListener;
import jp.android.a.akira.library.okwear.util.Payload;

public class MainActivity extends AppCompatActivity implements WearReceiveListener {

    private static final String PATH = "/path";

    private static final String USERNAME_KEY = "username";

    private static final String USERPASS_KEY = "pass";

    private TextView textView;

    private Button sendButton;

    private OkWear okWear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLayout();
        initDataLayerApi();
    }

    private void initLayout() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView = (TextView) findViewById(R.id.textMobile);
        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(clickListener);
    }

    private void initDataLayerApi() {
        okWear = new OkWear(this);
        okWear.registReceiver(this);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            syncDataToWear();
        }
    };

    private void syncDataToWear() {
        Payload payload = new Payload.Builder(PATH)
            .addPayload(USERNAME_KEY, "Trim")
            .addPayload(USERPASS_KEY, "pass123")
            .build();

        okWear.syncData(payload, new SendResultListener<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult result) {
                textView.setText("Sending data to mobile ... with status : " + result.getStatus());
            }
        });
    }

    @Override
    public void onReceiveMessage(MessageEvent messageEvent) {
    }

    @Override
    public void onReceiveDataApi(DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            final DataMap dataMap = DataMap.fromByteArray(event.getDataItem().getData());
            final String username = dataMap.getString(USERNAME_KEY);
            final String userpass = dataMap.getString(USERPASS_KEY);
            textView.setText(username + "|" + userpass);
        }
    }
}
