require xorg-app-common.inc

SUMMARY = "Utility for setting various user preference options of the display"

DESCRIPTION = "xset is a utility that is used to set various user \
preference options of the display."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=bea81cc9827cdf1af0e12c2b8228cf8d"
DEPENDS += "libxext libxmu libxau"
PE = "1"

SRC_URI += "file://disable-xkb.patch"

SRC_URI_EXT = "xz"

SRC_URI[sha256sum] = "9f692d55635b3862cd63633b1222a87680ec283c7a8e8ed6dd698a3147f75e2f"

CFLAGS += "-D_GNU_SOURCE"
EXTRA_OECONF = "--disable-xkb --without-fontcache"
