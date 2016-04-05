package sample.trimegah.com.weardatalayersample;

import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

import jp.android.a.akira.library.okwear.OkWear;
import jp.android.a.akira.library.okwear.listener.SendResultListener;
import jp.android.a.akira.library.okwear.listener.WearReceiveListener;
import jp.android.a.akira.library.okwear.util.Payload;

public class MainActivity extends Activity implements View.OnClickListener, WearReceiveListener {

    private static final String TAG = MainActivity.class.getSimpleName();

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

    @Override
    protected void onResume() {
        super.onResume();
        okWear.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        okWear.disconnect();
    }

    private void initLayout() {
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                textView = (TextView) stub.findViewById(R.id.text);
                sendButton = (Button) stub.findViewById(R.id.sendButton);
                sendButton.setOnClickListener(MainActivity.this);
            }
        });
    }

    private void initDataLayerApi() {
        okWear = new OkWear(this);
        okWear.registReceiver(this);
    }

    private void syncDataToMobile() {
        int randomId = new Random().nextInt();
        Payload payload = new Payload.Builder(OkWear.DEFAULT_DATA_API_PATH)
            .addPayload(USERNAME_KEY, "Trim")
            .addPayload(USERPASS_KEY, "pass123" + randomId)
            .build();

        okWear.syncData(payload, new SendResultListener<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult result) {
                Log.v(TAG, result.getStatus().toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        syncDataToMobile();
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(username + "|" + userpass);
                }
            });
        }
    }
}
