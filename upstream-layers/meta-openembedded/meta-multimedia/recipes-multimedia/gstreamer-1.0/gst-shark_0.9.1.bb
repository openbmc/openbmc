SUMMARY = "Gst-Shark Tracers"
DESCRIPTION = "Benchmarks and profiling tools for GStreamer"
HOMEPAGE = "https://developer.ridgerun.com/wiki/index.php?title=GstShark"
SECTION = "multimedia"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=e1caa368743492879002ad032445fa97"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad "

SRCBRANCH ?= "master"

SRCREV = "3e3370e3cc66912f88235c43a3b926ea8fc35313"
SRCREV_common = "b64f03f6090245624608beb5d2fff335e23a01c0"
SRCREV_FORMAT = "default_common"
SRC_URI = " \
    git://github.com/RidgeRun/gst-shark.git;protocol=https;branch=${SRCBRANCH} \
    git://gitlab.freedesktop.org/gstreamer/common.git;protocol=https;branch=master;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/common;name=common \
    file://0001-tracers-Fix-buffer-overflow.patch \
"


EXTRA_OECONF += " \
    --disable-graphviz \
    --enable-gtk-doc=no \
    --disable-fatal-warnings \
"

FILES:${PN} += "\
    ${libdir}/gstreamer-1.0/libgstsharktracers.so  \
    ${libdir}/gstreamer-1.0/libgstsharktracers.la \
"

inherit autotools gettext pkgconfig gtk-doc
