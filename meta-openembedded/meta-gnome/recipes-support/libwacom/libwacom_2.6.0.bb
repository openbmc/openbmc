SUMMARY = "A tablet description library"
DESCRIPTION = "libwacom is a library to identify Wacom tablets and their model-specific features. \
               It provides easy access to information such as 'is this a built-in on-screen tablet\', \
               'what is the size of this model', etc."
HOMEPAGE = "https://github.com/linuxwacom/libwacom"
BUGTRACKER = "https://github.com/linuxwacom/libwacom/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=40a21fffb367c82f39fd91a3b137c36e"

SRC_URI = "git://github.com/linuxwacom/libwacom.git;branch=master;protocol=https"
SRCREV = "cb36c462763a321454d5c08fe974a3d7dec4ed1a"

DEPENDS = " \
    libxml2-native \
    libgudev \
"

S = "${WORKDIR}/git"

inherit meson pkgconfig

EXTRA_OEMESON = " \
    -Dtests=disabled \
"
