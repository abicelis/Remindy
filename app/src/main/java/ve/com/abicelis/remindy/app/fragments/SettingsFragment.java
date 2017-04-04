package ve.com.abicelis.remindy.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.activities.AboutActivity;
import ve.com.abicelis.remindy.app.activities.PlaceListActivity;


/**
 * Created by abice on 9/2/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    //UI

    private Preference mManagePlaces;
    private ListPreference mDateFormat;
    private ListPreference mTimeFormat;
    private SwitchPreference mShowLocationBasedReminderInNewTab;
    private Preference mBackup;
    private Preference mRestore;
    private Preference mAbout;
    private Preference mRate;
    private Preference mContact;


    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.remindy_settings);
        final Context packageContext = getActivity().getApplicationContext();


        mManagePlaces = findPreference(getResources().getString(R.string.settings_manage_places_key));
        mManagePlaces.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent goToPlaceListActivity = new Intent(packageContext, PlaceListActivity.class);
                startActivity(goToPlaceListActivity);
                return true;
            }
        });
        mDateFormat = (ListPreference) findPreference(getResources().getString(R.string.settings_date_format_key));
        mTimeFormat = (ListPreference) findPreference(getResources().getString(R.string.settings_time_format_key));
        mShowLocationBasedReminderInNewTab = (SwitchPreference) findPreference(getResources().getString(R.string.settings_show_location_based_reminder_in_new_tab_key));
        handleShowLocationBasedReminderInNewTabPreferenceChange(getShowLocationBasedReminderInNewTabValue());
        mShowLocationBasedReminderInNewTab.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                handleShowLocationBasedReminderInNewTabPreferenceChange((boolean) newValue);
                return true;
            }
        });
        mBackup = findPreference(getResources().getString(R.string.settings_backup_key));
        mRestore = findPreference(getResources().getString(R.string.settings_restore_key));
        mAbout = findPreference(getResources().getString(R.string.settings_about_key));
        mAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent goToAboutActivity = new Intent(packageContext, AboutActivity.class);
                startActivity(goToAboutActivity);
                return true;
            }
        });


        mRate = findPreference(getResources().getString(R.string.remindy_settings_rate_key));
        mRate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
                playStoreIntent.setData(Uri.parse(getResources().getString(R.string.url_market)));
                startActivity(playStoreIntent);
                return true;
            }
        });
        mContact = findPreference(getResources().getString(R.string.remindy_settings_contact_key));
        mContact.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",getResources().getString(R.string.address_email), null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                return true;
            }
        });
    }


    private boolean getShowLocationBasedReminderInNewTabValue() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        return preferences.getBoolean(getResources().getString(R.string.settings_show_location_based_reminder_in_new_tab_key), false);
    }

    private void handleShowLocationBasedReminderInNewTabPreferenceChange(boolean newValue) {
        if(newValue)
            mShowLocationBasedReminderInNewTab.setSummary(getResources().getText(R.string.settings_show_location_based_reminder_in_new_tab_summary_true));
        else
            mShowLocationBasedReminderInNewTab.setSummary(getResources().getText(R.string.settings_show_location_based_reminder_in_new_tab_summary_false));
    }



}
