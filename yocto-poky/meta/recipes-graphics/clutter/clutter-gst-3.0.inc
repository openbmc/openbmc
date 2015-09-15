SUMMARY = "GStreamer integration library for Clutter"
HOMEPAGE = "http://www.clutter-project.org/"
LICENSE = "LGPLv2+"

inherit clutter

SRC_URI += "file://0001-Install-example-binary-needed-for-core-image-clutter.patch"

DEPENDS = "gstreamer1.0-plugins-base gstreamer1.0-plugins-bad clutter-1.0"
RDEPENDS_${PN} += "gstreamer1.0-meta-base"
PACKAGES  =+ "${PN}-examples"

EXTRA_OECONF += "--disable-introspection"

FILES_${PN}          += "${libdir}/gstreamer-1.0/lib*.so"
FILES_${PN}-dev      += "${libdir}/gstreamer-1.0/*.la"
FILES_${PN}-dbg      += "${libdir}/gstreamer-1.0/.debug/lib*.so"
FILES_${PN}-examples  = "${bindir}/video-player ${bindir}/video-sink"
