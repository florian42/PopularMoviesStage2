
package de.flo_aumeier.popularmoviesstage2.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Parcel;
import android.os.Parcelable;

public class Page implements Parcelable
{

    private Integer page;
    private List<Movie> results = null;
    private Integer totalResults;
    private Integer totalPages;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    public final static Creator<Page> CREATOR = new Creator<Page>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Page createFromParcel(Parcel in) {
            Page instance = new Page();
            instance.page = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.results, (Movie.class.getClassLoader()));
            instance.totalResults = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.totalPages = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.additionalProperties = ((Map<String, Object> ) in.readValue((Map.class.getClassLoader())));
            return instance;
        }

        public Page[] newArray(int size) {
            return (new Page[size]);
        }

    }
    ;

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

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    public Page withResults(List<Movie> results) {
        this.results = results;
        return this;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Page withTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
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

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Page withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(page);
        dest.writeList(results);
        dest.writeValue(totalResults);
        dest.writeValue(totalPages);
        dest.writeValue(additionalProperties);
    }

    public int describeContents() {
        return  0;
    }

}
