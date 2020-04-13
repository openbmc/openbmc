SUMMARY = "Gigolo is a frontend to easily manage connections to remote filesystems using GIO/GVfs"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "gtk+3 intltool-native xfce4-dev-tools-native"

inherit xfce-app

SRC_URI[md5sum] = "94e89ad3fabba7167760004b64062f4a"
SRC_URI[sha256sum] = "ca87badb5871e4844579704704ea9e5ede444f710a3b264c12b60b2a0e48e14e"
