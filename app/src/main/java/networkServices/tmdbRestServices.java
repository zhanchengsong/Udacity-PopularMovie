package networkServices;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhancheng-ibm on 3/27/18.
 */

public class tmdbRestServices {
    private String baseUrl;
    private String apiKey;
    private Gson _gson;
    private HttpUrl.Builder httpbuilder;
    private static tmdbRestServices _service;
    private OkHttpClient client;
    private JsonArray popularMovieListJson;
    private JsonArray topRatedMovieListJson;
    //Private constructor for Singleton
    private tmdbRestServices(){
        this.baseUrl = "api.themoviedb.org";
        //TODO: Remove API key before commit/ push
        this.apiKey = "YOUR_API_KEY";
        this._gson = new Gson();
        this.httpbuilder = new HttpUrl.Builder();
        this.client = new OkHttpClient();
        this.popularMovieListJson = null;
        this.topRatedMovieListJson = null;
    }

    //Singleton factory method
    public static tmdbRestServices getTMDBService (){
        if (_service ==null){ _service = new tmdbRestServices(); }
        return _service;
    }
    //Rest call using HttpClient
    public JsonArray getPopularMovieList() throws Exception{

        if (this.popularMovieListJson!=null ) {
            return popularMovieListJson;
        }
        HttpUrl getPopularMovieListURL =
                httpbuilder.scheme("https")
                .host(baseUrl)
                        .addPathSegment("3")
                        .addPathSegment("discover")
                        .addPathSegment("movie")
                .addQueryParameter("sort_by","popularity.desc")
                .addQueryParameter("api_key",apiKey)
                        .addQueryParameter("include_adult", "false")
                        .addQueryParameter("include_video", "false")
                        .addQueryParameter("page","1")
                        .addQueryParameter("language", "en-US")
                        .build();


        Request popularmovieListRequest = new Request.Builder()
                                            .url(getPopularMovieListURL)
                                            .build();
        Response popularmovieListResponse = client.newCall(popularmovieListRequest).execute();
        String res_body = popularmovieListResponse.body().string();
        Log.i("REST Service", "GET " + getPopularMovieListURL.toString()
                                            + "\n" + "Response " + popularmovieListResponse.toString()
                                            + "\n" + "Body " + res_body);

        if (res_body!=null) {
            this.popularMovieListJson = _gson.fromJson(res_body,JsonObject.class).getAsJsonArray("results");

        }
        return popularMovieListJson;
    }

    public JsonArray getTopRatedMovieListJson() throws Exception{
        if (this.topRatedMovieListJson!=null) {
            return topRatedMovieListJson;
        }
        this.httpbuilder = new HttpUrl.Builder();
        HttpUrl topRatedMovieURL =
                httpbuilder.scheme("https")
                        .host(baseUrl)
                        .addPathSegment("3")
                        .addPathSegment("movie")
                        .addPathSegment("top_rated")
                        .addQueryParameter("api_key", apiKey)
                        .build();
        Request topRatedMovieRequest = new Request.Builder().url(topRatedMovieURL).build();
        Response topRatedMovieResponse = client.newCall(topRatedMovieRequest).execute();

        String res_body = topRatedMovieResponse.body().string();
        Log.i("REST Service", "GET " + topRatedMovieURL.toString()
                + "\n" + "Response " + topRatedMovieResponse.toString()
                + "\n" + "Body " + res_body);

        if (res_body!=null) {
            this.topRatedMovieListJson = _gson.fromJson(res_body,JsonObject.class).getAsJsonArray("results");

        }
        return topRatedMovieListJson;

    }



    public List<Bitmap> getPopularPosters() throws Exception {
        JsonArray internal_json = this.getPopularMovieList();
        Log.i ("getPopularPosters", "Processiong JSON: " + internal_json);
        List<Bitmap> posters = new ArrayList<>();
        for (int i=0; i<internal_json.size(); i++){
            URL photo_url = new URL("http://image.tmdb.org/t/p/w185/" + internal_json.get(i).getAsJsonObject().get("poster_path").getAsString());
            Bitmap bitmap = BitmapFactory.decodeStream(  (InputStream) photo_url.getContent() );
            posters.add(bitmap);
        }

        return posters;
    }

    public JsonObject getMovieDetailBySequence(int seq) throws Exception{
        JsonArray internal_json = this.getPopularMovieList();
        return internal_json.get(seq).getAsJsonObject();
    }

    public List<Bitmap> loadPictures(JsonArray data) throws Exception {
        List<Bitmap> posters = new ArrayList<>();
        for (int i=0; i<data.size(); i++){
            URL photo_url = new URL("http://image.tmdb.org/t/p/w185/" + data.get(i).getAsJsonObject().get("poster_path").getAsString());
            Bitmap bitmap = BitmapFactory.decodeStream(  (InputStream) photo_url.getContent() );
            posters.add(bitmap);
        }

        return posters;
    }

    public JsonObject getMovieDetailById(String mId) throws Exception {
        HttpUrl getMovieByIdUrl = new HttpUrl.Builder()
                .scheme("https")
                .host(baseUrl)
                .addPathSegment("3")
                .addPathSegment("movie")
                .addPathSegment(mId)
                .addQueryParameter("api_key", apiKey)
                .build();
        Request getMovieRequest = new Request.Builder().url(getMovieByIdUrl).build();
        Response getMoveResponse = client.newCall(getMovieRequest).execute();
        if (getMoveResponse.body()!=null) {
            return _gson.fromJson(getMoveResponse.body().string(),JsonObject.class);
        }
        return null;
    }

}
