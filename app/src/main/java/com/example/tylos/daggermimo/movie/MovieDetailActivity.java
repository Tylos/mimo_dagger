package com.example.tylos.daggermimo.movie;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tylos.daggermimo.BuildConfig;
import com.example.tylos.daggermimo.R;
import com.example.tylos.daggermimo.base.utils.ImageBuilder;
import com.example.tylos.daggermimo.base.view.BaseActivity;
import com.example.tylos.daggermimo.login.api.PublicApi;
import com.example.tylos.daggermimo.login.api.services.AuthenticationService;
import com.example.tylos.daggermimo.movie.api.AuthenticatedApi;
import com.example.tylos.daggermimo.movie.api.model.AccountMovie;
import com.example.tylos.daggermimo.movie.api.services.AccountMovieService;
import com.squareup.picasso.Picasso;
import com.uwetrottmann.tmdb.TmdbHelper;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.services.MoviesService;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class MovieDetailActivity extends BaseActivity {

    private static final String EXTRAS_KEY_MOVIE_ID = "extras_key.movie_id";

    private ImageView background;
    private TextView overview;
    private TextView genre;
    private TextView date;
    private TextView rating;
    private TextView votes;
    private TextView title;
    private View btWatchlist;
    private View btFavorite;

    private final RequestInterceptor publicRequestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addQueryParam("api_key", BuildConfig.TMDB_API_KEY);
        }
    };

    private final AuthenticationService authenticationService = new RestAdapter.Builder()
            .setEndpoint(BuildConfig.TMDB_ROOT)
            .setRequestInterceptor(publicRequestInterceptor)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build()
            .create(AuthenticationService.class);

    private final MoviesService moviesService = new RestAdapter.Builder()
            .setEndpoint(BuildConfig.TMDB_ROOT)
            .setRequestInterceptor(publicRequestInterceptor)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setConverter(new GsonConverter(TmdbHelper.getGsonBuilder().create()))
            .build()
            .create(MoviesService.class);

    private int movieId;

    public static final Bundle generateBundle(int movieId) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRAS_KEY_MOVIE_ID, movieId);
        return bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieId = getIntent().getExtras().getInt(EXTRAS_KEY_MOVIE_ID, 0);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_movie_detail;
    }

    @Override
    protected void mapUI() {
        title = findView(R.id.title);
        background = findView(R.id.background);
        overview = findView(R.id.overview);
        genre = findView(R.id.genre);
        date = findView(R.id.date);
        rating = findView(R.id.rating);
        votes = findView(R.id.votes);
        btFavorite = findViewById(R.id.favorite);
        btWatchlist = findView(R.id.watchlist);
    }

    @Override
    protected void hookUI() {
        btFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteMovie(!v.isSelected());
            }
        });
        btWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMovieToWatchlist(!v.isSelected());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestMovie();
        requestUserMovieData();
    }

    private void requestMovie() {
        new AsyncTask<Void, Void, Movie>() {
            @Override
            protected Movie doInBackground(Void... params) {
                PublicApi api = new PublicApi(authenticationService, moviesService);
                return api.getMovieDetail(movieId);
            }

            @Override
            protected void onPostExecute(Movie movie) {
                title.setText(movie.title);
                overview.setText(movie.overview);
                genre.setText(movie.genres.isEmpty() ? getString(R.string.movie_detail_genre_default) : movie.genres.get(0).name);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                date.setText(dateFormat.format(movie.release_date));
                Picasso picasso = Picasso.with(MovieDetailActivity.this);
                picasso.setLoggingEnabled(true);
                picasso
                        .load(ImageBuilder.generateImageUrl(movie.backdrop_path))
                        .placeholder(R.drawable.gradient_placeholder)
                        .fit()
                        .centerCrop()
                        .into(background);

                DecimalFormat decimalFormat = new DecimalFormat();
                decimalFormat.setMinimumFractionDigits(2);
                decimalFormat.setMaximumFractionDigits(2);
                decimalFormat.setMinimumIntegerDigits(1);
                decimalFormat.setMaximumIntegerDigits(2);
                decimalFormat.setDecimalSeparatorAlwaysShown(true);
                DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
                symbols.setDecimalSeparator('.');
                decimalFormat.setDecimalFormatSymbols(symbols);
                rating.setText(decimalFormat.format(movie.vote_average));
                votes.setText(movie.vote_count.toString());
            }
        }.execute();
    }

    private void requestUserMovieData() {
        new AsyncTask<Void, Void, AccountMovie>() {
            @Override
            protected AccountMovie doInBackground(Void... params) {
                SharedPreferences preferences = MovieDetailActivity.this.getApplication().getSharedPreferences("mimodagger", MODE_PRIVATE);
                final String session_token = preferences.getString("preferences.session_token", "");

                AccountMovieService service = new RestAdapter.Builder()
                        .setEndpoint(BuildConfig.TMDB_ROOT)
                        .setRequestInterceptor(new RequestInterceptor() {
                            @Override
                            public void intercept(RequestFacade request) {
                                request.addQueryParam("api_key", BuildConfig.TMDB_API_KEY);
                                request.addQueryParam("session_id", session_token);
                            }
                        })
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setConverter(new GsonConverter(TmdbHelper.getGsonBuilder().create()))
                        .build()
                        .create(AccountMovieService.class);

                return service.getAccountMovieData(movieId);
            }

            @Override
            protected void onPostExecute(AccountMovie accountMovie) {
                btWatchlist.setActivated(accountMovie.isWatchlist());
                btFavorite.setActivated(accountMovie.isFavorite());
            }
        }.execute();
    }

    private void addMovieToWatchlist(final boolean shouldAddToWatchlist) {
        new AsyncTask<Void, Void, Response>() {
            @Override
            protected Response doInBackground(Void... params) {
                SharedPreferences preferences = MovieDetailActivity.this.getApplication().getSharedPreferences("mimodagger", MODE_PRIVATE);
                final String sessionToken = preferences.getString("preferences.session_token", "");
                final int accountId = (int) preferences.getLong("preferences.account_id", 0);

                AuthenticatedApi api = new AuthenticatedApi(sessionToken, accountId);
                return api.postWatchlistMovie(movieId, shouldAddToWatchlist);
            }

            @Override
            protected void onPostExecute(Response response) {
                if(response.getStatus() == 201) {
                    btWatchlist.setActivated(shouldAddToWatchlist);
                } else {
                    Toast.makeText(MovieDetailActivity.this, getString(R.string.movie_detail_watchlist_error), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void favoriteMovie(final boolean shouldFavorite) {
        new AsyncTask<Void, Void, Response>() {
            @Override
            protected Response doInBackground(Void... params) {
                SharedPreferences preferences = MovieDetailActivity.this.getApplication().getSharedPreferences("mimodagger", MODE_PRIVATE);
                final String sessionToken = preferences.getString("preferences.session_token", "");
                final int accountId = (int) preferences.getLong("preferences.account_id", 0);

                AuthenticatedApi api = new AuthenticatedApi(sessionToken, accountId);
                return api.postFavoriteMovie(movieId, shouldFavorite);
            }

            @Override
            protected void onPostExecute(Response response) {
                if(response.getStatus() == 201) {
                    btFavorite.setActivated(shouldFavorite);
                } else {
                    Toast.makeText(MovieDetailActivity.this, getString(R.string.movie_detail_favorite_error), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

}
