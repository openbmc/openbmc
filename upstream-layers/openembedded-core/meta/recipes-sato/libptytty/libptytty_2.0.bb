SUMMARY = " OS independent and secure pty/tty and utmp/wtmp/lastlog handling"
HOMEPAGE = "http://software.schmorp.de/pkg/libptytty.html"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

SRC_URI = "http://dist.schmorp.de/libptytty/libptytty-${PV}.tar.gz \
           file://0001-CMakeLists.txt-do-not-run-cross-binary.patch \
           "
SRC_URI[sha256sum] = "8033ed3aadf28759660d4f11f2d7b030acf2a6890cb0f7926fb0cfa6739d31f7"

inherit cmake

EXTRA_OECMAKE:append:libc-musl = " -DWTMP_SUPPORT=OFF"
