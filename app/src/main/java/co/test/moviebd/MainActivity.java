package co.test.moviebd;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.design.widget.Snackbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import co.test.moviebd.adapter.FavoritosAdapter;
import co.test.moviebd.adapter.MoviesAdapter;
import co.test.moviebd.adapter.MoviesResponse;
import co.test.moviebd.model.*;
import co.test.moviebd.util.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener, Response.Listener, Response.ErrorListener, MoviesResponse {

    private LinearLayout llHeader;
    private EditText edBusqueda;
    private ImageView ivBuscar;
    private LinearLayout llFavoritosShow;
    private TextView tvLabelNoResult;
    private RecyclerView rvResultados;
    private LinearLayout llResult;

    private LinearLayout llFavoritos;
    private RecyclerView rvFavoritos;

    private ProgressDialog pdMsj;
    private Integer TIPO_REQUEST;
    private Map<String, String> mHeaders;
    private Integer pageResult;
    private Gson gson;
    private ResultSearch result;
    private LinearLayoutManager mLayoutManager;
    private LinearLayoutManager mLayoutManagerFav;
    private MoviesAdapter adMovies;
    private FavoritosAdapter adFavoritos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cargarObjetos();
    }

    /**
     * Cargar los objetos de la actividad e inicializa los recursos
     *
     */
    private void cargarObjetos(){
        verificarPermisos();
        llHeader = (LinearLayout) findViewById(R.id.llHeader);

        edBusqueda = (EditText) findViewById(R.id.edBusqueda);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edBusqueda, InputMethodManager.SHOW_FORCED);

        edBusqueda.setOnEditorActionListener(this);

        ivBuscar = (ImageView) findViewById(R.id.ivBuscar);
        ivBuscar.setOnClickListener(this);
        llFavoritosShow = (LinearLayout) findViewById(R.id.llFavoritosShow);
        llFavoritosShow.setOnClickListener(this);
        tvLabelNoResult = (TextView) findViewById(R.id.tvLabelNoResult);
        llResult = (LinearLayout) findViewById(R.id.llResult);
        rvResultados = (RecyclerView) findViewById(R.id.rvResultados);

        llFavoritos = (LinearLayout) findViewById(R.id.llFavoritos);
        rvFavoritos = (RecyclerView) findViewById(R.id.rvFavoritos);
        pdMsj = new ProgressDialog(this);
        gson = new Gson();


        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        rvResultados.setHasFixedSize(true);
        rvResultados.setLayoutManager(mLayoutManager);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        rvResultados.addItemDecoration(itemDecoration);

        mLayoutManagerFav = new LinearLayoutManager(this);
        rvFavoritos.setHasFixedSize(true);
        rvFavoritos.setLayoutManager(mLayoutManagerFav);
        rvFavoritos.addItemDecoration(itemDecoration);
    }

    private void verificarPermisos(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
        }
    }

    /**
     * Iniciar el intent para lanzar la actividad de favoritos
     */
    private void cargarFavoritos(){

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivBuscar:
                consultarMovieBD();
                break;

            case R.id.llFavoritosShow:
                mostrarFavoritos();
                break;
        }
    }

    /**
     * Lanza la busqueda al presionar el boton ok del teclado virtual
     *
     * @param textView
     * @param actionId
     * @param keyEvent
     * @return
     */
    @Override
    public boolean onEditorAction(TextView textView, int actionId , KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            consultarMovieBD();
            return true;
        }
        return false;
    }


    /**
     * Realiza la consulta al webservice
     */
    private void consultarMovieBD() {
        //Verifica el contenido del edittext
        if(!edBusqueda.getText().toString().equals("") && edBusqueda.getText().toString().length() > 1){
            String keyword = edBusqueda.getText().toString();
            pageResult = 1;

            //Construye la URL para la consulta
            String query = "?query=" + keyword + "&api_key=" + Constantes.API_KEY + "&page=" + pageResult;
            String urlCOnsultaOrden = Constantes.URL_BASE + Constantes.URL_CONSULTA_KEYWORD + query;

            //Muestra el progress dialog
            pdMsj.setCancelable(false);
            pdMsj.setMessage("Consultando MovieBD");
            pdMsj.show();

            //Lanza la peticion al WS
            HttpsTrustManager.allowAllSSL();
            RequestQueue queue = Volley.newRequestQueue(this);

            TIPO_REQUEST = Constantes.CONSULTA_KEYWORD;
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

        }else{
            Snackbar.make(getCurrentFocus(), "Debe ingresar un concepto de busqueda", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        pdMsj.hide();
        Snackbar.make(getCurrentFocus(), "Ocurrio un error al realizar la consulta", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        Log.e("movieBD", "Ocurrio un error al realizar la consulta\n" + error.toString());

        llFavoritos.setVisibility(View.GONE);
        llResult.setVisibility(View.GONE);

    }

    /**
     * Resultado de la consulta al web service
     * @param response
     */
    @Override
    public void onResponse(Object response) {
        pdMsj.hide();
        result =  gson.fromJson(response.toString(), ResultSearch.class);
        if(result.getTotalResults() == 0){
            tvLabelNoResult.setVisibility(View.VISIBLE);
            llFavoritos.setVisibility(View.GONE);
            llResult.setVisibility(View.GONE);
        }else{
            tvLabelNoResult.setVisibility(View.GONE);
            llFavoritos.setVisibility(View.GONE);

            //Actualiza el listado
            adMovies = new MoviesAdapter(result.getResults(), this);
            rvResultados.setAdapter(adMovies);
            llResult.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Muestra el intent detallado de la pelicula, se pasa como parametro el id de la pelicula
     * @param movie
     */
    @Override
    public void SelectedMovie(Result movie) {
        Intent detalle = new Intent(this, DetalleMovie.class);
        detalle.putExtra("idMovie", movie.getId());
        startActivity(detalle);
    }

    /**
     * Agregar a la tabla de favoritos la pelicula
     * @param movie
     */
    @Override
    public void MarcarFavorito(Result movie) {
        Favoritos newFav = new Favoritos(movie.getId(), movie.getName());
        newFav.save();
    }

    /**
     * Eliminar de la tabla de favoritos la pelicula
     * @param movie
     */
    @Override
    public void DesmarcarFavorito(Result movie) {
        List<Favoritos> delList = Favoritos.find(Favoritos.class, "ID_MOVIE = ?", String.valueOf(movie.getId()));
        if(delList.size() > 0){
            Favoritos.delete(delList.get(0));
        }

        //Si se esta mostrando actualiza la lista de favoritos
        if(llFavoritos.getVisibility() == View.VISIBLE){
            mostrarFavoritos();
        }
    }

    /**
     * Consulta la tabla de favoritos y la muestra en la lista
     */
    private void mostrarFavoritos(){
        List<Favoritos> favoritos = Favoritos.find(Favoritos.class, null, null);
        adFavoritos = new FavoritosAdapter(favoritos, this);
        rvFavoritos.setAdapter(adFavoritos);

        tvLabelNoResult.setVisibility(View.GONE);
        llResult.setVisibility(View.GONE);
        llFavoritos.setVisibility(View.VISIBLE);
    }
}
