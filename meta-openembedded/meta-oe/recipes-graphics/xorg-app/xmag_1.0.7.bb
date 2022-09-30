require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "xmag is a tool to magnify parts of the screen"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xmag/"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=3413fe6832380b44b69b172d2d1b2387"
DEPENDS += " libxaw libxt"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "009936cc5a2706084079127b26cf55c713767650a34cb69e5682d60e33ce7461"
