package com.staf621.ki4a;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.MenuItem;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */

    private static final String TAG = "ki4a";
    protected static String BASE = "/data/data/com.staf621.ki4a/ki4a";

    protected static ListPreference profile_list;
    protected static Preference proxy_host;
    protected static Preference proxy_port;
    protected static Preference proxy_header;
    protected static Preference ssh_password;
    protected static Preference ssh_key;
    protected static Preference ssh_ask_pass;
    protected static Preference key_switch;
    protected static Preference iptables_switch;
    protected static Preference airplane_switch;
    protected static Preference route_switch;
    protected static Preference route_button;
    protected static Preference dns_server;
    protected static Preference verify_host_text;
    protected static Preference autoconnect;
    protected static Context myContext;
    protected static Activity activity;


    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();


            if(preference.getKey().equals("profile_list"))
            {
                ListPreference listPreference = (ListPreference) preference;
                if(!listPreference.getValue().equals(stringValue)) {
                    //System.out.println("New Profile [" + stringValue+"] Old Profile ["+listPreference.getValue()+"]");

                    /* CHANGE THE PROFILE */
                    /* First save the old one */
                    SharedPreferences settings = PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext());
                    SharedPreferences profile = preference.getContext().getSharedPreferences("Profile" + listPreference.getValue(), 0);
                    SharedPreferences.Editor editor = profile.edit();

                    editor.clear();
                    for (Map.Entry<String, ?> entry : settings.getAll().entrySet()) {
                        Object v = entry.getValue();
                        String key = entry.getKey();
                        if (v instanceof Boolean)
                            editor.putBoolean(key, ((Boolean) v).booleanValue());
                        else if (v instanceof Float)
                            editor.putFloat(key, ((Float) v).floatValue());
                        else if (v instanceof Integer)
                            editor.putInt(key, ((Integer) v).intValue());
                        else if (v instanceof Long)
                            editor.putLong(key, ((Long) v).longValue());
                        else if (v instanceof String)
                            editor.putString(key, ((String) v));
                    }
                    editor.commit();

                    /* Now load the new profile into the current one */
                    SharedPreferences profile_new = preference.getContext().getSharedPreferences("Profile" + stringValue, 0);
                    SharedPreferences.Editor editor_current = settings.edit();

                    editor_current.clear();
                    for (Map.Entry<String, ?> entry : profile_new.getAll().entrySet()) {
                        Object v = entry.getValue();
                        String key = entry.getKey();
                        if (v instanceof Boolean)
                            editor_current.putBoolean(key, ((Boolean) v).booleanValue());
                        else if (v instanceof Float)
                            editor_current.putFloat(key, ((Float) v).floatValue());
                        else if (v instanceof Integer)
                            editor_current.putInt(key, ((Integer) v).intValue());
                        else if (v instanceof Long)
                            editor_current.putLong(key, ((Long) v).longValue());
                        else if (v instanceof String)
                            editor_current.putString(key, ((String) v));
                    }
                    editor_current.commit();

                    /* Reload the activity */
                    activity.finish();
                    activity.overridePendingTransition(0, 0);
                    activity.startActivity(activity.getIntent());
                    activity.overridePendingTransition(0, 0);
                }
            }

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            }
            else if(preference.getKey().equals("proxy_header"))
            {
                try {
                    String newFileName = BASE + "/header_file";
                    OutputStream out = new FileOutputStream(newFileName);
                    byte[] buffer = value.toString().getBytes();

                    out.write(buffer, 0, value.toString().length());
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    MyLog.e(TAG, "Exception creating header file", e);
                }
            }
            else if(preference.getKey().equals("key_text"))
            {
                try {
                    String newFileName = BASE + "/id_rsa";
                    OutputStream out = new FileOutputStream(newFileName);
                    byte[] buffer = value.toString().getBytes();

                    out.write(buffer, 0, value.toString().length());
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    MyLog.e(TAG, "Exception creating ssh key file", e);
                }
            }
            else
            {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };


    private static Preference.OnPreferenceChangeListener sBindPreferenceEnablerListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            if (preference.getKey().equals("proxy_switch")) {
                if((boolean) value) {
                    proxy_host.setEnabled(true);
                    proxy_port.setEnabled(true);
                    proxy_header.setEnabled(true);
                }
                else
                {
                    proxy_host.setEnabled(false);
                    proxy_port.setEnabled(false);
                    proxy_header.setEnabled(false);
                }
            }
            else if(preference.getKey().equals("key_switch"))
            {
                //Let's reset saved passwords
                ki4aService.got_ssh_pass = false;
                ki4aService.current_ssh_pass = "";
                if((boolean) value) {
                    ssh_password.setEnabled(false);
                    ssh_ask_pass.setEnabled(false);
                    ssh_key.setEnabled(true);
                }
                else
                {
                    ssh_password.setEnabled(true);
                    ssh_ask_pass.setEnabled(true);
                    ssh_key.setEnabled(false);
                }
            }
            else if(preference.getKey().equals("ask_pass_switch"))
            {
                //Let's reset saved passwords
                ki4aService.got_ssh_pass = false;
                ki4aService.current_ssh_pass = "";
                SwitchPreference key_switch_pref = (SwitchPreference) key_switch;
                if((boolean) value) {
                    ssh_password.setEnabled(false);
                    key_switch.setEnabled(false);
                }
                else
                {
                    if(!key_switch_pref.isChecked()) ssh_password.setEnabled(true);
                    key_switch.setEnabled(true);
                }
            }
            else if(preference.getKey().equals("cellular_switch"))
            {
                if((boolean) value) {
                    airplane_switch.setEnabled(true);
                    autoconnect.setEnabled(false);
                }
                else
                {
                    airplane_switch.setEnabled(false);
                    autoconnect.setEnabled(true);
                }
            }
            else if(preference.getKey().equals("dns_switch"))
            {
                if((boolean) value) {
                    dns_server.setEnabled(true);
                }
                else
                {
                    dns_server.setEnabled(false);
                }
            }
            else if(preference.getKey().equals("iptables_switch"))
            {
                if((boolean) value) {
                    route_switch.setEnabled(false);
                    route_button.setEnabled(false);
                }
                else
                {
                    SwitchPreference route_switch_pref = (SwitchPreference) route_switch;
                    route_switch.setEnabled(true);
                    route_button.setEnabled(!route_switch_pref.isChecked());
                }
            }
            else if(preference.getKey().equals("route_switch"))
            {
                if((boolean) value) {
                    route_button.setEnabled(false);
                }
                else
                {
                    route_button.setEnabled(true);
                }
            }
            else if(preference.getKey().equals("verify_internet_switch"))
            {
                if((boolean) value) {
                    verify_host_text.setEnabled(true);
                }
                else
                {
                    verify_host_text.setEnabled(false);
                }
            }
            return true;
        }
    };


    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private static void bindPreferenceEnabler(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceEnablerListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceEnablerListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), false));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment ())
                .commit();
        myContext = this;
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //Let's go back to main Activity
            dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
            dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {

    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            activity = getActivity();

            profile_list = (ListPreference) findPreference("profile_list");
            proxy_host = findPreference("proxy_host");
            proxy_port = findPreference("proxy_port");
            proxy_header = findPreference("proxy_header");
            ssh_key = findPreference("key_text");
            ssh_password = findPreference("password_text");
            ssh_ask_pass = findPreference("ask_pass_switch");
            iptables_switch = findPreference("iptables_switch");
            airplane_switch = findPreference("airplane_switch");
            key_switch = findPreference("key_switch");
            dns_server = findPreference("dns_server");
            route_switch = findPreference("route_switch");
            route_button = findPreference("route_button");
            verify_host_text = findPreference("verify_host_text");
            autoconnect = findPreference("autoconnect_switch");

            bindPreferenceSummaryToValue(profile_list);
            bindPreferenceSummaryToValue(findPreference("server_text"));
            bindPreferenceSummaryToValue(findPreference("server_port"));
            bindPreferenceSummaryToValue(findPreference("user_text"));
            bindPreferenceSummaryToValue(proxy_host);
            bindPreferenceSummaryToValue(proxy_port);
            bindPreferenceSummaryToValue(proxy_header);
            bindPreferenceSummaryToValue(ssh_key);
            bindPreferenceSummaryToValue(dns_server);
            bindPreferenceSummaryToValue(verify_host_text);

            bindPreferenceEnabler(key_switch);
            bindPreferenceEnabler(ssh_ask_pass);
            bindPreferenceEnabler(route_switch);
            bindPreferenceEnabler(iptables_switch);
            bindPreferenceEnabler(findPreference("proxy_switch"));
            bindPreferenceEnabler(findPreference("cellular_switch"));
            bindPreferenceEnabler(findPreference("dns_switch"));
            bindPreferenceEnabler(findPreference("verify_internet_switch"));
            bindPreferenceEnabler(autoconnect);

            // Enable/Disable iptables switch when connected/disconnected
            // Also enable/disable profiles
            if(ki4aService.current_status==Util.STATUS_DISCONNECT)
            {
                iptables_switch.setEnabled(true);
                profile_list.setEnabled(true);
            }
            else
            {
                iptables_switch.setEnabled(false);
                profile_list.setEnabled(false);
            }

            Preference button = findPreference("about_button");
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent().setClass(myContext, AboutActivity.class);
                    startActivity(intent);
                    return true;
                }
            });

            Preference button2 = findPreference("forward_button");
            button2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent().setClass(myContext, ForwardActivity.class);
                    startActivity(intent);
                    return true;
                }
            });

            Preference button3 = findPreference("log_button");
            button3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent().setClass(myContext, ShowLogActivity.class);
                    startActivity(intent);
                    return true;
                }
            });

            Preference button4 = findPreference("route_button");
            button4.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent().setClass(myContext, RouteActivity.class);
                    startActivity(intent);
                    return true;
                }
            });
        }
    }
}
