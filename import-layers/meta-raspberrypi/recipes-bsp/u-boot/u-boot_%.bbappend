FILESEXTRAPATHS_prepend_rpi := "${THISDIR}/files:"
RDEPENDS_${PN}_append_rpi = " rpi-u-boot-scr"
SRC_URI_append_rpi = " \
    file://0001-arm-add-save_boot_params-for-ARM1176.patch \
    file://0002-rpi-passthrough-of-the-firmware-provided-FDT-blob.patch \
    file://0003-Include-lowlevel_init.o-for-rpi2.patch \
    "
