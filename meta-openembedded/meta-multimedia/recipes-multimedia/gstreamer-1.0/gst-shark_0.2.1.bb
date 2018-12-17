SUMMARY = "Gst-Shark Tracers"
DESCRIPTION = "Benchmarks and profiling tools for GStreamer"
HOMEPAGE = "https://developer.ridgerun.com/wiki/index.php?title=GstShark"
SECTION = "multimedia"
LICENSE = "GPLv2+"

LIC_FILES_CHKSUM = "file://COPYING;md5=e1caa368743492879002ad032445fa97"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad "

SRCBRANCH ?= "master"

SRCREV_base = "a60b3996fe3376d42334fc89014e9d6f6af62899"
SRCREV_common = "b64f03f6090245624608beb5d2fff335e23a01c0"

SRC_URI = " \
    git://github.com/RidgeRun/gst-shark.git;protocol=https;branch=${SRCBRANCH};name=base \
    git://anongit.freedesktop.org/git/gstreamer/common.git;protocol=https;destsuffix=git/common;name=common; \
    "

S = "${WORKDIR}/git"

PACKAGECONFIG_CONFARGS = " \
       --disable-graphviz \
       --enable-gtk-doc=no \
"

FILES_${PN} += "\
       ${libdir}/gstreamer-1.0/libgstsharktracers.so  \
       ${libdir}/gstreamer-1.0/libgstsharktracers.la \
"

inherit autotools gettext

do_configure() {
        ${S}/autogen.sh --noconfigure
        oe_runconf
}
