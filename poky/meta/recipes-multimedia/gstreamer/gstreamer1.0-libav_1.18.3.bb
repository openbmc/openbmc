SUMMARY = "Libav-based GStreamer 1.x plugin"
DESCRIPTION = "Contains a GStreamer plugin for using the encoders, decoders, \
muxers, and demuxers provided by FFmpeg."
HOMEPAGE = "http://gstreamer.freedesktop.org/"
SECTION = "multimedia"

# ffmpeg has comercial license flags so add it as we need ffmpeg as a dependency
LICENSE_FLAGS = "commercial"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=6762ed442b3822387a51c92d928ead0d \
                    file://ext/libav/gstav.h;beginline=1;endline=18;md5=a752c35267d8276fd9ca3db6994fca9c \
                    "

SRC_URI = "https://gstreamer.freedesktop.org/src/gst-libav/gst-libav-${PV}.tar.xz"
SRC_URI[sha256sum] = "ad20546bcd78ac1e7cf194666d73c4f33efeb62647d2b6af22993b540699e91c"

S = "${WORKDIR}/gst-libav-${PV}"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base ffmpeg"

inherit meson pkgconfig upstream-version-is-even

FILES_${PN} += "${libdir}/gstreamer-1.0/*.so"
FILES_${PN}-staticdev += "${libdir}/gstreamer-1.0/*.a"
