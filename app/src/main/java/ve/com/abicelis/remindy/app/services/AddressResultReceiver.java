package ve.com.abicelis.remindy.app.services;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by abice on 9/3/2017.
 */

public class AddressResultReceiver extends ResultReceiver {

    private AddressReceiverListener mReceiverListener;

    public AddressResultReceiver(Handler handler) {
        super(handler);
    }

    public interface AddressReceiverListener {
        void onReceiveAddressResult(int resultCode, Bundle resultData);
    }

    public void setReceiverListener(AddressReceiverListener receiver) {
        mReceiverListener = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        mReceiverListener.onReceiveAddressResult(resultCode, resultData);
    }
}
