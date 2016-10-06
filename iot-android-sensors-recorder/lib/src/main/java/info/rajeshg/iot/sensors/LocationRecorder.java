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

package info.rajeshg.iot.sensors;

import android.Manifest;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Recorder for GPS location updates.
 */
public class LocationRecorder implements Recorder, LocationListener {

    public static final String PREF_KEY = SensorsRecorder.PREF_SENSOR_ + "location";

    protected List<String> requiredPermissions;
    protected FrequencyMeasure measure = new FrequencyMeasure(5500, 10500, 100);
    protected SensorsRecorder sensorsRecorder;
    protected boolean started;

    public LocationRecorder(SensorsRecorder sensorsRecorder) {
        this.sensorsRecorder = sensorsRecorder;
        this.requiredPermissions = new ArrayList<>();
        this.requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    public short getTypeId() {
        return SensorsRecorder.TYPE_GPS;
    }

    @Override
    public short getDeviceId() {
        return 0;
    }

    @Override
    public String getPrefKey() {
        return PREF_KEY;
    }

    @Override
    public String getShortName() {
        return sensorsRecorder.getGpsName();
    }

    @Override
    public String getDescription() {
        return sensorsRecorder.getGpsDescription();
    }

    @Override
    public FrequencyMeasure getFrequencyMeasure() {
        return measure;
    }

    @Override
    public Collection<String> getRequiredPermissions() {
        return requiredPermissions;
    }

    @Override
    public void start() {
        if (!started) {
            try {
                sensorsRecorder.getLocationManager()
                        .requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                started = true;
                measure.onStarted();
            } catch (SecurityException ex) {
                measure.onPermissionDenied();
            }
        }
    }

    @Override
    public void stop() {
        if (started) {
            try {
                sensorsRecorder.getLocationManager().removeUpdates(this);
            } catch (SecurityException ex) {
                // Ignore
            }
            started = false;
        }
        measure.onStopped();
    }

    @Override
    public void onLocationChanged(Location location) {
        long millisecond = measure.onNewSample();
        sensorsRecorder.getOutput()
                .start(SensorsRecorder.TYPE_GPS, getDeviceId())
                .write(sensorsRecorder.getIMEI())
                .write(millisecond)
                .write(location.getLatitude())
                .write(location.getLongitude())
                .write(location.getAltitude())
                .write(location.getBearing())
                .write(location.getSpeed())
                .write(location.getAccuracy())
                .write(location.getTime())
                .save();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
