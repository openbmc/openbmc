FILESEXTRAPATHS_append_mtjade := "${THISDIR}/${PN}:"

SRC_URI += " \
            file://0001-aspeed-scu-Switch-PWM-pin-to-GPIO-mode.patch \
	    file://0002-aspeed-Add-support-for-passing-system-reset-status.patch \
            file://0003-aspeed-Disable-internal-PD-resistors-for-GPIOs.patch \
            file://0004-aspeed-add-gpio-support.patch \
            file://0005-aspeed-mtjade-enable-I2C4-device-access.patch \
           "
