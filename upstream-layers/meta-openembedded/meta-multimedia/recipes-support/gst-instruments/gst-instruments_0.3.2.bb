SUMMARY = "Profiling utilities for GStreamer 1.0 pipelines"
HOMEPAGE = "https://github.com/kirushyk/gst-instruments"
SECTION = "multimedia"

LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6a600fd5e1d9cbde2d983680233ad02"
DEPENDS = "gstreamer1.0"
SRCREV = "0fdaed3fbabe272e02cfc3b0a956b36da9f4ce63"

SRC_URI = "git://github.com/kirushyk/gst-instruments.git;protocol=https;branch=master;tag=v${PV}"


inherit vala meson pkgconfig

FILES:${PN}-staticdev += "${libdir}/gstreamer-1.0/*a"
FILES:${PN} += "${libdir}/*"

INSANE_SKIP:${PN}-dev = "dev-elf"
