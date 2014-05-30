/*
 Hive Cameo Framework
 Copyright (C) 2008-2014 Hive Solutions Lda.

 This file is part of Hive Cameo Framework.

 Hive Cameo Framework is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Hive Cameo Framework is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Hive Cameo Framework. If not, see <http://www.gnu.org/licenses/>.

 __author__    = João Magalhães <joamag@hive.pt>
 __version__   = 1.0.0
 __revision__  = $LastChangedRevision$
 __date__      = $LastChangedDate$
 __copyright__ = Copyright (c) 2008-2014 Hive Solutions Lda.
 __license__   = GNU General Public License (GPL), Version 3
 */

package pt.hive.cameo.activities;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import pt.hive.cameo.Info;
import pt.hive.cameo.ProxyRequest;
import pt.hive.cameo.ProxyRequestDelegate;
import pt.hive.cameo.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class LoginActivity extends Activity implements ProxyRequestDelegate {

    private String loginPath;

    public int logoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // saves a reference to the current instance under the self variable
        // so that it may be used by any clojure method
        final LoginActivity self = this;

        // tries to retrieve the extras set of parameters from the intent
        // and in case they exist, tries to retrieve the login path (parameter)
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            this.loginPath = extras.getString("LOGIN_PATH");
            this.logoId = extras.getInt("LOGO_ID");
        }

        // removes the title bar from the window (improves readability)
        // and then sets the login layout on it (starts the layout)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.login);

        // in case we've received a valid logo identifier the logo image must be
        // updated with the associated resrouce (customized view)
        if (this.logoId != 0) {
            Drawable logoResource = this.getResources()
                    .getDrawable(this.logoId);
            ImageView logo = (ImageView) this.findViewById(R.id.logo);
            logo.setImageDrawable(logoResource);
        }

        // retrieves the password edit text field and updates it to the
        // sans serif typeface and then updates the transformation method
        EditText password = (EditText) this.findViewById(R.id.password);
        password.setTypeface(Typeface.SANS_SERIF);
        password.setTransformationMethod(new PasswordTransformationMethod());
        password.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    self.login();
                }
                return false;
            }
        });

        // retrieves the reference to the various button in the current activity
        // and registers the current instance as the click listener
        Button signIn = (Button) findViewById(R.id.sign_in);
        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                self.login();
            }
        });
    }

    private void login() {
        EditText username = (EditText) this.findViewById(R.id.username);
        EditText password = (EditText) this.findViewById(R.id.password);

        List<List<String>> parameters = new LinkedList<List<String>>();
        parameters.add(new LinkedList<String>(Arrays.asList("username",
                username.getText().toString())));
        parameters.add(new LinkedList<String>(Arrays.asList("password",
                password.getText().toString())));

        ProxyRequest request = new ProxyRequest(this, this.loginPath);
        request.setDelegate(this);
        request.setParameters(parameters);
        request.setUseSession(false);
        request.execute();
    }

    @Override
    public void didReceiveJson(JSONObject data) {
        try {
            // verifies if there's an exception set in the received data, if
            // that's
            // the case the exception must be presented to the user
            boolean hasException = data.has("exception");
            if (hasException) {
                this.handleException(data);
                return;
            }

            // retrieves the value for the session id that has just been
            // retrieve by the remote call and sets it under the current
            // preferences settings so that it may be used latter
            String sessionId = data.getString("session_id");
            String username = data.getString("username");
            SharedPreferences preferences = this.getSharedPreferences("cameo",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("sessionId", sessionId);
            editor.putString("username", username);
            editor.commit();

            // finishes the current activity as it no longer needs to be
            // present for the login operation (already logged in)
            this.finish();
        } catch (JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void didReceiveError(Object error) {
        Log.d(Info.TAG,
                String.format("Error received in login request: %s", error));
    }

    public void handleException(JSONObject data) {
        try {
            JSONObject exception = data.getJSONObject("exception");
            String message = exception.getString("message");
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Error in submission");
            alertDialog.setMessage(message);
            alertDialog.show();
        } catch (JSONException exception) {
            throw new RuntimeException(exception);
        }
    }
}
