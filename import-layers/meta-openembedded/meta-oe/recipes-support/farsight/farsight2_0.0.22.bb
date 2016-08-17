DESCRIPTION = "FarSight is an audio/video conferencing framework specifically designed for Instant Messengers."
HOMEPAGE = "http://farsight.sf.net"
SRC_URI = "http://farsight.freedesktop.org/releases/farsight2/${BP}.tar.gz"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

PR = "r3"

DEPENDS = "libnice glib-2.0 libxml2 zlib dbus gstreamer gst-plugins-base"

inherit autotools

PACKAGECONFIG ??= ""
PACKAGECONFIG[gupnp] = "--enable-gupnp,--disable-gupnp,gupnp-igd"

EXTRA_OECONF = " \
    --disable-debug \
    --disable-gtk-doc \
    --disable-python \
"

FILES_${PN} += "${libdir}/*/*.so"
FILES_${PN}-dev += "${libdir}/*/*.la"
FILES_${PN}-staticdev += "${libdir}/*/*.a"
FILES_${PN}-dbg += "${libdir}/*/.debug"


SRC_URI[md5sum] = "e1f540cf3ebab06c3d7db1f46b44ac88"
SRC_URI[sha256sum] = "3ae59aa61a8071c9fad111e5fd606aabc27961eb4192f8729987a27dae6b3974"
