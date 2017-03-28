# Requirements
# API KEY
Past your API KEY in: de.flo_aumeier.popularmoviesstage2.model.TmdbApiEndpointInterface.API_KEY
## User Interface - Layout
1. UI contains an element (e.g., a spinner or settings menu) to toggle the sort order of the 
movies by: most popular, highest rated.
2. (DONE)Movies are displayed in the main layout via a grid of their corresponding movie poster 
thumbnails.
3. (DONE)UI contains a screen for displaying the details for a selected movie.
4. (DONE)Movie Details layout contains title, release date, movie poster, vote average, and plot 
synopsis.
5. Movie Details layout contains a section for displaying trailer videos and user reviews.
## User Interface - Function
1. When a user changes the sort criteria (most popular, highest rated, and favorites) the main view gets updated correctly.
2. (DONE)When a movie poster thumbnail is selected, the movie details screen is launched.
3. When a trailer is selected, app uses an Intent to launch the trailer.
4. In the movies detail screen, a user can tap a button(for example, a star) to mark it as a Favorite.
## Network API Implementation
1. (DONE)In a background thread, app queries the /movie/popular or /movie/top_rated API for the sort criteria specified in the settings menu.
2. App requests for related videos for a selected movie via the /movie/{id}/videos endpoint in a background thread and displays those details when the user selects a movie.
3. App requests for user reviews for a selected movie via the /movie/{id}/reviews endpoint in a background thread and displays those details when the user selects a movie.
## Suggestions to Make Your Project Stand Out!
1. Extend the favorites ContentProvider to store the movie poster, synopsis, user rating, and release date, and display them even when offline.
2. Implement sharing functionality to allow the user to share the first trailer’s YouTube URL from the movie details screen.
