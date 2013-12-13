/*
 Hive Cameo Framework
 Copyright (C) 2008-2012 Hive Solutions Lda.

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
 __copyright__ = Copyright (c) 2008-2012 Hive Solutions Lda.
 __license__   = GNU General Public License (GPL), Version 3
 */

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
