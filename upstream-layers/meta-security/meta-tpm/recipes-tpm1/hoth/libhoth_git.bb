SUMMARY = "Google Hoth USB library"
DESCRIPTION = "Libraries and example programs for interacting with a \
               hoth-class root of trust."
HOMEPAGE = "https://github.com/google/libhoth"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "git://github.com/google/libhoth;protocol=https;branch=main \
           file://0001-Fix-building-with-gcc-15.patch \
           file://0002-Fix-building-without-dbus-backend.patch \
"
SRCREV = "69661d3ea542604353c48a00beee9a6247b27686"

DEPENDS += "libusb1"

inherit pkgconfig meson

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'dbus', '', d)}"

PACKAGECONFIG[dbus] = "-Ddbus_backend=true,-Ddbus_backend=false,systemd libcap"

