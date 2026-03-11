SUMMARY = "Quickly share a folder using Samba from Thunar"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

inherit thunar-plugin features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
REQUIRED_DISTRO_FEATURES = "pam"

SRC_URI[sha256sum] = "34d4d69d413e63837c5083506b4dbf65f1fd2efe17667b1d7ad0699e1e2eb07d"

RDEPENDS:${PN} += "samba-server"
