package ve.com.abicelis.remindy.app.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by abice on 9/5/2017.
 */

public interface ViewHolderClickListener {
    public void onItemClicked(int position, @Nullable Intent optionalIntent, @Nullable Bundle optionalBundle);
    public boolean onItemLongClicked(int position);
}
