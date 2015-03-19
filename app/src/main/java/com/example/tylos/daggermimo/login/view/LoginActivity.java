package com.example.tylos.daggermimo.login.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.tylos.daggermimo.BuildConfig;
import com.example.tylos.daggermimo.R;
import com.example.tylos.daggermimo.base.view.BaseActivity;
import com.example.tylos.daggermimo.login.api.PublicApi;
import com.example.tylos.daggermimo.login.api.model.SessionData;
import com.example.tylos.daggermimo.login.api.services.AuthenticationService;
import com.example.tylos.daggermimo.upcomming.view.MoviesListActivity;
import com.uwetrottmann.tmdb.TmdbHelper;
import com.uwetrottmann.tmdb.services.MoviesService;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class LoginActivity extends BaseActivity {

    private EditText username;
    private EditText password;
    private Button loginButton;

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
        return R.layout.activity_login;
    }

    @Override
    protected void mapUI() {
        username = findView(R.id.username);
        password = findView(R.id.password);
        loginButton = findView(R.id.login);
    }

    @Override
    protected void hookUI() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick(v);
            }
        });
    }

    public void onLoginClick(View view) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                PublicApi api = new PublicApi(authenticationService, moviesService);

                SessionData session = api.login(username.getText().toString(), password.getText().toString());
                if (isSessionValid(session)) {
                    storeSession(session);
                    return Boolean.TRUE;
                }

                return Boolean.FALSE;
            }

            @Override
            protected void onPostExecute(Boolean isSuccess) {
                if (isSuccess) {
                    startActivity(new Intent(LoginActivity.this, MoviesListActivity.class));
                }

            }
        }.execute();
    }

    private void storeSession(SessionData session) {
        SharedPreferences preferences = this.getApplication().getSharedPreferences("mimodagger", MODE_PRIVATE);
        preferences.edit().putString("preferences.session_token", session.getToken().getRequestToken()).commit();
        preferences.edit().putLong("preferences.account_id", session.getAccount().getId()).commit();
    }

    private boolean isSessionValid(SessionData session) {
        return session.getToken() != null && session.getAccount() != null && session.getToken().isSuccess();
    }

}
