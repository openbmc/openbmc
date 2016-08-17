require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "xmag is a tool to magnify parts of the screen"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xmag/"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=3413fe6832380b44b69b172d2d1b2387"
DEPENDS += " libxaw libxt"

SRC_URI[md5sum] = "280b81cb09d1903aa868a058d90128ad"
SRC_URI[sha256sum] = "4ace8795cf756be4ba387f30366045594ce26adda2f5ffe1f8e896825d0932c6"
