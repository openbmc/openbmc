require xorg-app-common.inc

SUMMARY = "Print out X-Video extension adaptor information"

DESCRIPTION = "xvinfo prints out the capabilities of any video adaptors \
associated with the display that are accessible through the X-Video \
extension."

LIC_FILES_CHKSUM = "file://COPYING;md5=b664101ad7a1dc758a4c4109bf978e68"
DEPENDS += " libxv"
PE = "1"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "3ede71ecb26d9614ccbc6916720285e95a2c7e0c5e19b8570eaaf72ad7c5c404"
