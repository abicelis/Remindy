package ve.com.abicelis.remindy.util;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import ve.com.abicelis.remindy.R;

/**
 * Created by abice on 18/3/2017.
 */

public class SnackbarUtil {

    public static void showErrorSnackbar(View container, @StringRes int textStringRes) {
        doShowSnackbar(container, textStringRes, R.color.snackbar_error_background);
    }

    public static void showNoticeSnackbar(View container, @StringRes int textStringRes) {
        doShowSnackbar(container, textStringRes, R.color.snackbar_notice_background);

    }

    public static void showSuccessSnackbar(View container, @StringRes int textStringRes) {
        doShowSnackbar(container, textStringRes, R.color.snackbar_success_background);
    }

    private static void doShowSnackbar(View container, @StringRes int stringRes, @ColorRes int backgroundColorRes) {
        Snackbar snackbar = Snackbar.make(container, stringRes, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(backgroundColorRes);
        TextView snackbarText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackbarText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_error_snackbar, 0);
        snackbarText.setGravity(Gravity.CENTER);
        snackbar.show();
    }
}
