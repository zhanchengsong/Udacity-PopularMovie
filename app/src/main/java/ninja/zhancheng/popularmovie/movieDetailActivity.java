package ninja.zhancheng.popularmovie;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.Date;

import networkServices.tmdbRestServices;

public class movieDetailActivity extends AppCompatActivity {
    private TextView detailRating;
    private TextView movieTitle;
    private TextView detailYear;
    private TextView detailDuration;
    private TextView detailDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Gson gson = new Gson();
        Intent fromHomePage = getIntent();
        JsonObject detailJson = gson.fromJson(fromHomePage.getStringExtra("detailJson"),JsonObject.class);
        this.setTitle("Move Detail");
        String picturePath = detailJson.get("poster_path").getAsString();
        Log.i("Detail Picasso", "Loading " + "http://image.tmdb.org/t/p/w185" + picturePath);
        Picasso.with(movieDetailActivity.this)
                .load("http://image.tmdb.org/t/p/w500" + picturePath)
                .into((ImageView)findViewById(R.id.iv_detailImage));

        //Adds the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        movieTitle = findViewById(R.id.tv_detail_title);
        movieTitle.setText(detailJson.get("title").getAsString());
        detailYear = findViewById(R.id.detailYear);

        String r_date = detailJson.get("release_date").getAsString();
        detailYear.setText(r_date.substring(0,r_date.indexOf("-")));

        new getMovieDetailTask().execute(detailJson.get("id").getAsInt());
    }
    //Populate UI for detail page
    public void populateDetailUI(JsonObject detailJson){
        detailDuration = findViewById(R.id.detailDuration);
        detailDuration.setText(detailJson.get("runtime").getAsString() + " mins");
        detailRating = findViewById(R.id.detailRating);
        detailRating.setText(detailJson.get("vote_average").getAsString() + " /10");
        detailDescription = findViewById(R.id.detailDescription);
        detailDescription.setText(detailJson.get("overview").getAsString());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent backhome = new Intent(this, MainActivity.class);
            startActivity(backhome);
        }
        return true;
    }

    //Get movie detail task

    public class getMovieDetailTask extends AsyncTask<Integer, Void, JsonObject> {

        private tmdbRestServices service = tmdbRestServices.getTMDBService();
        @Override
        protected JsonObject doInBackground(Integer... ids) {

            try {
                return service.getMovieDetailById(ids[0].toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(JsonObject jsonObject) {
            super.onPostExecute(jsonObject);
            populateDetailUI(jsonObject);
        }
    }
}
