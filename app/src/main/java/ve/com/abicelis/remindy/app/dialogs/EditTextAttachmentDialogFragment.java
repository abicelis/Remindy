package ve.com.abicelis.remindy.app.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import ve.com.abicelis.remindy.R;

/**
 * Created by abice on 16/3/2017.
 */

public class EditTextAttachmentDialogFragment extends DialogFragment implements View.OnClickListener {

    //UI
    private EditTextAttachmentDialogDismissListener mListener;
    private EditText mText;
    private Button mCancel;
    private Button mOk;

    public EditTextAttachmentDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditTextAttachmentDialogFragment newInstance(String text) {
        EditTextAttachmentDialogFragment frag = new EditTextAttachmentDialogFragment();
        Bundle args = new Bundle();
        args.putString("text", text);
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


        final String text = getArguments().getString("text");

        View dialogView =  inflater.inflate(R.layout.dialog_edit_attachment_text, container);

        mText = (EditText) dialogView.findViewById(R.id.dialog_edit_text_attachment_text);
        mText.setText(text);

        mOk = (Button) dialogView.findViewById(R.id.dialog_edit_text_attachment_ok);
        mOk.setOnClickListener(this);
        mCancel = (Button) dialogView.findViewById(R.id.dialog_edit_text_attachment_cancel);
        mCancel.setOnClickListener(this);

        return dialogView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.dialog_edit_text_attachment_cancel:
                //Hide keyboard
                ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mLink.getWindowToken(), 0);

                dismiss();
                break;

            case R.id.dialog_edit_text_attachment_ok:
                mListener.onFinishEditTextAttachmentDialog(mText.getText().toString());

                //Hide keyboard
                ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mLink.getWindowToken(), 0);

                dismiss();
                break;
        }
    }

    public void setListener(EditTextAttachmentDialogDismissListener listener) {
        mListener = listener;
    }


    public interface EditTextAttachmentDialogDismissListener {
        void onFinishEditTextAttachmentDialog(String text);
    }
}
