SUMMARY = "Gigolo is a frontend to easily manage connections to remote filesystems using GIO/GVfs"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "gtk+3 intltool-native xfce4-dev-tools-native"

inherit xfce-app

SRC_URI[sha256sum] = "d25984f65744665e2433335249f9547a38cead45440027af0c397ebf254d2fd0"
