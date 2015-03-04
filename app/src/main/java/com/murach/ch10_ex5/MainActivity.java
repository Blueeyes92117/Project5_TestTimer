package com.murach.ch10_ex5;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    private TextView messageTextView;
    private Button startButton;
    private Button stopButton;
    private Timer timer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageTextView = (TextView) findViewById(R.id.messageTextView);

        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);

        startTimer();
    }

    private void startTimer() {
        final long startMillis = System.currentTimeMillis();
        timer = new Timer(true);
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                long elapsedMillis = System.currentTimeMillis() - startMillis;
                updateView(elapsedMillis);

                downloadFile();
            }
        };
        timer.schedule(task, 0, 1000);
    }

    private void stopTimer() {
        timer.cancel();
    }

    private void updateView(final long elapsedMillis) {
        // UI changes need to be run on the UI thread
        messageTextView.post(new Runnable() {

            int elapsedSeconds = (int) elapsedMillis/1000;
            int downloadCount = elapsedSeconds / 10;

            @Override
            public void run() {
                messageTextView.setText("File downloaded " + downloadCount + " time(s).");
            }
        });
    }

    @Override
    public void onPause() {
        stopTimer();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startButton:
                startTimer();
                break;
            case R.id.stopButton:
                stopTimer();
                break;
        }
    }

    private void downloadFile() {
        final String URL_STRING = "http://www.npr.org/rss/";
        final String FILENAME = "news_feed.xml";
        try{
            // get the URL
            URL url = new URL(URL_STRING);

            // get the input stream
            InputStream in = url.openStream();

            // get the output stream
            FileOutputStream out =
                    openFileOutput(FILENAME, MODE_PRIVATE);

            // read input and write output
            byte[] buffer = new byte[1024];
            int bytesRead = in.read(buffer);
            while (bytesRead != -1)
            {
                out.write(buffer, 0, bytesRead);
                bytesRead = in.read(buffer);
            }
            out.close();
            in.close();
        }
        catch (IOException e) {
            Log.e("News reader", e.toString());
        }
    }
}