package co.test.moviebd;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.Map;

import co.test.moviebd.model.MovieDetail;
import co.test.moviebd.model.ResultSearch;
import co.test.moviebd.util.Constantes;

public class DetalleMovie extends AppCompatActivity implements View.OnClickListener, Response.Listener, Response.ErrorListener{

    private TextView tvTitulo;
    private TextView tvAdultos;
    private TextView tvLanzamiento;
    private TextView tvGeneros;
    private TextView tvPopularidad;
    private TextView tvVotos;
    private ImageView ivPoster;

    private int idMovie;
    private ProgressDialog pdMsj;
    private Map<String, String> mHeaders;
    private Gson gson;
    private ImageLoaderConfiguration configLoader;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_movie);
        cargarObjetos();
        //http://image.tmdb.org/t/p/w185/rlhQZRfKfe35vH1O97rpJIflhSY.jpg
    }

    private void cargarObjetos(){
        tvTitulo = (TextView) findViewById(R.id.tvTitulo);
        tvAdultos = (TextView) findViewById(R.id.tvAdultos);
        tvLanzamiento = (TextView) findViewById(R.id.tvLanzamiento);
        tvGeneros = (TextView) findViewById(R.id.tvGeneros);
        tvPopularidad = (TextView) findViewById(R.id.tvPopularidad);
        tvVotos  = (TextView) findViewById(R.id.tvVotos);
        ivPoster = (ImageView) findViewById(R.id.ivPoster);

        pdMsj = new ProgressDialog(this);
        gson = new Gson();

        idMovie = getIntent().getIntExtra("idMovie", 0);
        if(idMovie == 0){
            Toast.makeText(this, "Ocurrio un error al consultar la pelicula", Toast.LENGTH_SHORT).show();
            finish();
        }

        configLoader= new ImageLoaderConfiguration.Builder(this)
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(configLoader);

        consultarMovie();
    }

    /**
     * Lanza la consulta al webservice de los datos de la pelicula
     */
    private void consultarMovie(){
        //Construye la URL para la consulta
        String query = "?api_key=12956a722c68d23f60961096eaebdd15&language=ISO8859-1";
        String urlCOnsultaOrden = Constantes.URL_BASE + Constantes.URL_CONSULTA_MOVIE_ID + idMovie + query;

        //Muestra el progress dialog
        pdMsj.setCancelable(false);
        pdMsj.setMessage("Consultando MovieBD");
        pdMsj.show();

        //Lanza la peticion al WS
        HttpsTrustManager.allowAllSSL();
        RequestQueue queue = Volley.newRequestQueue(this);

        //Header para request
        mHeaders = new ArrayMap<String, String>();
        mHeaders.put("content-type", "application/json");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlCOnsultaOrden, null, this, this){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return mHeaders;
            }
        };
        queue.add(request);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        pdMsj.hide();
        Toast.makeText(this, "No se pueden obtener los detalles de la consulta", Toast.LENGTH_SHORT).show();
        Log.e("movieBD", "Ocurrio un error al realizar la consulta\n" + error.toString());
        finish();
    }

    @Override
    public void onResponse(Object response) {
        pdMsj.hide();
        MovieDetail result =  gson.fromJson(response.toString(), MovieDetail.class);
        tvTitulo.setText(result.getTitle());
        if(result.isAdult()){
            tvAdultos.setVisibility(View.VISIBLE);
        }else{
            tvAdultos.setVisibility(View.GONE);
        }
        tvLanzamiento.setText(result.getReleaseDate());
        String generos = "";
        for(int cG = 0; cG < result.getGenres().size(); cG++){
            if(cG > 0)
                generos += ", ";
            generos +=  result.getGenres().get(cG).getName();
        }
        tvGeneros.setText(generos);
        tvPopularidad.setText(String.valueOf(result.getPopularity()));
        tvVotos.setText(String.valueOf(result.getVoteAverage()));
        String urlPoster = Constantes.URL_BASE_POSTER + result.getPosterPath();
        ImageLoader.getInstance().displayImage(urlPoster, ivPoster);

    }
}
