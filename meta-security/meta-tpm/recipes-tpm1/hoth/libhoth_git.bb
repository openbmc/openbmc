SUMMARY = "Google Hoth USB library"
DESCRIPTION = "Libraries and example programs for interacting with a \
               hoth-class root of trust."
HOMEPAGE = "https://github.com/google/libhoth"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "git://github.com/google/libhoth;protocol=https;branch=main"
SRCREV = "e520f8fa637589324ec56d34f26a48a8162a250c"

DEPENDS += "libusb1"

S = "${WORKDIR}/git"

inherit pkgconfig meson

