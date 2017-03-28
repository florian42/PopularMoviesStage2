package de.flo_aumeier.popularmoviesstage2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter holding the Trailers.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private static final String TAG = TrailerAdapter.class.getSimpleName();

    private List<String> trailerThumbnailUrls;

    private ListItemClickListener mOnClickListener;

    public TrailerAdapter(List<String> trailerThumbnailUrls, ListItemClickListener mOnClickListener) {
        this.trailerThumbnailUrls = trailerThumbnailUrls;
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.thumbnail_list_item_view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = layoutInflater.inflate(layoutIdForListItem, parent,
                shouldAttachToParentImmediately);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(trailerThumbnailUrls.get(position));
    }

    @Override
    public int getItemCount() {
        return trailerThumbnailUrls.size();
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView thumbnail;
        private Context mContext;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            thumbnail = (ImageView) itemView.findViewById(R.id.iv_trailer_thumbnail);
            itemView.setOnClickListener(this);
        }

        void bind(String trailerYoutubeId) {
            //https://img.youtube.com/vi/<insert-youtube-video-id-here>/default.jpg
            final String youtubeThumbnailUrlStart = "https://img.youtube.com/vi/";
            final String youtubeThumbnailUrlEnd = "/maxresdefault.jpg";
            final String thumbnailURL = youtubeThumbnailUrlStart + trailerYoutubeId + youtubeThumbnailUrlEnd;
            Log.d(TAG, "Loading Thumbnail: " + thumbnailURL);
            Picasso.with(mContext)
                    .load(thumbnailURL)
                    .into(thumbnail);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
