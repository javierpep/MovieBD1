package co.test.moviebd.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import co.test.moviebd.R;
import co.test.moviebd.model.Favoritos;
import co.test.moviebd.model.Result;

/**
 * Created by JPerezP on 10/03/2016.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.DataObjectHolder> {

    private List<Result> movies;
    private MoviesResponse callback;
    private Boolean esFavorito;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        CardView cvMovie;
        TextView tvNombre;
        ImageView ivFavorito;

        public DataObjectHolder(View itemView) {
            super(itemView);
            tvNombre = (TextView) itemView.findViewById(R.id.tvNombre);
            cvMovie = (CardView) itemView.findViewById(R.id.cvMovie);
            ivFavorito = (ImageView) itemView.findViewById(R.id.ivFavorito);
        }
    }

    public MoviesAdapter(List<Result> myDataset, MoviesResponse callback) {
        this.movies = myDataset;
        this.callback = callback;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        holder.tvNombre.setText(movies.get(position).getName());
        holder.tvNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.SelectedMovie(movies.get(position));
            }
        });

        //Verifica si forma parte de los favoritos
        esFavorito = false;
        List<Favoritos> favs = Favoritos.find(Favoritos.class, "ID_MOVIE = ?", String.valueOf(movies.get(position).getId()));
        if(favs.size() > 0){
            holder.ivFavorito.setImageResource(R.drawable.ic_favorite_fill);
            esFavorito = true;
        }
        holder.ivFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(esFavorito){
                    callback.DesmarcarFavorito(movies.get(position));
                    holder.ivFavorito.setImageResource(R.drawable.ic_favorite_outline);
                    esFavorito = false;
                }else{
                    callback.MarcarFavorito(movies.get(position));
                    holder.ivFavorito.setImageResource(R.drawable.ic_favorite_fill);
                    esFavorito = true;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}