require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "X.Org X11 X client utilities"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xlsclients/"
DESCRIPTION = "xlsclients is a utility for listing information about the \
client applications running on a X11 server."
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=350e1b718a56df39cfe8ca9345ea4575"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "9fbf6b174a5138a61738a42e707ad8f5"
SRC_URI[sha256sum] = "5d9666fcc6c3de210fc70d5a841a404955af709a616fde530fe4e8f7723e3d3d"
