SUMMARY = "A library on top of GStreamer for building an RTSP server"
SECTION = "multimedia"
LICENSE = "LGPLv2"
HOMEPAGE = "http://cgit.freedesktop.org/gstreamer/gst-rtsp-server/"
DEPENDS = "gstreamer1.0 libcgroup gstreamer1.0-plugins-base"

PNREAL = "gst-rtsp-server"

SRC_URI = "http://gstreamer.freedesktop.org/src/${PNREAL}/${PNREAL}-${PV}.tar.xz"

S = "${WORKDIR}/${PNREAL}-${PV}"

inherit autotools pkgconfig 

