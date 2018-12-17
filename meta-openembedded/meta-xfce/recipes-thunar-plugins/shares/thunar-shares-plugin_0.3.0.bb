SUMMARY = "Quickly share a folder using Samba from Thunar"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

inherit thunar-plugin distro_features_check
REQUIRED_DISTRO_FEATURES = "pam"

SRC_URI[md5sum] = "a1773168c0b3c7c0f253a339f02e5ae2"
SRC_URI[sha256sum] = "d6adc01ca45a3d2567a2a969a3b16d1799a8975453ab1803a065fa82496b5b65"

RDEPENDS_${PN} += "samba-server"
