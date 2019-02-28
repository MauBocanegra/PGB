package propulsar.pgb.presentationlayer.CustomViews;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;

/**
 * Created by maubocanegra on 14/03/17.
 */

public class EditTextCampo extends AppCompatEditText {
    public EditTextCampo(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFilters(new InputFilter[]{filter});
    }

    // ------------------------------------------ //
    // -------------- OWN METHODS --------------- //
    // ------------------------------------------ //

    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            //String blockCharacterSet = "~#^|$%&*·<>='ç+*ºª¬∞¢`Ç¨?¿¡!\"";
            String blockCharacterSet="<>=";
            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };
}
