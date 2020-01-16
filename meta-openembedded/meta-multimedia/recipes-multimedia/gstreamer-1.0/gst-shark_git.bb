SUMMARY = "Gst-Shark Tracers"
DESCRIPTION = "Benchmarks and profiling tools for GStreamer"
HOMEPAGE = "https://developer.ridgerun.com/wiki/index.php?title=GstShark"
SECTION = "multimedia"
LICENSE = "GPLv2+"

LIC_FILES_CHKSUM = "file://COPYING;md5=e1caa368743492879002ad032445fa97"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad "

SRCBRANCH ?= "master"

PV = "0.6.1"

SRCREV_base = "c41a05cc9e2310c2f73eda4b4f0b4477bf4479c5"
SRCREV_common = "88e512ca7197a45c4114f7fa993108f23245bf50"

SRC_URI = " \
    git://github.com/RidgeRun/gst-shark.git;protocol=https;branch=${SRCBRANCH};name=base \
    git://gitlab.freedesktop.org/gstreamer/common.git;protocol=https;destsuffix=git/common;name=common; \
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
