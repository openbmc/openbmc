SUMMARY = "Profiling utilities for GStreamer 1.0 pipelines"
HOMEPAGE = "https://github.com/kirushyk/gst-instruments"
SECTION = "multimedia"

LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6a600fd5e1d9cbde2d983680233ad02"
DEPENDS = "gstreamer1.0"
SRCREV = "cb8977a6711657e32853159cd539d1d75fcbc772"
PV = "0.3.1+git${SRCPV}"

SRC_URI = "git://github.com/kirushyk/gst-instruments.git;protocol=https;branch=master"

S = "${WORKDIR}/git"

inherit vala meson pkgconfig

FILES:${PN}-staticdev += "${libdir}/gstreamer-1.0/*a"
FILES:${PN} += "${libdir}/*"

INSANE_SKIP:${PN}-dev = "dev-elf"

PACKAGECONFIG ??= "ui"
PACKAGECONFIG[ui] = "-Dui=enabled,-Dui=disabled,gtk+3"
