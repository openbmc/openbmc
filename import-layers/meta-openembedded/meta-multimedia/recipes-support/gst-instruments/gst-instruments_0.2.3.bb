SUMMARY = "Profiling utilities for GStreamer 1.0 pipelines"
HOMEPAGE = "https://github.com/kirushyk/gst-instruments"
SECTION = "multimedia"

LICENSE = "LGPL-3.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6a600fd5e1d9cbde2d983680233ad02"

DEPENDS = "gstreamer1.0"

S = "${WORKDIR}/git"
SRCREV = "4ce8092636ee6572148b5fa044080734cf5a6b8d"
SRC_URI = "git://github.com/kirushyk/gst-instruments.git;protocol=https;"

FILES_${PN}-staticdev += "${libdir}/gstreamer-1.0/*a"
FILES_${PN} += "${libdir}/*"

INSANE_SKIP_${PN} = "dev-so"

inherit autotools pkgconfig

