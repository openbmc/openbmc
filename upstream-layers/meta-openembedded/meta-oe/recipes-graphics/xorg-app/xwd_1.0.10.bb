require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "xwd is a tool to capture an X window or screen to file"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xwd/"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=c0cdb783e9a0198237371fdaa26a18bf"
DEPENDS = "libxmu libxkbfile"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "4909eacc26c4cd5a9afcd1468aec26e7c646544639031bdad4f71173aa82f3c3"
