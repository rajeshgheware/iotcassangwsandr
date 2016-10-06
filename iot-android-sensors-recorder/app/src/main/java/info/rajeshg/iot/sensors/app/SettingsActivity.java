/*
 * (C) Copyright 2013, 2015 Wojciech Mruczkiewicz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Wojciech Mruczkiewicz
 */

package info.rajeshg.iot.sensors.app;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.List;
import java.util.Set;

import info.rajeshg.iot.sensors.PhysicalRecorderComparator;
import info.rajeshg.iot.sensors.app.R;
import info.rajeshg.iot.sensors.Recorder;
import info.rajeshg.iot.sensors.SensorsRecorder;
import info.rajeshg.iot.sensors.app.util.SwitchPreference;

/**
 * Activity for configuring recording.
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String KEY_SENSORS = "pref_sensors";
    private static final String KEY_NETWORK = "pref_network";

    private BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                HeartRateDialog heartRateDialog = getHeartRateDialog();
                if (heartRateDialog != null) {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR);
                    heartRateDialog.onBluetoothAdapterState(state);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(bluetoothStateReceiver);
        super.onDestroy();
    }

    private HeartRateDialog getHeartRateDialog() {
        FragmentManager fm = getSupportFragmentManager();
        return (HeartRateDialog) fm.findFragmentByTag(HeartRateDialog.DIALOG_TAG);
    }

    public static class SettingsFragment extends PreferenceFragment implements
            SharedPreferences.OnSharedPreferenceChangeListener {

        private SensorsRecorder recorder;
        private SharedPreferences preferences;
        private Preference samplingPref;
        private Preference networkPref;
        private Preference heartRatePref;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            preferences.registerOnSharedPreferenceChangeListener(this);

            networkPref = getPreferenceScreen().findPreference(KEY_NETWORK);
            networkPref.setSummary(getNetworkSummary());
            networkPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    FragmentTransaction ft = ((AppCompatActivity) getActivity())
                            .getSupportFragmentManager().beginTransaction();
                    ft.add(new NetworkDialog(), NetworkDialog.DIALOG_TAG);
                    ft.commit();
                    return true;
                }
            });

            samplingPref = getPreferenceScreen()
                    .findPreference(SensorsRecorder.PREF_SAMPLING_PERIOD);
            samplingPref.setSummary(getSamplingSummary());
            samplingPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    FragmentTransaction ft = ((AppCompatActivity) getActivity())
                            .getSupportFragmentManager().beginTransaction();
                    ft.add(new SamplingPeriodDialog(), SamplingPeriodDialog.DIALOG_TAG);
                    ft.commit();
                    return true;
                }
            });


            PreferenceCategory sensors = (PreferenceCategory) getPreferenceScreen()
                    .findPreference(KEY_SENSORS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 &&
                    getActivity().getPackageManager().hasSystemFeature(
                            PackageManager.FEATURE_BLUETOOTH_LE)) {
                heartRatePref = new Preference(getActivity());
                heartRatePref.setTitle(R.string.pref_heart_rate);
                heartRatePref.setSummary(getHeartRateSummary());
                heartRatePref.setOnPreferenceClickListener(
                        new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        FragmentTransaction ft = ((AppCompatActivity) getActivity())
                                .getSupportFragmentManager().beginTransaction();
                        ft.add(new HeartRateDialog(), HeartRateDialog.DIALOG_TAG);
                        ft.commit();
                        return true;
                    }
                });
                sensors.addPreference(heartRatePref);
            }

            recorder = RecordingService.getRecorder(getActivity());
            PhysicalRecorderComparator physicalComparator = recorder.getPhysicalComparator();
            List<Recorder> all = recorder.getFixedRecorders();
            for (int i = 0; i < all.size(); ++i) {
                Recorder r = all.get(i);
                if (r.getPrefKey() != null) {
                    Preference pref = new SwitchPreference(getActivity());
                    pref.setKey(r.getPrefKey());
                    pref.setDefaultValue(physicalComparator.isDefaultEnabled(r));
                    pref.setTitle(r.getShortName());
                    pref.setSummary(r.getDescription());
                    sensors.addPreference(pref);
                }
            }
        }

        @Override
        public void onDestroy() {
            preferences.unregisterOnSharedPreferenceChangeListener(this);
            super.onDestroy();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (SensorsRecorder.PREF_SAMPLING_PERIOD.equals(key)) {
                samplingPref.setSummary(getSamplingSummary());
            } else if (SensorsRecorder.PREF_NETWORK_SAVE.equals(key) ||
                    SensorsRecorder.PREF_NETWORK_HOST.equals(key) ||
                    SensorsRecorder.PREF_NETWORK_PORT.equals(key) ||
                    SensorsRecorder.PREF_NETWORK_PROTOCOL.equals(key)) {
                networkPref.setSummary(getNetworkSummary());
            } else if (SensorsRecorder.PREF_BLE_DEVICES.equals(key)) {
                heartRatePref.setSummary(getHeartRateSummary());
            }

            if (recorder.isRecording() && (SensorsRecorder.PREF_SAMPLING_PERIOD.equals(key) ||
                    SensorsRecorder.PREF_SAVE_BINARY.equals(key))) {
                Snackbar.make(getView(), R.string.pref_sensors_restart, Snackbar.LENGTH_LONG)
                        .show();
            }
        }

        private String getSamplingSummary() {
            long delay = preferences.getLong(SensorsRecorder.PREF_SAMPLING_PERIOD,
                    SensorsRecorder.DEFAULT_SAMPLING_PERIOD);
            int position = SamplingPeriodDialog.getSamplingPosition(delay);
            if (position == SamplingPeriodDialog.POSITION_CUSTOM) {
                return getString(R.string.pref_sampling_period_value, delay);
            } else {
                String[] constants = getResources()
                        .getStringArray(R.array.sampling_period_default_values);
                return getString(R.string.pref_sampling_period_constant, constants[position]);
            }
        }

        private String getNetworkSummary() {
            if (preferences.getBoolean(SensorsRecorder.PREF_NETWORK_SAVE,
                    SensorsRecorder.DEFAULT_NETWORK_SAVE)) {
                String[] protocol = getResources().getStringArray(R.array.network_protocol_values);
                return getString(R.string.pref_network_value,
                        preferences.getString(SensorsRecorder.PREF_NETWORK_HOST,
                                SensorsRecorder.DEFAULT_HOST),
                        protocol[preferences.getInt(SensorsRecorder.PREF_NETWORK_PROTOCOL,
                                SensorsRecorder.DEFAULT_PROTOCOL)],
                        preferences.getInt(SensorsRecorder.PREF_NETWORK_PORT,
                                SensorsRecorder.DEFAULT_PORT));
            } else {
                return getString(R.string.pref_network_disabled);
            }
        }

        private String getHeartRateSummary() {
            Set<String> devices = preferences.getStringSet(SensorsRecorder.PREF_BLE_DEVICES, null);
            int count = devices != null ? devices.size() : 0;
            return getResources()
                    .getQuantityString(R.plurals.pref_heart_rate_summary, count, count);
        }
    }

}
