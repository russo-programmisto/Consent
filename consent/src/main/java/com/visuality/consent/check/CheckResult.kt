package com.visuality.consent.check

data class CheckResult(
    val allowed: Array<out String>,
    val blocked: Array<out String>
) {

    val hasBlocked: Boolean
        get() = this.blocked.size > 0
}
