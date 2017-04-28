package ve.com.abicelis.remindy.app.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
        mLink.setSelection(mLink.getText().length());

        mOk = (Button) dialogView.findViewById(R.id.dialog_edit_link_attachment_ok);
        mOk.setOnClickListener(this);
        mCancel = (Button) dialogView.findViewById(R.id.dialog_edit_link_attachment_cancel);
        mCancel.setOnClickListener(this);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialogView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.dialog_edit_link_attachment_cancel:
                //Hide keyboard
                ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mLink.getWindowToken(), 0);

                dismiss();
                break;

            case R.id.dialog_edit_link_attachment_ok:
                String link = mLink.getText().toString();
                if(!link.isEmpty() && Patterns.WEB_URL.matcher(link).matches()) {
                    mListener.onFinishEditLinkAttachmentDialog(mLink.getText().toString());

                    //Hide keyboard
                    ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mLink.getWindowToken(), 0);

                    dismiss();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.dialog_edit_link_error_empty_text), Toast.LENGTH_SHORT).show();
                }
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
