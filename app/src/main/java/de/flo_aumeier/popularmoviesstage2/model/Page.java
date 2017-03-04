
package de.flo_aumeier.popularmoviesstage2.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Page implements Parcelable
{

    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("results")
    @Expose
    private List<Movie> results = null;
    @SerializedName("total_results")
    @Expose
    private Integer totalMovies;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    public final static Parcelable.Creator<Page> CREATOR = new Creator<Page>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Page createFromParcel(Parcel in) {
            Page instance = new Page();
            instance.page = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.results, (Movie.class.getClassLoader()));
            instance.totalMovies = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.totalPages = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public Page[] newArray(int size) {
            return (new Page[size]);
        }

    }
            ;

    /**
     * No args constructor for use in serialization
     *
     */
    public Page() {
    }

    /**
     *
     * @param results
     * @param totalMovies
     * @param page
     * @param totalPages
     */
    public Page(Integer page, List<Movie> results, Integer totalMovies, Integer totalPages) {
        super();
        this.page = page;
        this.results = results;
        this.totalMovies = totalMovies;
        this.totalPages = totalPages;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Page withPage(Integer page) {
        this.page = page;
        return this;
    }

    public List<Movie> getMovies() {
        return results;
    }

    public void setMovies(List<Movie> results) {
        this.results = results;
    }

    public Page withMovies(List<Movie> results) {
        this.results = results;
        return this;
    }

    public Integer getTotalMovies() {
        return totalMovies;
    }

    public void setTotalMovies(Integer totalMovies) {
        this.totalMovies = totalMovies;
    }

    public Page withTotalMovies(Integer totalMovies) {
        this.totalMovies = totalMovies;
        return this;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Page withTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(page);
        dest.writeList(results);
        dest.writeValue(totalMovies);
        dest.writeValue(totalPages);
    }

    public int describeContents() {
        return 0;
    }

}