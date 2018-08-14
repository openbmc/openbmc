SUMMARY = "Gigolo is a frontend to easily manage connections to remote filesystems using GIO/GVfs"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "gtk+ intltool-native xfce4-dev-tools-native"

inherit xfce-app

SRC_URI[md5sum] = "4abc6fde56572adf3ec3a0181092584c"
SRC_URI[sha256sum] = "553fc78fe4e7bd2f01f3851baea7e63f6414fe652dfb4b08c60b5c4b2b909164"
