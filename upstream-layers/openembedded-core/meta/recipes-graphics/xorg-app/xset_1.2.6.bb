require xorg-app-common.inc

SUMMARY = "Utility for setting various user preference options of the display"

DESCRIPTION = "xset is a utility that is used to set various user \
preference options of the display."

LICENSE = "MIT-open-group"
LIC_FILES_CHKSUM = "file://COPYING;md5=373a2cff5a5e58e7ccff069e0ce9fe74"
DEPENDS += "libxext libxmu libxau"
PE = "1"

SRC_URI += "file://disable-xkb.patch"

SRC_URI_EXT = "xz"

SRC_URI[sha256sum] = "623837349ea887bc003f01ee2e4b6b8ddd9c2774f632c6d70eead1b56306b695"

CFLAGS += "-D_GNU_SOURCE"
EXTRA_OECONF = "--disable-xkb --without-fontcache"
