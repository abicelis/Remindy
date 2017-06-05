package ve.com.abicelis.remindy.util;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.enums.TapTargetSequenceType;

/**
 * Created by abice on 13/5/2017.
 */

public class TapTargetSequenceUtil {

    private static final int DELAY = 100;

    public static void showTapTargetSequenceFor(@NonNull final Activity activity, @NonNull TapTargetSequenceType type) {
        List<TapTarget> targets = new ArrayList<>();

        if(!SharedPreferenceUtil.doShowTapTargetSequenceFor(activity, type))
            return;

        switch (type) {
            case EDIT_IMAGE_ATTACHMENT_ACTIVITY:
                targets.add(TapTarget.forView(activity.findViewById(R.id.activity_edit_image_attachment_crop), activity.getResources().getString(R.string.ttsu_activity_edit_image_attachment_crop_title), activity.getResources().getString(R.string.ttsu_activity_edit_image_attachment_crop_description)).transparentTarget(true).cancelable(false));
                targets.add(TapTarget.forView(activity.findViewById(R.id.activity_edit_image_attachment_rotate), activity.getResources().getString(R.string.ttsu_activity_edit_image_attachment_rotate_title), activity.getResources().getString(R.string.ttsu_activity_edit_image_attachment_rotate_description)).transparentTarget(true).cancelable(false));
                targets.add(TapTarget.forView(activity.findViewById(R.id.activity_edit_image_attachment_camera), activity.getResources().getString(R.string.ttsu_activity_edit_image_attachment_camera_title), activity.getResources().getString(R.string.ttsu_activity_edit_image_attachment_camera_description)).transparentTarget(true).cancelable(false));
                doShowTapTargetSequenceFor(activity, targets, null);
                break;

            case PLACE_LIST_ACTIVITY:
                targets.add(TapTarget.forView(activity.findViewById(R.id.activity_place_list_no_items_container), activity.getResources().getString(R.string.ttsu_activity_place_list_no_items_container_title), activity.getResources().getString(R.string.ttsu_activity_place_list_no_items_container_description)).transparentTarget(true).cancelable(false).targetRadius(100));
                targets.add(TapTarget.forView(activity.findViewById(R.id.activity_place_list_fab), activity.getResources().getString(R.string.ttsu_activity_place_list_fab_title), activity.getResources().getString(R.string.ttsu_activity_place_list_fab_description)).transparentTarget(true).cancelable(false));
                doShowTapTargetSequenceFor(activity, targets, null);
                break;

            case PLACE_ACTIVITY:
                targets.add(TapTarget.forView(activity.findViewById(R.id.place_autocomplete_search_button), activity.getResources().getString(R.string.ttsu_activity_place_autocomplete_search_button_title), activity.getResources().getString(R.string.ttsu_activity_place_autocomplete_search_button_description)).transparentTarget(true).cancelable(false));
                targets.add(TapTarget.forView(activity.findViewById(R.id.activity_place_map), activity.getResources().getString(R.string.ttsu_activity_place_map_container_title), activity.getResources().getString(R.string.ttsu_activity_place_map_container_description)).transparentTarget(true).cancelable(false).targetRadius(100));
                targets.add(TapTarget.forView(activity.findViewById(R.id.activity_place_radius_icon), activity.getResources().getString(R.string.ttsu_activity_place_radius_icon_title), activity.getResources().getString(R.string.ttsu_activity_place_radius_icon_description)).transparentTarget(true).cancelable(false));
                doShowTapTargetSequenceFor(activity, targets, null);
                break;

            default:
                Toast.makeText(activity, "TapTargetSequenceUtil(): Invalid TapTargetSequenceType", Toast.LENGTH_SHORT).show();
        }
    }

    private static void doShowTapTargetSequenceFor(@NonNull final Activity activity, @NonNull final List<TapTarget> targets, @Nullable final TapTargetSequence.Listener listener) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new TapTargetSequence(activity)
                        .targets(targets)
                        .listener(listener)
                        .start();
            }
        }, DELAY);
    }
}
