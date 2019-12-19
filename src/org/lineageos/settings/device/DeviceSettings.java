/*
 * Copyright (C) 2018 The Xiaomi-SDM660 Project
 * Copyright (C) 2019 Mohammad Hasan Keramat Jahromi m.h.k.jahromi@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package org.lineageos.settings.device;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;

import org.lineageos.settings.device.kcal.KCalSettingsActivity;
import org.lineageos.settings.device.preferences.SecureSettingCustomSeekBarPreference;
import org.lineageos.settings.device.preferences.SecureSettingListPreference;
import org.lineageos.settings.device.preferences.SecureSettingSwitchPreference;
import org.lineageos.settings.device.preferences.VibrationSeekBarPreference;

public class DeviceSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String PREF_TORCH_BRIGHTNESS = "torch_brightness";
    public static final String TORCH_1_BRIGHTNESS_PATH = "/sys/devices/soc/400f000.qcom," +
            "spmi/spmi-0/spmi0-03/400f000.qcom,spmi:qcom,pmi8994@3:qcom,leds@d300/leds/led:torch_0/max_brightness";
    public static final String TORCH_2_BRIGHTNESS_PATH = "/sys/devices/soc/400f000.qcom," +
            "spmi/spmi-0/spmi0-03/400f000.qcom,spmi:qcom,pmi8994@3:qcom,leds@d300/leds/led:torch_1/max_brightness";

    public static final String PREF_VIBRATION_STRENGTH = "vibration_strength";
    public static final String VIBRATION_STRENGTH_PATH = "/sys/class/timed_output/vibrator/vtg_level";
	
    // value of vtg_min and vtg_max
    public static final int MIN_VIBRATION = 12;
    public static final int MAX_VIBRATION = 127;
	
    public static final String CATEGORY_DISPLAY = "display";
    public static final String PREF_DEVICE_DOZE = "device_doze";
    public static final String PREF_DEVICE_KCAL = "device_kcal";

    public static final String PREF_SPECTRUM = "spectrum";
    public static final String SPECTRUM_SYSTEM_PROPERTY = "persist.spectrum.profile";

    public static final String PREF_ENABLE_DIRAC = "dirac_enabled";
    public static final String PREF_HEADSET = "dirac_headset_pref";
    public static final String PREF_PRESET = "dirac_preset_pref";

	// buttons
    public static final String CATEGORY_SWAPBUTTONS = "buttons";
    public static final String PREF_BUTTONS = "swapbuttons";
    public static final String BUTTONS_PATH = "/proc/touchpanel/reversed_keys_enable";

	// fpactions
	public static final String CATEGORY_FPWAKEUP = "fp_wakeup";
    public static final String PREF_FP_WAKEUP = "fpwakeup";
    public static final String FP_WAKEUP_PATH = "/sys/devices/soc/soc:fpc_fpc1020/enable_wakeup";
	public static final String CATEGORY_FPHOME = "fp_home";
    public static final String PREF_FP_HOME = "fphome";
    public static final String FP_HOME_PATH = "/sys/devices/soc/soc:fpc_fpc1020/enable_key_events";
	public static final String CATEGORY_FPPOCKET = "fp_pocket";
    public static final String PREF_FP_POCKET = "fppocket";
    public static final String FP_POCKET_PATH = "/sys/devices/soc/soc:fpc_fpc1020/proximity_state";
	
	//gestures
    public static final String CATEGORY_DT2W = "dt2_w";
    public static final String PREF_DT2_W = "dt2w";
    public static final String DT2_W_PATH = "/proc/touchpanel/double_tap_enable";
	
    public static final String DEVICE_DOZE_PACKAGE_NAME = "org.lineageos.settings.doze";

    private SecureSettingCustomSeekBarPreference mTorchBrightness;
    private VibrationSeekBarPreference mVibrationStrength;
    private Preference mKcal;
    private SecureSettingListPreference mSPECTRUM;
    private SecureSettingSwitchPreference mEnableDirac;
    private SecureSettingListPreference mHeadsetType;
    private SecureSettingListPreference mPreset;
    private SecureSettingSwitchPreference mSwapbuttons;
    private SecureSettingSwitchPreference mFpwakeup;
    private SecureSettingSwitchPreference mFphome;
    private SecureSettingSwitchPreference mDt2w;
	private SecureSettingSwitchPreference mFppocket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_xiaomi_parts, rootKey);

        String device = FileUtils.getStringProp("ro.build.product", "unknown");

        mTorchBrightness = (SecureSettingCustomSeekBarPreference) findPreference(PREF_TORCH_BRIGHTNESS);
        mTorchBrightness.setEnabled(FileUtils.fileWritable(TORCH_1_BRIGHTNESS_PATH) &&
                FileUtils.fileWritable(TORCH_2_BRIGHTNESS_PATH));
        mTorchBrightness.setOnPreferenceChangeListener(this);

        mVibrationStrength = (VibrationSeekBarPreference) findPreference(PREF_VIBRATION_STRENGTH);
        mVibrationStrength.setEnabled(FileUtils.fileWritable(VIBRATION_STRENGTH_PATH));
        mVibrationStrength.setOnPreferenceChangeListener(this);
		
        PreferenceCategory displayCategory = (PreferenceCategory) findPreference(CATEGORY_DISPLAY);
        if (isAppNotInstalled(DEVICE_DOZE_PACKAGE_NAME)) {
            displayCategory.removePreference(findPreference(PREF_DEVICE_DOZE));
        }

        mKcal = findPreference(PREF_DEVICE_KCAL);

        mKcal.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), KCalSettingsActivity.class);
            startActivity(intent);
            return true;
        });

        mSPECTRUM = (SecureSettingListPreference) findPreference(PREF_SPECTRUM);
        mSPECTRUM.setValue(FileUtils.getStringProp(SPECTRUM_SYSTEM_PROPERTY, "0"));
        mSPECTRUM.setSummary(mSPECTRUM.getEntry());
        mSPECTRUM.setOnPreferenceChangeListener(this);

        boolean enhancerEnabled;
        try {
            enhancerEnabled = DiracService.sDiracUtils.isDiracEnabled();
        } catch (java.lang.NullPointerException e) {
            getContext().startService(new Intent(getContext(), DiracService.class));
            enhancerEnabled = DiracService.sDiracUtils.isDiracEnabled();
        }

        mEnableDirac = (SecureSettingSwitchPreference) findPreference(PREF_ENABLE_DIRAC);
        mEnableDirac.setOnPreferenceChangeListener(this);
        mEnableDirac.setChecked(enhancerEnabled);

        mHeadsetType = (SecureSettingListPreference) findPreference(PREF_HEADSET);
        mHeadsetType.setOnPreferenceChangeListener(this);

        mPreset = (SecureSettingListPreference) findPreference(PREF_PRESET);
        mPreset.setOnPreferenceChangeListener(this);

        if (FileUtils.fileWritable(BUTTONS_PATH)) {
            mSwapbuttons = (SecureSettingSwitchPreference) findPreference(PREF_BUTTONS);
            mSwapbuttons.setChecked(FileUtils.getFileValueAsBoolean(BUTTONS_PATH, false));
            mSwapbuttons.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(findPreference(CATEGORY_SWAPBUTTONS));
        }
		
        if (FileUtils.fileWritable(FP_WAKEUP_PATH)) {
            mFpwakeup = (SecureSettingSwitchPreference) findPreference(PREF_FP_WAKEUP);
            mFpwakeup.setChecked(FileUtils.getFileValueAsBoolean(FP_WAKEUP_PATH, false));
            mFpwakeup.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(findPreference(CATEGORY_FPWAKEUP));
        }
		
        if (FileUtils.fileWritable(FP_HOME_PATH)) {
            mFphome = (SecureSettingSwitchPreference) findPreference(PREF_FP_HOME);
            mFphome.setChecked(FileUtils.getFileValueAsBoolean(FP_HOME_PATH, false));
            mFphome.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(findPreference(CATEGORY_FPHOME));
        }
		
        if (FileUtils.fileWritable(DT2_W_PATH)) {
            mDt2w = (SecureSettingSwitchPreference) findPreference(PREF_DT2_W);
            mDt2w.setChecked(FileUtils.getFileValueAsBoolean(DT2_W_PATH, false));
            mDt2w.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(findPreference(CATEGORY_DT2W));
        }
		
        if (FileUtils.fileWritable(FP_POCKET_PATH)) {
            mFppocket = (SecureSettingSwitchPreference) findPreference(PREF_FP_POCKET);
            mFppocket.setChecked(FileUtils.getFileValueAsBoolean(FP_POCKET_PATH, false));
            mFppocket.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(findPreference(CATEGORY_FPPOCKET));
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        final String key = preference.getKey();
        switch (key) {
            case PREF_TORCH_BRIGHTNESS:
                FileUtils.setValue(TORCH_1_BRIGHTNESS_PATH, (int) value);
                FileUtils.setValue(TORCH_2_BRIGHTNESS_PATH, (int) value);
                break;

            case PREF_VIBRATION_STRENGTH:
                double vibrationValue = (int) value / 100.0 * (MAX_VIBRATION - MIN_VIBRATION) + MIN_VIBRATION;
                FileUtils.setValue(VIBRATION_STRENGTH_PATH, vibrationValue);
                break;
				
            case PREF_SPECTRUM:
                mSPECTRUM.setValue((String) value);
                mSPECTRUM.setSummary(mSPECTRUM.getEntry());
                FileUtils.setStringProp(SPECTRUM_SYSTEM_PROPERTY, (String) value);
                break;

            case PREF_ENABLE_DIRAC:
                try {
                    DiracService.sDiracUtils.setEnabled((boolean) value);
                } catch (java.lang.NullPointerException e) {
                    getContext().startService(new Intent(getContext(), DiracService.class));
                    DiracService.sDiracUtils.setEnabled((boolean) value);
                }
                break;

            case PREF_HEADSET:
                try {
                    DiracService.sDiracUtils.setHeadsetType(Integer.parseInt(value.toString()));
                } catch (java.lang.NullPointerException e) {
                    getContext().startService(new Intent(getContext(), DiracService.class));
                    DiracService.sDiracUtils.setHeadsetType(Integer.parseInt(value.toString()));
                }
                break;

            case PREF_PRESET:
                try {
                    DiracService.sDiracUtils.setLevel(String.valueOf(value));
                } catch (java.lang.NullPointerException e) {
                    getContext().startService(new Intent(getContext(), DiracService.class));
                    DiracService.sDiracUtils.setLevel(String.valueOf(value));
                }
                break;

            case PREF_BUTTONS:
                FileUtils.setValue(BUTTONS_PATH, (boolean) value);
                break;
				
            case PREF_FP_WAKEUP:
                FileUtils.setValue(FP_WAKEUP_PATH, (boolean) value);
                break;
				
            case PREF_FP_HOME:
                FileUtils.setValue(FP_HOME_PATH, (boolean) value);
                break;
				
            case PREF_DT2_W:
                FileUtils.setValue(DT2_W_PATH, (boolean) value);
                break;
				
            case PREF_FP_POCKET:
                FileUtils.setValue(FP_POCKET_PATH, (boolean) value);
                break;

            default:
                break;
        }
        return true;
    }

    private boolean isAppNotInstalled(String uri) {
        PackageManager packageManager = getContext().getPackageManager();
        try {
            packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }
}
