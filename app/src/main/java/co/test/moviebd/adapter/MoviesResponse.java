package co.test.moviebd.adapter;

import co.test.moviebd.model.Result;

/**
 * Created by JPerezP on 10/03/2016.
 */
public interface MoviesResponse {
    public void SelectedMovie(Result movie);

    public void MarcarFavorito(Result movie);

    public void DesmarcarFavorito(Result movie);
}
