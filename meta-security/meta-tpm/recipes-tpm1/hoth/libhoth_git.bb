SUMMARY = "Google Hoth USB library"
DESCRIPTION = "Libraries and example programs for interacting with a \
               hoth-class root of trust."
HOMEPAGE = "https://github.com/google/libhoth"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "git://github.com/google/libhoth;protocol=https;branch=main"
SRCREV = "0e3eec6937d35c602ce497444eda05f3c465b5a8"

DEPENDS += "libusb1"

S = "${WORKDIR}/git"

inherit pkgconfig meson

