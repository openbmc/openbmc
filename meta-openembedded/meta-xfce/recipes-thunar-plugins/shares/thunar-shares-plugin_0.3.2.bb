SUMMARY = "Quickly share a folder using Samba from Thunar"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

inherit thunar-plugin features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
REQUIRED_DISTRO_FEATURES = "pam"

SRC_URI[sha256sum] = "1009d5e6c91534fa49a69090c53c54ab9da2e0428d08d8e687528f63a4ac3f07"

RDEPENDS:${PN} += "samba-server"
