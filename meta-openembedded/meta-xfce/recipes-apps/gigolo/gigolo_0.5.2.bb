SUMMARY = "Gigolo is a frontend to easily manage connections to remote filesystems using GIO/GVfs"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "gtk+3 intltool-native xfce4-dev-tools-native"

inherit xfce-app

SRC_URI[sha256sum] = "e34a1aa0755f9f6c234c7d24b23a6cecd6ef50741d79da3bb6f698a2281dbbc3"
