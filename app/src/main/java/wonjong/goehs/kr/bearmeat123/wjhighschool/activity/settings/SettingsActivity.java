package wonjong.goehs.kr.bearmeat123.wjhighschool.activity.settings;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import itmir.tistory.com.xor.SecurityXor;
import wonjong.goehs.kr.bearmeat123.wjhighschool.R;
import wonjong.goehs.kr.bearmeat123.wjhighschool.autoupdate.updateAlarm;
import wonjong.goehs.kr.bearmeat123.wjhighschool.tool.BapTool;
import wonjong.goehs.kr.bearmeat123.wjhighschool.tool.TimeTableTool;

import static android.icu.text.DateTimePatternGenerator.DAY;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class SettingsActivity extends AppCompatActivity {
static String adc = "newadmin";
    public static Activity activity = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);
        activity = this;
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(R.id.container, new PrefsFragment()).commit();
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_settings);

            boolean isAdmin = getPreferenceManager().getSharedPreferences().getBoolean("userAdmin_1", false);
            if (isAdmin) {
                Preference proUpgrade = findPreference("proUpgrade");
                proUpgrade.setSummary(R.string.user_info_licensed);
                proUpgrade.setEnabled(false);
            }

            findPreference("myDeviceId")
                    .setSummary(Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));

            setOnPreferenceClick(findPreference("infoAutoUpdate"));
            setOnPreferenceClick(findPreference("openSource"));
            setOnPreferenceClick(findPreference("ChangeLog"));
            setOnPreferenceClick(findPreference("DBreset"));
            setOnPreferenceClick(findPreference("proUpgrade"));
            setOnPreferenceChange(findPreference("autoBapUpdate"));
            setOnPreferenceChange(findPreference("updateLife"));

            try {
                PackageManager packageManager = getActivity().getPackageManager();
                PackageInfo info = packageManager.getPackageInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
                findPreference("appVersion").setSummary(info.versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void setOnPreferenceClick(Preference mPreference) {
            mPreference.setOnPreferenceClickListener(onPreferenceClickListener);
        }

        private Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                String getKey = preference.getKey();

                if ("openSource".equals(getKey)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                    builder.setTitle(R.string.license_title);
                    builder.setMessage(R.string.license_msg);
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                } else if ("DBreset".equals(getKey)) {
                    resetdb();
                }
                else if ("ChangeLog".equals(getKey)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                    builder.setTitle(R.string.changeLog_title);
                    builder.setMessage(R.string.changeLog_msg);
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                } else if ("infoAutoUpdate".equals(getKey)) {
                    showNotification();
                }

                else if ("proUpgrade".equals(getKey)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                    builder.setTitle(R.string.user_info_class_up_title);
//                    builder.setMessage(R.string.no_network_msg);

                    // Set an EditText view to get user input
                    final EditText input = new EditText(getActivity());
                    builder.setView(input);

                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = input.getText().toString();
                            if (value.equals(adc))
                            {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                                builder.setTitle("성공");
                                builder.setMessage("관리자 인증에 성공했습니다.");
                                builder.setNegativeButton("확인", null);
                                builder.show();

                                getPreferenceManager().getSharedPreferences().edit().putBoolean("userAdmin_1", true).apply();
                                Toast.makeText(activity, "관리자 권한을 사용하실수 있습니다.", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                                builder.setTitle("실패");
                                builder.setMessage("관리자 인증에 실패했습니다");
                                builder.setNegativeButton("확인", null);
                                builder.show();
                            }
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, null);
                    builder.show();
                }
                return true;
            }
        };

        private void setOnPreferenceChange(Preference mPreference) {
            mPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);

            if (mPreference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) mPreference;
                int index = listPreference.findIndexOfValue(listPreference.getValue());
                mPreference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            } else if (mPreference instanceof EditTextPreference) {
                String values = ((EditTextPreference) mPreference).getText();
                if (values == null) values = "";
                onPreferenceChangeListener.onPreferenceChange(mPreference, values);
            }
        }

        private Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();

                if (preference instanceof EditTextPreference) {
                    preference.setSummary(stringValue);

                } else if (preference instanceof ListPreference) {

                    /**
                     * ListPreference의 경우 stringValue가 entryValues이기 때문에 바로 Summary를
                     * 적용하지 못한다 따라서 설정한 entries에서 String을 로딩하여 적용한다
                     */
                    ListPreference listPreference = (ListPreference) preference;
                    int index = listPreference.findIndexOfValue(stringValue);

                    preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

                    updateAlarm updateAlarm = new updateAlarm(getActivity());
                    updateAlarm.cancel();

                    if (index == 0) updateAlarm.autoUpdate();
                    else if (index == 1) updateAlarm.SaturdayUpdate();
                    else if (index == 2) updateAlarm.SundayUpdate();

                } else if (preference instanceof CheckBoxPreference) {
                    wonjong.goehs.kr.bearmeat123.wjhighschool.tool.Preference mPref = new wonjong.goehs.kr.bearmeat123.wjhighschool.tool.Preference(getActivity());

                    if (mPref.getBoolean("firstOfAutoUpdate", true)) {
                        mPref.putBoolean("firstOfAutoUpdate", false);
                        showNotification();
                    }

                    if (!mPref.getBoolean("autoBapUpdate", false) && preference.isEnabled()) {
                        int updateLife = Integer.parseInt(mPref.getString("updateLife", "0"));

                        updateAlarm updateAlarm = new updateAlarm(getActivity());
                        if (updateLife == 1) updateAlarm.autoUpdate();
                        else if (updateLife == 0) updateAlarm.SaturdayUpdate();
                        else if (updateLife == -1) updateAlarm.SundayUpdate();

                    } else {
                        updateAlarm updateAlarm = new updateAlarm(getActivity());
                        updateAlarm.cancel();
                    }
                }
                return true;
            }
        };
private void resetdb(){
    Toast.makeText(activity, "리셋 완료", Toast.LENGTH_SHORT).show();
    getPreferenceManager().getSharedPreferences().edit().putBoolean("userAdmin_1", false).apply();
    new File(TimeTableTool.mFilePath + TimeTableTool.TimeTableDBName).delete();
    Preference autoBapUpdate = findPreference("autoBapUpdate");
    wonjong.goehs.kr.bearmeat123.wjhighschool.tool.Preference mPref = new wonjong.goehs.kr.bearmeat123.wjhighschool.tool.Preference(getActivity());
    mPref.getBoolean("autoBapUpdate", false);
    mPref.putBoolean("autoBapUpdate", false);

}
        private void showNotification() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.info_autoUpdate_title);
            builder.setMessage(R.string.info_autoUpdate_msg);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }
    }
}
