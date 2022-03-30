SUMMARY = "GStreamer integration library for Clutter"
DESCRIPTION = "Clutter-Gst is an integration library for using GStreamer with Clutter. \
It provides a GStreamer sink to upload frames to GL and an actor that \
implements the ClutterGstPlayer interface using playbin."
HOMEPAGE = "http://www.clutter-project.org/"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/clutter-gst/-/issues"
LICENSE = "LGPL-2.0-or-later"

inherit clutter features_check upstream-version-is-even gobject-introspection
# depends on clutter-1.0 which depends on cogl-1.0
REQUIRED_DISTRO_FEATURES ?= "opengl"

export GST_PLUGIN_SCANNER_1_0="${S}/gst-plugin-scanner-dummy"

SRC_URI += "file://0001-Install-example-binary-needed-for-core-image-clutter.patch"

DEPENDS = "gstreamer1.0-plugins-base gstreamer1.0-plugins-bad clutter-1.0 libgudev"
RDEPENDS:${PN} += "gstreamer1.0-meta-base"
PACKAGES  =+ "${PN}-examples"

FILES:${PN}          += "${libdir}/gstreamer-1.0/lib*.so"
FILES:${PN}-dev      += "${libdir}/gstreamer-1.0/*.la"
FILES:${PN}-examples  = "${bindir}/video-player ${bindir}/video-sink"
