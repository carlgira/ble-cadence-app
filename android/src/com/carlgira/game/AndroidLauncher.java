package com.carlgira.game;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.scan.BleScanRuleConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AndroidLauncher extends AndroidApplication {

	private BleController bleManager;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bleManager = new BleController();
		bleManager.init(getApplication());
		bleManager.setAndroidApp(this);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		initialize(new BLECadenceTest(bleManager), config);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		bleManager.disconnectAllDevice();
		bleManager.destroy();
	}

	@Override
	public final void onRequestPermissionsResult(int requestCode,
												 String[] permissions,
												 int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case REQUEST_CODE_PERMISSION_LOCATION:
				if (grantResults.length > 0) {
					for (int i = 0; i < grantResults.length; i++) {
						if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
							onPermissionGranted(permissions[i]);
						}
					}
				}
				break;
		}
	}

	private static final int REQUEST_CODE_OPEN_GPS = 1;
	private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;

	private BleScanCallback scanCallback;
	private String serviceUUID;


	public void checkPermissions(String uuid, BleScanCallback callback) {
		this.scanCallback = callback;
		this.serviceUUID = uuid;
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!bluetoothAdapter.isEnabled()) {
			Toast.makeText(this, "R.string.please_open_blue", Toast.LENGTH_LONG).show();
			return;
		}

		String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
		List<String> permissionDeniedList = new ArrayList<>();
		for (String permission : permissions) {
			int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
			if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
				onPermissionGranted(permission);
			} else {
				permissionDeniedList.add(permission);
			}
		}
		if (!permissionDeniedList.isEmpty()) {
			String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
			ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
		}
	}

	private void onPermissionGranted(String permission) {
		switch (permission) {
			case Manifest.permission.ACCESS_FINE_LOCATION:
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {

					if (Looper.myLooper() == null) {
						Looper.prepare();
					}
					new AlertDialog.Builder(this)
							.setTitle("R.string.notifyTitle")
							.setMessage("R.string.gpsNotifyMsg")
							.setNegativeButton("R.string.cancel",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											finish();
										}
									})
							.setPositiveButton("R.string.setting",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
											startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
										}
									})

							.setCancelable(false)
							.show();
				} else {
					setScanRule(this.serviceUUID);
					scanCallback.onScanStarted(true);
					startScan();
				}
				break;
		}
	}

	private boolean checkGPSIsOpen() {
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager == null)
			return false;
		return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
	}

	private void setScanRule(String uuid) {
		UUID[] serviceUuids = new UUID[]{UUID.fromString(uuid)};

		boolean isAutoConnect = true;

		BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
				.setServiceUuids(serviceUuids)
				.setAutoConnect(isAutoConnect)
				.setScanTimeOut(10000)
				.build();
		com.clj.fastble.BleManager.getInstance().initScanRule(scanRuleConfig);
	}

	private void startScan() {
		com.clj.fastble.BleManager.getInstance().scan(scanCallback);
	}

}
