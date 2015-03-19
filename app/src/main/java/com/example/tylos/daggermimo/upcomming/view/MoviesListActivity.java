package com.example.tylos.daggermimo.upcomming.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tylos.daggermimo.BuildConfig;
import com.example.tylos.daggermimo.R;
import com.example.tylos.daggermimo.base.utils.ImageBuilder;
import com.example.tylos.daggermimo.base.view.BaseActivity;
import com.example.tylos.daggermimo.common.transformations.GradientTransformation;
import com.example.tylos.daggermimo.login.api.PublicApi;
import com.example.tylos.daggermimo.login.api.services.AuthenticationService;
import com.example.tylos.daggermimo.movie.MovieDetailActivity;
import com.squareup.picasso.Picasso;
import com.uwetrottmann.tmdb.TmdbHelper;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;
import com.uwetrottmann.tmdb.services.MoviesService;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class MoviesListActivity extends BaseActivity {

    private ListView movieList;
    private View emptyView;
    private List<Movie> movies = new ArrayList<>();
    private MoviesAdapter adapter;

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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_movies_list;
    }

    @Override
    protected void mapUI() {
        movieList = findView(R.id.movie_list);
        emptyView = findView(R.id.spinner);

        adapter = new MoviesAdapter(movies);
        movieList.setEmptyView(emptyView);
        movieList.setAdapter(adapter);
    }


    private class MoviesAdapter extends BaseAdapter {

        private List<Movie> movies;

        private MoviesAdapter(List<Movie> movies) {
            this.movies = movies;
        }

        @Override
        public int getCount() {
            return movies.size();
        }

        @Override
        public Object getItem(int position) {
            return movies.get(position);
        }

        @Override
        public long getItemId(int position) {
            return movies.get(position).id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = initializeConvertView(parent);
            }
            renderConvertView((Movie) getItem(position), convertView);

            return convertView;
        }

        private View initializeConvertView(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_movie, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = (TextView) view.findViewById(R.id.title);
            viewHolder.cover = (ImageView) view.findViewById(R.id.cover);
            viewHolder.score = (TextView) view.findViewById(R.id.score);
            view.setTag(viewHolder);
            return view;
        }

        private void renderConvertView(Movie movie, View convertView) {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.title.setText(movie.title);
            Picasso picasso = Picasso.with(convertView.getContext());
            picasso.setLoggingEnabled(true);
            picasso
                    .load(ImageBuilder.generateImageUrl(movie.backdrop_path))
                    .placeholder(R.drawable.gradient_placeholder)
                    .fit()
                    .centerCrop()
                    .transform(
                            new GradientTransformation(
                                    convertView.getResources().getColor(android.R.color.transparent),
                                    convertView.getResources().getColor(R.color.black_90)))
                    .into(viewHolder.cover);

            DecimalFormat decimalFormat = new DecimalFormat();
            decimalFormat.setMinimumFractionDigits(2);
            decimalFormat.setMaximumFractionDigits(2);
            decimalFormat.setMinimumIntegerDigits(1);
            decimalFormat.setMaximumIntegerDigits(2);
            decimalFormat.setDecimalSeparatorAlwaysShown(true);
            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
            symbols.setDecimalSeparator('.');
            decimalFormat.setDecimalFormatSymbols(symbols);

            String average = decimalFormat.format(movie.vote_average);

            Spannable span = new SpannableString(average);
            span.setSpan(new RelativeSizeSpan(0.5f), average.indexOf('.'), average.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.score.setText(span);
        }

        private class ViewHolder {
            public TextView title;
            public ImageView cover;
            public TextView score;
        }
    }

    @Override
    protected void hookUI() {
        movieList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MoviesListActivity.this, MovieDetailActivity.class);
                intent.putExtras(MovieDetailActivity.generateBundle((int) parent.getItemIdAtPosition(position)));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestMovies();
    }

    private void requestMovies() {
        new AsyncTask<Void, Void, MovieResultsPage>() {
            @Override
            protected MovieResultsPage doInBackground(Void... params) {
                PublicApi api = new PublicApi(publicRequestInterceptor, authenticationService, moviesService);
                return api.getUpcomingMovies();
            }

            @Override
            protected void onPostExecute(MovieResultsPage upcommingQuery) {
                movies.addAll(upcommingQuery.results);
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }

}
