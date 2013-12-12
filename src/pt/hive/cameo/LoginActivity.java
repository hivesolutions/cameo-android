package pt.hive.cameo;

import pt.hive.caneo.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Window;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // removes the title bar from the window (improves readability)
        // and then sets the login layout on it (starts the layout)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.login);
    }
}
