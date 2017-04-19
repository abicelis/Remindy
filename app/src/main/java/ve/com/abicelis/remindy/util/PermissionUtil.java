package ve.com.abicelis.remindy.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abice on 17/4/2017.
 */

public class PermissionUtil {
    /**
     * Returns a list of permissions which have not been granted or null if all
     * permissions have been granted
     * @param context The activity Context
     * @param permissions Array of permissions to check for grant
     * @return Array of non-granted permissions or null if all is good
     */
    public static String[] checkIfPermissionsAreGranted(Context context, String ... permissions) {

        List<String> nonGrantedPermissions = new ArrayList<>();
        for (String permission: permissions) {
            int rc = ActivityCompat.checkSelfPermission(context, permission);
            if(rc != PackageManager.PERMISSION_GRANTED) {
                nonGrantedPermissions.add(permission);
            }
        }

        if(nonGrantedPermissions.size() == 0)
            return null;
        else
            return nonGrantedPermissions.toArray(new String[nonGrantedPermissions.size()]);
    }
}
