package ve.com.abicelis.remindy.util;

import android.content.res.Resources;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Alex on 9/3/2017.
 */

public class DpToPxUtil {
    /**
     * Converts dp to px
     */
    public static int dpToPx(int dp, Resources resources) {
        final float scale = resources.getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);
        return px;
    }
}