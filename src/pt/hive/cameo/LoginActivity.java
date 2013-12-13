package pt.hive.cameo;

import pt.hive.cameo.R;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Typeface;
import android.text.method.PasswordTransformationMethod;
import android.view.Window;
import android.widget.EditText;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // removes the title bar from the window (improves readability)
        // and then sets the login layout on it (starts the layout)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.login);
        
        // retrieves the password edit text field and updates it to the
        // sans serif typeface and then updates the transformation method
        EditText password = (EditText) this.findViewById(R.id.password);
        password.setTypeface(Typeface.SANS_SERIF);
        password.setTransformationMethod(new PasswordTransformationMethod());
    }
}
