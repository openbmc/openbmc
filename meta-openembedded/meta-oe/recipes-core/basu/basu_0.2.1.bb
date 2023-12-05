SUMMARY = "The sd-bus library, extracted from systemd."
HOMEPAGE = "https://sr.ht/~emersion/basu"
SECTION = "base"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL2.1;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://git.sr.ht/~emersion/basu;protocol=https;branch=master"

DEPENDS += "gperf-native"

S = "${WORKDIR}/git"
PV = "0.2.1+git"
SRCREV = "a08cf5a575325435040d35710dbac3d0c32b1676"

inherit meson pkgconfig

PACKAGECONFIG ?= ""
PACKAGECONFIG[libcap] = "-Dlibcap=enabled,-Dlibcap=disabled,libcap"
PACKAGECONFIG[audit] = "-Daudit=enabled,-Daudit=disabled,audit"

EXTRA_OEMESON += "--buildtype release"
