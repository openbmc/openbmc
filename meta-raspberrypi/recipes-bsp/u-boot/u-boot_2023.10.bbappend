FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:rpi = " file://0001-bootstd-Scan-all-bootdevs-in-a-boot_targets-entry.patch"
