package ve.com.abicelis.remindy.util;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import ve.com.abicelis.remindy.R;

/**
 * Created by abice on 18/3/2017.
 */

public class SnackbarUtil {


    public static void showSnackbar(View container, @NonNull SnackbarType snackbarType,
                                    @StringRes int textStringRes, @Nullable SnackbarDuration duration,
                                    @Nullable BaseTransientBottomBar.BaseCallback<Snackbar> callback) {

        duration = (duration == null ? SnackbarDuration.LONG : duration);

        Snackbar snackbar = Snackbar.make(container, textStringRes, duration.getDuration());
        snackbar.getView().setBackgroundResource(snackbarType.getColorRes());
        TextView snackbarText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackbarText.setCompoundDrawablesWithIntrinsicBounds(0, 0,snackbarType.getIconRes(), 0);
        snackbarText.setGravity(Gravity.CENTER);
        if(callback != null)
            snackbar.addCallback(callback);
        snackbar.show();
    }


    public enum SnackbarDuration {
        LONG(Snackbar.LENGTH_LONG),
        SHORT(Snackbar.LENGTH_SHORT),
        INDEFINITE(Snackbar.LENGTH_INDEFINITE);

        private int duration;

        SnackbarDuration(int duration){
            this.duration = duration;
        }

        public int getDuration() {
            return duration;
        }
    }

    public enum SnackbarType {
        ERROR(R.color.snackbar_error_background, R.drawable.icon_error_snackbar),
        SUCCESS(R.color.snackbar_success_background, R.drawable.icon_success_snackbar),
        NOTICE(R.color.snackbar_notice_background, R.drawable.icon_error_snackbar);

        private @ColorRes int colorRes;
        private @DrawableRes int iconRes;

        SnackbarType(@ColorRes int colorRes, @DrawableRes int iconRes){
            this.colorRes = colorRes;
            this.iconRes = iconRes;
        }

        public @ColorRes int getColorRes() {
            return colorRes;
        }

        public @DrawableRes int getIconRes() {
            return iconRes;
        }
    }

}


