require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "xwd is a tool to capture an X window or screen to file"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xwd/"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=c0cdb783e9a0198237371fdaa26a18bf"
DEPENDS = "libxmu libxkbfile"

SRC_URI[sha256sum] = "fbaa2b34163714fe7be22b60920ea4683f63b355babb1781aec2e452a033031b"
