package ve.com.abicelis.remindy.app.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ve.com.abicelis.remindy.R;

/**
 * Created by abice on 16/3/2017.
 */

public class EditLinkAttachmentDialogFragment extends DialogFragment implements View.OnClickListener {

    //UI
    private EditLinkAttachmentDialogDismissListener mListener;
    private EditText mLink;
    private Button mCancel;
    private Button mOk;

    public EditLinkAttachmentDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditLinkAttachmentDialogFragment newInstance(String text) {
        EditLinkAttachmentDialogFragment frag = new EditLinkAttachmentDialogFragment();
        Bundle args = new Bundle();
        args.putString("link", text);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final String link = getArguments().getString("link");

        View dialogView =  inflater.inflate(R.layout.dialog_edit_attachment_link, container);

        mLink = (EditText) dialogView.findViewById(R.id.dialog_edit_link_attachment_link);
        mLink.setText(link);

        mOk = (Button) dialogView.findViewById(R.id.dialog_edit_link_attachment_ok);
        mOk.setOnClickListener(this);
        mCancel = (Button) dialogView.findViewById(R.id.dialog_edit_link_attachment_cancel);
        mCancel.setOnClickListener(this);

        return dialogView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.dialog_edit_link_attachment_cancel:
                dismiss();
                break;

            case R.id.dialog_edit_link_attachment_ok:
                mListener.onFinishEditLinkAttachmentDialog(mLink.getText().toString());
                dismiss();
                break;
        }
    }

    public void setListener(EditLinkAttachmentDialogDismissListener listener) {
        mListener = listener;
    }


    public interface EditLinkAttachmentDialogDismissListener {
        void onFinishEditLinkAttachmentDialog(String text);
    }
}
