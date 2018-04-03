package ninja.zhancheng.popularmovie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import ViewAdapters.ImageAdapter;
import networkServices.tmdbRestServices;

public class MainActivity extends AppCompatActivity {

    private JsonArray bootstrap_data;
    private Boolean popular = true;
    private ProgressBar progressBar;
    //Adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.pb_loading_indicator);


        //Call to populate the boostrap data

        new getMovieTask().execute(popular);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_change_order) {
            if (item.getTitle().equals("By Rating")) {
                item.setTitle("By Popularity");
                this.popular = false;
                new getMovieTask().execute(popular);
            } else if (item.getTitle().equals("By Popularity")){
                item.setTitle("By Rating");
                this.popular = true;
                new getMovieTask().execute(popular);
            }
        }
        return true;
    }

    public void populateView(List<Bitmap> photos){


        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(bootstrap_data, this, photos));
        gridview.setOnItemClickListener(new movieOnItemClickListener(this.bootstrap_data));

    }

    public class movieOnItemClickListener implements AdapterView.OnItemClickListener {
        private JsonArray movieData;

        public movieOnItemClickListener(JsonArray data){
            this.movieData = data;
        }
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            Intent goToDetailIntent = new Intent(MainActivity.this, movieDetailActivity.class);
            JsonObject detailJson = this.movieData.get(i).getAsJsonObject();
            goToDetailIntent.putExtra("detailJson",detailJson.toString());
            startActivity(goToDetailIntent);
        }
    }



    // Task to retrive information from the tmdb rest services
    public class getMovieTask extends AsyncTask<Boolean,Void,List<Bitmap>> {

        private JsonArray data;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Bitmap> doInBackground(Boolean... flags) {
            tmdbRestServices service = tmdbRestServices.getTMDBService();
            List<Bitmap> photos = null;
            try {

                if (flags[0]) {
                    data = service.getPopularMovieList();
                }
                else {data=service.getTopRatedMovieListJson();}

                bootstrap_data = data;
                photos = service.loadPictures(data);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return photos;
        }

        @Override
        protected void onPostExecute(List<Bitmap> photos) {
            super.onPostExecute(photos);

            populateView(photos);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }





}
