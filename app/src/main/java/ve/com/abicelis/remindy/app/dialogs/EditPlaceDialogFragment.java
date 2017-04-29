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

public class EditPlaceDialogFragment extends DialogFragment implements View.OnClickListener {

    //UI
    private EditPlaceDialogDismissListener mListener;
    private EditText mAlias;
    private EditText mAddress;
    private Button mCancel;
    private Button mOk;

    public EditPlaceDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditPlaceDialogFragment newInstance(String alias, String address) {
        EditPlaceDialogFragment frag = new EditPlaceDialogFragment();
        Bundle args = new Bundle();
        args.putString("alias", alias);
        args.putString("address", address);
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


        final String alias = getArguments().getString("alias");
        final String address = getArguments().getString("address");

        View dialogView =  inflater.inflate(R.layout.dialog_edit_place, container);

        mAlias = (EditText) dialogView.findViewById(R.id.dialog_edit_place_alias);
        mAlias.setText(alias);
        mAddress = (EditText) dialogView.findViewById(R.id.dialog_edit_place_address);
        mAddress.setText(address);

        mOk = (Button) dialogView.findViewById(R.id.dialog_edit_place_ok);
        mOk.setOnClickListener(this);
        mCancel = (Button) dialogView.findViewById(R.id.dialog_edit_place_cancel);
        mCancel.setOnClickListener(this);

        return dialogView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.dialog_edit_place_cancel:
                dismiss();
                break;

            case R.id.dialog_edit_place_ok:
                mListener.onFinishEditPlaceDialog(mAlias.getText().toString().trim(), mAddress.getText().toString().trim());
                dismiss();
                break;
        }
    }



    public void setListener(EditPlaceDialogDismissListener listener) {
        mListener = listener;
    }


    public interface EditPlaceDialogDismissListener {
        void onFinishEditPlaceDialog(String alias, String address);
    }
}
