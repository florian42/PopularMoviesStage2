
package de.flo_aumeier.popularmoviesstage2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Data object holding the response for the Trailers.
 */
public class ResultsTrailer implements Parcelable {

    public final static Creator<ResultsTrailer> CREATOR = new Creator<ResultsTrailer>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ResultsTrailer createFromParcel(Parcel in) {
            ResultsTrailer instance = new ResultsTrailer();
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.results, (Trailer.class.getClassLoader()));
            return instance;
        }

        public ResultsTrailer[] newArray(int size) {
            return (new ResultsTrailer[size]);
        }

    };
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<Trailer> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ResultsTrailer withId(Integer id) {
        this.id = id;
        return this;
    }

    public List<Trailer> getResults() {
        return results;
    }

    public void setResults(List<Trailer> results) {
        this.results = results;
    }

    public ResultsTrailer withResults(List<Trailer> results) {
        this.results = results;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(results);
    }

    public int describeContents() {
        return 0;
    }

}
