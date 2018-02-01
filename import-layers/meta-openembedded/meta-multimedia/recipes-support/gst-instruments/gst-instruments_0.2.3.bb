SUMMARY = "Profiling utilities for GStreamer 1.0 pipelines"
HOMEPAGE = "https://github.com/kirushyk/gst-instruments"
SECTION = "multimedia"

LICENSE = "LGPL-3.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI = "https://github.com/kirushyk/gst-instruments/archive/${PV}.tar.gz"

SRC_URI[md5sum] = "14a394dec25642848b17f9999f1b4999"
SRC_URI[sha256sum] = "5565658a33ff9596946541304fbdd3b3683dbb82171a0e6ce56f42b64a01e58d"

S = "${WORKDIR}/gst-instruments-${PV}"

FILES_${PN} += "${libdir}/*"

INSANE_SKIP_${PN} = "dev-so"

DEPENDS = "gstreamer1.0"

inherit autotools pkgconfig

