FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot:"

SRC_URI_append_rpi = " \
    file://0002-rpi_0_w-Add-configs-consistent-with-RpI3.patch \
"

DEPENDS_append_rpi = " rpi-u-boot-scr"
