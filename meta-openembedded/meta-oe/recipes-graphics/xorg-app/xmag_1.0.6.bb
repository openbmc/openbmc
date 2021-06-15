require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "xmag is a tool to magnify parts of the screen"
HOMEPAGE = "http://cgit.freedesktop.org/xorg/app/xmag/"
SECTION = "x11/app"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=3413fe6832380b44b69b172d2d1b2387"
DEPENDS += " libxaw libxt"

SRC_URI[md5sum] = "8aaa41374935d697ee55d7dc9de70781"
SRC_URI[sha256sum] = "87a2bc23b251e2d8f8370d3283a4d6c8dac98a30cb5749a04336cdb55c14e161"
