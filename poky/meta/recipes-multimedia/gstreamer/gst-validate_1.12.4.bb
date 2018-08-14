SUMMARY = "Gstreamer validation tool"
DESCRIPTION = "A Tool to test GStreamer components"
HOMEPAGE = "https://gstreamer.freedesktop.org/releases/gst-validate/1.12.3.html"
SECTION = "multimedia"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343"

SRC_URI = "https://gstreamer.freedesktop.org/src/${BPN}/${BP}.tar.xz"
SRC_URI[md5sum] = "bc074d49677081f9c27de11a09165746"
SRC_URI[sha256sum] = "f9da9dfe6e5d6f5ba3b38c5752b42d3f927715904942b405c2924d3cb77afba1"

DEPENDS = "json-glib glib-2.0 gstreamer1.0 gstreamer1.0-plugins-base"
RRECOMMENDS_${PN} = "git"

FILES_${PN} += "${datadir}/gstreamer-1.0/* ${libdir}/gst-validate-launcher/* ${libdir}/gstreamer-1.0/*"

inherit pkgconfig gettext autotools gobject-introspection gtk-doc upstream-version-is-even

# With gtk-doc enabled this recipe fails to build, so forcibly disable it:
# WORKDIR/build/docs/validate/gst-validate-scan: line 117:
# WORKDIR/build/docs/validate/.libs/lt-gst-validate-scan: No such file or directory
GTKDOC_ENABLED = "False"
