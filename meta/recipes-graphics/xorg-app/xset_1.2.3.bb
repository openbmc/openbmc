require xorg-app-common.inc

SUMMARY = "Utility for setting various user preference options of the display"

DESCRIPTION = "xset is a utility that is used to set various user \
preference options of the display."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=bea81cc9827cdf1af0e12c2b8228cf8d"
DEPENDS += "libxext libxxf86misc libxmu libxau"
PE = "1"

SRC_URI += "file://disable-xkb.patch"

SRC_URI[md5sum] = "dcd227388b57487d543cab2fd7a602d7"
SRC_URI[sha256sum] = "4382f4fb29b88647e13f3b4bc29263134270747fc159cfc5f7e3af23588c8063"

CFLAGS += "-D_GNU_SOURCE"
EXTRA_OECONF = "--disable-xkb --without-fontcache"
