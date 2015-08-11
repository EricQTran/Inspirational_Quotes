package com.example.eric.inspirationalquotes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Credentials;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import net.dean.jraw.RedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //global variables
    int count = 0;
    UserAgent myUserAgent;
    RedditClient redditClient;
    net.dean.jraw.http.oauth.Credentials credentials;
    ArrayList<Quote> quoteList;
    RelativeLayout touch;
    TextView personText;
    TextView quoteText;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Allow for background process in main thread
         StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
         StrictMode.setThreadPolicy(policy);

        //set global variables
        touch = (RelativeLayout) findViewById(R.id.touch);
        quoteText = (TextView) findViewById(R.id.quote);
        personText = (TextView) findViewById(R.id.person);
        quoteList = new ArrayList<Quote>();
        myUserAgent = UserAgent.of("desktop", "Inspirational Quotes", "0.1", "Eric");
        redditClient = new RedditClient(myUserAgent);
        credentials = net.dean.jraw.http.oauth.Credentials.script("vulturesama", "eric10208", "QzjE4_gH5QrsqA", "JUGdGBG3stBKgebfJbhvIMDxRvc");

        new getQuotesFromReddit().execute();
    }


    private void getQuotes()
    {
        try {
            //receive credentials from Reddit
            OAuthData authData = redditClient.getOAuthHelper().easyAuth(credentials);
            redditClient.authenticate(authData);

            touch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Retrieve the quote from Reddit
                    Submission quoteSub = redditClient.getRandomSubmission("quotes");
                    String quoteTitle = quoteSub.getTitle();
                    Quote quote = new Quote(quoteTitle,"");
                    quoteList.add(quote);

                    if (count < quoteList.size()) {

                        Quote q = quoteList.get(count);

                        quoteText.setText(q.getQuote());

                        personText.setText(q.getPerson());

                        count = count + 1;

                    } else {
                        count = 0;
                    }
                }
            });

        }
        catch (OAuthException e)
        {
            e.printStackTrace();
        }
    }

    //Class allows for background processing of quote retrieval
    private class getQuotesFromReddit extends AsyncTask
    {
        protected Object doInBackground(Object... arg0)
        {
            getQuotes();
            return null;
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
