package ve.com.abicelis.remindy.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import ve.com.abicelis.remindy.R;

/**
 * Created by abice on 16/4/2017.
 */

public class ClipboardUtil {

    public static void copyToClipboard(Activity activity, String textToCopy) {
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Remindy text", textToCopy);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(activity, R.string.dialog_text_attachment_text_copied_clipboard, Toast.LENGTH_SHORT).show();

    }
}
