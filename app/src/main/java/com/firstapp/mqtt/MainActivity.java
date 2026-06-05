package com.firstapp.mqtt;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private View ledView;
    private Mqtt3AsyncClient client;
    private boolean isOtherLedOn = false;

    private final String TOPIC = "optimuslogic/led_control_project";
    private final String MY_DEVICE_ID = UUID.randomUUID().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ledView = findViewById(R.id.virtualLed);
        Button toggleButton = findViewById(R.id.toggleBtn);

        client = Mqtt3Client.builder()
                .identifier(MY_DEVICE_ID)
                .serverHost("broker.emqx.io")
                .serverPort(8883)
                .sslWithDefaultConfig()
                .buildAsync();

        client.connect().whenComplete((connAck, throwable) -> {
            if (throwable == null) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Connected to Broker!", Toast.LENGTH_SHORT).show());

                client.subscribeWith()
                        .topicFilter(TOPIC)
                        .callback(publish -> {
                            String message = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
                            String[] parts = message.split(":");
                            String senderId = parts[0];
                            String command = parts[1];

                            if (!senderId.equals(MY_DEVICE_ID)) {
                                runOnUiThread(() -> {
                                    if (command.equals("TOGGLE")) {
                                        if (isOtherLedOn) {
                                            ledView.setBackgroundColor(0xFFFF0000); // Red
                                            isOtherLedOn = false;
                                        } else {
                                            ledView.setBackgroundColor(0xFF00FF00); // Green
                                            isOtherLedOn = true;
                                        }
                                    }
                                });
                            }
                        })
                        .send();
            } else {
                
            String exactError = throwable.getCause() != null ? throwable.getCause().toString() : throwable.getMessage();
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "ERROR: " + exactError, Toast.LENGTH_LONG).show());
        }
        });

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageToSend = MY_DEVICE_ID + ":TOGGLE";

                client.publishWith()
                        .topic(TOPIC)
                        .payload(messageToSend.getBytes(StandardCharsets.UTF_8))
                        .send();
            }
        });
    }
}