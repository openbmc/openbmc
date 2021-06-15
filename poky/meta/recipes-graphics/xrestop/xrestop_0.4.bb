SUMMARY = "XRes: A top-like resource usage tool for X"

DESCRIPTION = "top-like statistics of X11 server resource usage by clients"

HOMEPAGE = "http://www.freedesktop.org/wiki/Software/xrestop"
BUGTRACKER = "https://bugs.freedesktop.org/"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://xrestop.c;endline=18;md5=730876c30f0d8a928676bcd1242a3b35"

SECTION = "x11/utils"
PR = "r3"

DEPENDS = "libxres libxext virtual/libx11 ncurses"

SRC_URI = "http://downloads.yoctoproject.org/releases/xrestop/xrestop-${PV}.tar.gz"

SRC_URI[md5sum] = "d8a54596cbaf037e62b80c4585a3ca9b"
SRC_URI[sha256sum] = "67c2fc94a7ecedbaae0d1837e82e93d1d98f4a6d759828860e552119af3ce257"

inherit autotools pkgconfig features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"
