# gem5-arm64 support
COMPATIBLE_MACHINE:gem5-arm64 = "gem5-arm64"
FILESEXTRAPATHS:prepend:gem5-arm64 := "${THISDIR}/files:"
SRC_URI:append:gem5-arm64 = " file://early-printk.cfg"
