package de.flo_aumeier.popularmoviesstage2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.flo_aumeier.popularmoviesstage2.model.Movie;

/**
 * The detailed view of a particular movie.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.PosterViewHolder> {
    private static final String TAG = MovieAdapter.class.getSimpleName();

    private ListItemClickListener mOnClickListener;

    private List<Movie> mMovies;

    public MovieAdapter(ListItemClickListener onClickListener, List<Movie> movieList) {
        mOnClickListener = onClickListener;
        mMovies = movieList;
    }

    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForGridItem = R.layout.movie_grid_item_view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = layoutInflater.inflate(layoutIdForGridItem, parent,
                shouldAttachToParentImmediately);
        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(mMovies.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovies.size(); //Ist null wenn man von MovieActivity zurückgeht.
    }

    public List<Movie> getMovies() {
        return mMovies;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mPosterImageView;
        private TextView mMovieTitle;
        private Context mContext;

        public PosterViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mPosterImageView = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        void bind(Movie movie) {
            final String basePosterURL = "https://image.tmdb.org/t/p/w185/";
            final String posterPath = movie.getPosterPath();
            // movie was clicked.
            final String completeUrl = basePosterURL + posterPath;
            Log.d(TAG, "Loading Poster: " + completeUrl);
            Picasso.with(mContext)
                    .load(completeUrl)
                    .into(mPosterImageView);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
