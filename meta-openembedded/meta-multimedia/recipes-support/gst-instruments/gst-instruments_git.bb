SUMMARY = "Profiling utilities for GStreamer 1.0 pipelines"
HOMEPAGE = "https://github.com/kirushyk/gst-instruments"
SECTION = "multimedia"

LICENSE = "LGPL-3.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6a600fd5e1d9cbde2d983680233ad02"

DEPENDS = "gstreamer1.0"

S = "${WORKDIR}/git"
SRCREV = "3b862e52e5c53ad1023dc6808effa4cb75572c4b"
SRC_URI = "git://github.com/kirushyk/gst-instruments.git;protocol=https;branch=master"

FILES_${PN}-staticdev += "${libdir}/gstreamer-1.0/*a"
FILES_${PN} += "${libdir}/*"

INSANE_SKIP_${PN} = "dev-so"

inherit autotools pkgconfig

