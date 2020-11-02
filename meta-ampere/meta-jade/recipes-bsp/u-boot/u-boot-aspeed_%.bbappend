FILESEXTRAPATHS_append_mtjade := "${THISDIR}/${PN}:"

SRC_URI += " \
            file://0001-aspeed-scu-Switch-PWM-pin-to-GPIO-mode.patch \
	    file://0002-aspeed-Add-support-for-passing-system-reset-status.patch \
            file://0003-aspeed-Disable-internal-PD-resistors-for-GPIOs.patch \
           "
