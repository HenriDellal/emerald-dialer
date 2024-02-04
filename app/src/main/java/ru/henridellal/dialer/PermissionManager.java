package ru.henridellal.dialer;

import static android.os.Build.VERSION_CODES.M;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class PermissionManager {
	public static final String[] PERMISSIONS = {
			Manifest.permission.CALL_PHONE,
			Manifest.permission.READ_CALL_LOG,
			Manifest.permission.READ_CONTACTS,
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.WRITE_CALL_LOG
	};

	@TargetApi(M)
	public static boolean isPermissionGrantedRaw(Context context, String permission) {
		return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
	}

	public static boolean isPermissionGranted(Context context, String permission) {
		if (Build.VERSION.SDK_INT < 23) {
			return true;
		}
		return isPermissionGrantedRaw(context, permission);
	}

	@TargetApi(M)
	public static boolean hasRequiredPermissions(Context context) {
		for (int i = 0; i < PERMISSIONS.length; i++) {
			if (!isPermissionGrantedRaw(context, PERMISSIONS[i])) {
				return false;
			}
		}
		return true;
	}
}
