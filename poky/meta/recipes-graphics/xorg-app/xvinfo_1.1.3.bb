require xorg-app-common.inc

SUMMARY = "Print out X-Video extension adaptor information"

DESCRIPTION = "xvinfo prints out the capabilities of any video adaptors \
associated with the display that are accessible through the X-Video \
extension."

LIC_FILES_CHKSUM = "file://COPYING;md5=b664101ad7a1dc758a4c4109bf978e68"
DEPENDS += " libxv"
PE = "1"

SRC_URI[md5sum] = "558360176b718dee3c39bc0648c0d10c"
SRC_URI[sha256sum] = "9fba8b68daf53863e66d5004fa9c703fcecf69db4d151ea2d3d885d621e6e5eb"
