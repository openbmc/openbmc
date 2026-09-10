require xorg-app-common.inc

SUMMARY = "Print out X-Video extension adaptor information"

DESCRIPTION = "xvinfo prints out the capabilities of any video adaptors \
associated with the display that are accessible through the X-Video \
extension."

LIC_FILES_CHKSUM = "file://COPYING;md5=b664101ad7a1dc758a4c4109bf978e68"
DEPENDS += " libxv"
PE = "1"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "a436945e6a4ab70590358eec2b85d26970f7de480d27e8a25af8fe8421e88ae2"

XORGBUILDCLASS = "meson"
