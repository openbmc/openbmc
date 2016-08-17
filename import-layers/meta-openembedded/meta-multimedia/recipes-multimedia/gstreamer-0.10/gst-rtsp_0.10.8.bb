SUMMARY = "GStreamer RTSP server"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=55ca817ccb7d5b5b66355690e9abc605"

SRC_URI = "http://gstreamer.freedesktop.org/src/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://0001-change-deprecated-INCLUDES-to-AM_CPPFLAGS-for-automa.patch"
SRC_URI[md5sum] = "b511af07000595f63c3a705946221643"
SRC_URI[sha256sum] = "9915887cf8515bda87462c69738646afb715b597613edc7340477ccab63a6617"

DEPENDS = "gst-plugins-base gstreamer"

EXTRA_OECONF = "--disable-introspection"

# Configure always checks for Python so inherit pythonnative. Better solution
# would be to disable the checks entirely.
inherit autotools pythonnative gettext

FILES_${PN}-dev += "${datadir}/vala/vapi/"
