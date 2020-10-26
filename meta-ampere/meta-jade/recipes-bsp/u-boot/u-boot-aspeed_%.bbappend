FILESEXTRAPATHS_append_mtjade := "${THISDIR}/${PN}:"

SRC_URI += " \
            file://0001-aspeed-scu-Switch-PWM-pin-to-GPIO-mode.patch \
           "
