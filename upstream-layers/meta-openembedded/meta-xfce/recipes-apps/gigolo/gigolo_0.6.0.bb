SUMMARY = "Gigolo is a frontend to easily manage connections to remote filesystems using GIO/GVfs"
HOMEPAGE = "https://docs.xfce.org/apps/gigolo/start"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

XFCEBASEBUILDCLASS = "meson"
XFCE_COMPRESS_TYPE = "xz"

DEPENDS = "gtk+3 intltool-native xfce4-dev-tools-native"

inherit xfce-app

SRC_URI[sha256sum] = "f27dbb51abe8144c1b981f2d820ad1b279c1bc4623d7333b7d4f5f4777eb45ed"
