SUMMARY = "Gst-Shark Tracers"
DESCRIPTION = "Benchmarks and profiling tools for GStreamer"
HOMEPAGE = "https://developer.ridgerun.com/wiki/index.php?title=GstShark"
SECTION = "multimedia"
LICENSE = "GPL-2.0-or-later"

LIC_FILES_CHKSUM = "file://COPYING;md5=e1caa368743492879002ad032445fa97"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad "

SRCBRANCH ?= "master"

PV = "0.7.3.1"

SRCREV_base = "5413ef5475e5b70476c2480a75ca3746d91d4caf"
SRCREV_common = "b64f03f6090245624608beb5d2fff335e23a01c0"
SRCREV_FORMAT = "base_common"
SRC_URI = " \
    git://github.com/RidgeRun/gst-shark.git;protocol=https;branch=${SRCBRANCH};name=base \
    git://gitlab.freedesktop.org/gstreamer/common.git;protocol=https;destsuffix=git/common;name=common;;branch=master \
    file://0001-tracers-Fix-buffer-overflow.patch \
    "

S = "${WORKDIR}/git"

EXTRA_OECONF += " \
       --disable-graphviz \
       --enable-gtk-doc=no \
"

FILES:${PN} += "\
       ${libdir}/gstreamer-1.0/libgstsharktracers.so  \
       ${libdir}/gstreamer-1.0/libgstsharktracers.la \
"

inherit autotools gettext pkgconfig
