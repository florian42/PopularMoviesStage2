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

import java.util.LinkedList;

import de.flo_aumeier.popularmoviesstage2.model.Page;

/**
 * Created by Society on 22.01.2017.
 */
//TODO (1): Adapt to retrofit
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.PosterViewHolder> {
    private static final String TAG = MovieAdapter.class.getSimpleName();

    /**
     * Holding duplicate links to the interstellar poster
     * used for testing the recyclerview
     */

    final private ListItemClickListener mOnClickListener;

    private LinkedList<Page> mMovies;

    public MovieAdapter(ListItemClickListener onClickListener, LinkedList<Page> movieList) {
        mOnClickListener = onClickListener;
        mMovies = movieList;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
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
        return mMovies.size();
    }

    class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mPosterImageView;
        private TextView mMovieTitle;
        private Context mContext;
        //TODO (2): Adapt to new View
        public PosterViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mPosterImageView = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        void bind(Page movie) {
            String basePosterURL = "https://image.tmdb.org/t/p/w185/";
            String posterPath = movie.getResults(); //TODO (3): You need a way to specifiy which
            // movie was clicked.
            String completeUrl = basePosterURL + posterPath;
            Log.d(TAG, "Loading Poster: " + completeUrl);
            Picasso.with(mContext)
                    .load(completeUrl)
                    .into(mPosterImageView);
            Log.d(TAG, "Setting title: " + movie.getTitle());
            mMovieTitle.setText(movie.getTitle());
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    public LinkedList<Page> getMovies() {
        return mMovies;
    }
}
