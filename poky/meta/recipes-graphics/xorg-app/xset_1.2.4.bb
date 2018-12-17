require xorg-app-common.inc

SUMMARY = "Utility for setting various user preference options of the display"

DESCRIPTION = "xset is a utility that is used to set various user \
preference options of the display."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=bea81cc9827cdf1af0e12c2b8228cf8d"
DEPENDS += "libxext libxxf86misc libxmu libxau"
PE = "1"

SRC_URI += "file://disable-xkb.patch"

SRC_URI[md5sum] = "70ea7bc7bacf1a124b1692605883f620"
SRC_URI[sha256sum] = "e4fd95280df52a88e9b0abc1fee11dcf0f34fc24041b9f45a247e52df941c957"

CFLAGS += "-D_GNU_SOURCE"
EXTRA_OECONF = "--disable-xkb --without-fontcache"
