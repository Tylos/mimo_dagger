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
import com.example.tylos.daggermimo.login.api.model.Account;
import com.example.tylos.daggermimo.login.api.model.RequestToken;
import com.example.tylos.daggermimo.login.api.model.SessionData;
import com.example.tylos.daggermimo.login.api.model.SessionToken;
import com.example.tylos.daggermimo.login.api.services.AuthenticationService;
import com.example.tylos.daggermimo.movie.api.services.AccountMovieService;
import com.example.tylos.daggermimo.upcomming.view.MoviesListActivity;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class LoginActivity extends BaseActivity {
    private EditText username;
    private EditText password;
    private Button loginButton;


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
        new AsyncTask<Void, Void, SessionData>() {
            @Override
            protected SessionData doInBackground(Void... params) {
                AuthenticationService service = new RestAdapter.Builder()
                        .setEndpoint(BuildConfig.TMDB_ROOT)
                        .setRequestInterceptor(new RequestInterceptor() {
                            @Override
                            public void intercept(RequestFacade request) {
                                request.addQueryParam("api_key", BuildConfig.TMDB_API_KEY);
                            }
                        })
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .build()
                        .create(AuthenticationService.class);


                RequestToken token;
                SessionToken session;
                try {
                    token = service.generateNewRequestToken();
                    token = service.validateTokenWithLogin(
                            token.getRequestToken(),
                            username.getText().toString(),
                            password.getText().toString()
                    );
                    session = service.generateSessionToken(token.getRequestToken());
                } catch (RetrofitError error) {
                    session = null;
                }

                Account account = null;
                final SessionToken finalSession = session;
                if (session != null) {
                   try {
                       AccountMovieService accountService = new RestAdapter.Builder()
                               .setEndpoint(BuildConfig.TMDB_ROOT)
                               .setRequestInterceptor(new RequestInterceptor() {
                                   @Override
                                   public void intercept(RequestFacade request) {
                                       request.addQueryParam("api_key", BuildConfig.TMDB_API_KEY);
                                       request.addQueryParam("session_id", finalSession.getRequestToken());
                                   }
                               })
                               .setLogLevel(RestAdapter.LogLevel.FULL)
                               .build()
                               .create(AccountMovieService.class);
                       account = accountService.getAccountDetails();
                   }catch (RetrofitError error) {
                       account = null;
                   }
                }

                return new SessionData(session, account);
            }


            @Override
            protected void onPostExecute(SessionData session) {
                if (session.getToken()!=null && session.getAccount() != null && session.getToken().isSuccess()) {
                    SharedPreferences preferences = LoginActivity.this.getApplication().getSharedPreferences("mimodagger", MODE_PRIVATE);
                    preferences.edit().putString("preferences.session_token", session.getToken().getRequestToken()).commit();
                    preferences.edit().putLong("preferences.account_id", session.getAccount().getId()).commit();
                    startActivity(new Intent(LoginActivity.this, MoviesListActivity.class));
                }

            }
        }.execute();
    }

}
