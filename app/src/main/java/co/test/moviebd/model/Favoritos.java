package co.test.moviebd.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class Favoritos extends SugarRecord{

    private Integer idMovie;
    private String name;

    public Favoritos() {
    }

    public Favoritos(Integer idMovie, String name) {
        this.idMovie = idMovie;
        this.name = name;
    }

    public Integer getIdMovie() {
        return idMovie;
    }

    public void setIdMovie(Integer idMovie) {
        this.idMovie = idMovie;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}