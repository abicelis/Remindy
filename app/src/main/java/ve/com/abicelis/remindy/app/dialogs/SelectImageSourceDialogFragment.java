package ve.com.abicelis.remindy.app.dialogs;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.transitionseverywhere.TransitionManager;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.views.VisualizerView;
import ve.com.abicelis.remindy.enums.ImageSourceType;
import ve.com.abicelis.remindy.model.attachment.AudioAttachment;
import ve.com.abicelis.remindy.util.FileUtil;
import ve.com.abicelis.remindy.util.PermissionUtil;
import ve.com.abicelis.remindy.util.SnackbarUtil;

/**
 * Created by abice on 16/3/2017.
 */

public class SelectImageSourceDialogFragment extends DialogFragment implements View.OnClickListener {

    //DATA
    private SelectImageSourceSelectedListener mListener;
    private Drawable mCameraDrawable;
    private Drawable mGalleryDrawable;

    //UI
    private ImageButton mCamera;
    private ImageButton mGallery;
    private Button mCancel;


    public SelectImageSourceDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static SelectImageSourceDialogFragment newInstance() {
        SelectImageSourceDialogFragment frag = new SelectImageSourceDialogFragment();
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        PackageManager manager = getActivity().getPackageManager();

        //generate the intent to get the application list
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        List<ResolveInfo> cameraAppList = manager.queryIntentActivities(cameraIntent, 0);
        List<ResolveInfo> galleryAppList = manager.queryIntentActivities(galleryIntent, 0);

        //sort the list
        //Collections.sort(applicationList, new ResolveInfo.DisplayNameComparator(manager));

        if (cameraAppList.size() > 0)
            mCameraDrawable = cameraAppList.get(0).loadIcon(manager);

        if(galleryAppList.size() > 0)
            mGalleryDrawable = galleryAppList.get(0).loadIcon(manager);

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        int height = getResources().getDimensionPixelSize(R.dimen.dialog_select_image_source_height);
//        int width = getResources().getDimensionPixelSize(R.dimen.dialog_select_image_source_width);
//
//        getDialog().getWindow().setLayout(width, height);
//    }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView =  inflater.inflate(R.layout.dialog_image_source_select, container);

        mCamera = (ImageButton) dialogView.findViewById(R.id.dialog_image_source_select_camera);
        mGallery = (ImageButton) dialogView.findViewById(R.id.dialog_image_source_select_gallery);
        mCancel = (Button) dialogView.findViewById(R.id.dialog_image_source_select_cancel);

        if(mCameraDrawable != null)
            mCamera.setBackground(mCameraDrawable);

        if(mGalleryDrawable != null)
            mGallery.setBackground(mGalleryDrawable);

        mCamera.setOnClickListener(this);
        mGallery.setOnClickListener(this);
        mCancel.setOnClickListener(this);

        return dialogView;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if(mListener != null)
            mListener.onSourceSelected(ImageSourceType.NONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.dialog_image_source_select_camera:
                if(mListener != null)
                    mListener.onSourceSelected(ImageSourceType.CAMERA);
                dismiss();
                break;

            case R.id.dialog_image_source_select_gallery:
                if(mListener != null)
                    mListener.onSourceSelected(ImageSourceType.GALLERY);
                dismiss();
                break;

            case R.id.dialog_image_source_select_cancel:
                if(mListener != null)
                    mListener.onSourceSelected(ImageSourceType.NONE);
                dismiss();
                break;
        }
    }


    public void setListener(SelectImageSourceSelectedListener listener) {
        mListener = listener;
    }


    public interface SelectImageSourceSelectedListener {
        void onSourceSelected(ImageSourceType imageSourceType);
    }
}
