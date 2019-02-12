package com.visuality.consent.bridge

import android.app.Activity
import android.content.pm.PackageManager
import com.visuality.consent.request.RequestHolder
import com.visuality.consent.request.RequestOperation
import com.visuality.consent.request.RequestResult
import com.visuality.consent.types.Permission
import com.visuality.consent.types.permissionByNativeName
import kotlin.math.min

fun Activity.getConsent(
    vararg permissions: Permission
): RequestOperation {
    val requestOperation = RequestOperation(
        permissions,
        this,
        null
    ).also { operation ->
        operation.start()
    }
    RequestHolder.currentRequestOperation = requestOperation
    return requestOperation
}

fun Activity.handleConsent(
    requestCode: Int,
    permissions: Array<out String>,
    results: IntArray
): Boolean {
    if (requestCode != RequestOperation.REQUEST_CODE) {
        return false
    }

    val requestOperation = RequestHolder.currentRequestOperation ?: return false
    val callback = requestOperation.onFinishedCallback ?: return false
    RequestHolder.currentRequestOperation = null

    val allowedPermissions = arrayListOf<Permission>()
    val blockedPermissions = arrayListOf<Permission>()

    val endIndex = min(
        permissions.size,
        results.size
    ) - 1

    for (i in 0..endIndex) {
        val permission = permissionByNativeName(
            permissions[i]
        ) ?: continue
        val result = results[i]
        val permissionAllowed = result == PackageManager.PERMISSION_GRANTED

        if (permissionAllowed) {
            allowedPermissions.add(permission)
        } else {
            blockedPermissions.add(permission)
        }
    }

    val requestResult = RequestResult(
        allowedPermissions.toTypedArray(),
        blockedPermissions.toTypedArray()
    )
    callback(requestResult)
    return true
}

fun Activity.resetConsent() {
    val requestOperation = RequestHolder.currentRequestOperation ?: return
    requestOperation.onFinishedCallback = null
    RequestHolder.currentRequestOperation = null
}
