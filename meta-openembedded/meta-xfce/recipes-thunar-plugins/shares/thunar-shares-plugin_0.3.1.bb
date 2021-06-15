SUMMARY = "Quickly share a folder using Samba from Thunar"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

inherit thunar-plugin features_check
REQUIRED_DISTRO_FEATURES = "pam"

SRC_URI[md5sum] = "0884855d60cf1031b9013b6f5b125372"
SRC_URI[sha256sum] = "dc1d8c7caa727e76d033d4653dc0742613f57a1711d0050900659c90a84452a0"

RDEPENDS_${PN} += "samba-server"
