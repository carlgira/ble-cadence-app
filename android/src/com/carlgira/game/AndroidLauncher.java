package com.carlgira.game;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.carlgira.game.BLECadenceTest;
import com.carlgira.game.adapter.DeviceAdapter;
import com.carlgira.game.base.Callback;
import com.carlgira.game.comm.ObserverManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;

public class AndroidLauncher extends AndroidApplication {

	private BleManager bleManager;
	public DeviceAdapter mDeviceAdapter;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bleManager = new BleManager();
		bleManager.init(getApplication());
		bleManager.setAndroidApp(this);

		mDeviceAdapter = new DeviceAdapter(this);
		mDeviceAdapter.setOnDeviceClickListener(new DeviceAdapter.OnDeviceClickListener() {
			@Override
			public void onConnect(BleDevice bleDevice) {
				if (!com.clj.fastble.BleManager.getInstance().isConnected(bleDevice)) {
					com.clj.fastble.BleManager.getInstance().cancelScan();
					connect(bleDevice);
				}
			}

			@Override
			public void onDisConnect(final BleDevice bleDevice) {
				if (com.clj.fastble.BleManager.getInstance().isConnected(bleDevice)) {
					com.clj.fastble.BleManager.getInstance().disconnect(bleDevice);
				}
			}

			@Override
			public void onDetail(BleDevice bleDevice) {

			}
		});



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

	private Callback<com.carlgira.game.BleDevice> callback;
	public void checkPermissions(Callback callback) {
		this.callback = callback;
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
					setScanRule();
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

	private void setScanRule() {
		UUID[] serviceUuids = new UUID[]{UUID.fromString("00001816-0000-1000-8000-00805F9B34FB")};

		boolean isAutoConnect = true;

		BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
				.setServiceUuids(serviceUuids)
				//.setDeviceName(true, names)
				//.setDeviceMac(mac)
				.setAutoConnect(isAutoConnect)
				.setScanTimeOut(10000)
				.build();
		com.clj.fastble.BleManager.getInstance().initScanRule(scanRuleConfig);
	}

	private void startScan() {
		com.clj.fastble.BleManager.getInstance().scan(new BleScanCallback() {
			@Override
			public void onScanStarted(boolean success) {
				mDeviceAdapter.clearScanDevice();
				mDeviceAdapter.notifyDataSetChanged();
			}

			@Override
			public void onLeScan(com.clj.fastble.data.BleDevice bleDevice) {
				super.onLeScan(bleDevice);
			}

			@Override
			public void onScanning(com.clj.fastble.data.BleDevice bleDevice) {
				mDeviceAdapter.addDevice(bleDevice);
				mDeviceAdapter.notifyDataSetChanged();
			}

			@Override
			public void onScanFinished(List<BleDevice> scanResultList) {
				List<com.carlgira.game.BleDevice> devices = new ArrayList<>();
				for(int i =0;i< scanResultList.size();i++){
					devices.add(new com.carlgira.game.BleDevice(scanResultList.get(i)));
				}
				callback.call(devices);
				callback = null;
			}
		});
	}

	private void connect(final BleDevice bleDevice) {
		com.clj.fastble.BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
			@Override
			public void onStartConnect() {


			}

			@Override
			public void onConnectFail(BleDevice bleDevice, BleException exception) {

			}

			@Override
			public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
				mDeviceAdapter.addDevice(bleDevice);
				mDeviceAdapter.notifyDataSetChanged();
			}

			@Override
			public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
				mDeviceAdapter.removeDevice(bleDevice);
				mDeviceAdapter.notifyDataSetChanged();

				if (isActiveDisConnected) {

				} else {
					ObserverManager.getInstance().notifyObserver(bleDevice);
				}

			}
		});
	}
}