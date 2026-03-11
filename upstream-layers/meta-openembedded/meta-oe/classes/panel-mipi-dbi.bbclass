# SPDX-License-Identifier: MIT
#
# Copyright Pengutronix <yocto@pengutronix.de>
#
# Class to generate firmware files for use with the `panel-mipi-dbi` Linux
# driver.
#
# The firmware source file contains a list of commands to send to the display
# controller in order to initialize it:
#
#   $ cat shineworld,lh133k.txt
#   command 0x11 # exit sleep mode
#   delay 120
#
#   # Enable color inversion
#   command 0x21 # INVON
#   ...
#
# A recipe to compile such a command list into a firmware blob for use with
# the `panel-mipi-dbi` driver looks something like this:
#
#   $ cat panel-shineworld-lh133k.bb
#   inherit panel-mipi-dbi
#
#   SRC_URI = "file://${PANEL_FIRMWARE}"
#
#   PANEL_FIRMWARE = "shineworld,lh133k.txt"
#   ...

DEPENDS = "panel-mipi-dbi-native"

PANEL_FIRMWARE_BIN ?= "${@d.getVar('PANEL_FIRMWARE').removesuffix('.txt')}.bin"

do_configure[noexec] = "1"

do_compile () {
    mipi-dbi-cmd \
        "${B}/${PANEL_FIRMWARE_BIN}" \
        "${UNPACKDIR}/${PANEL_FIRMWARE}"
}

do_install () {
    install -m 0644 -D \
        "${B}/${PANEL_FIRMWARE_BIN}" \
        "${D}${nonarch_base_libdir}/firmware/${PANEL_FIRMWARE_BIN}"
}

FILES:${PN} = "${nonarch_base_libdir}/firmware/"
