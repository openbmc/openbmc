require gstreamer1.0-plugins-common.inc

DESCRIPTION = "'Ugly GStreamer plugins"
HOMEPAGE = "https://gstreamer.freedesktop.org/"
BUGTRACKER = "https://gitlab.freedesktop.org/gstreamer/gst-plugins-ugly/-/issues"

LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://tests/check/elements/xingmux.c;beginline=1;endline=21;md5=4c771b8af188724855cb99cadd390068"

LICENSE = "LGPL-2.1-or-later & GPL-2.0-or-later"
LICENSE_FLAGS = "commercial"

SRC_URI = " \
            https://gstreamer.freedesktop.org/src/gst-plugins-ugly/gst-plugins-ugly-${PV}.tar.xz \
            "
SRC_URI[sha256sum] = "4e8dcb5d26552f0a4937f6bc6279bd9070f55ca6ae0eaa32d72d264c44001c2e"

S = "${WORKDIR}/gst-plugins-ugly-${PV}"

DEPENDS += "gstreamer1.0-plugins-base"

GST_PLUGIN_SET_HAS_EXAMPLES = "0"

PACKAGECONFIG ??= " \
    ${GSTREAMER_ORC} \
    gpl a52dec mpeg2dec \
"

PACKAGECONFIG[amrnb]    = "-Damrnb=enabled,-Damrnb=disabled,opencore-amr"
PACKAGECONFIG[amrwb]    = "-Damrwbdec=enabled,-Damrwbdec=disabled,opencore-amr"

# GPL - only built if gpl option is also enabled!
PACKAGECONFIG[gpl]      = "-Dgpl=enabled,-Dgpl=disabled"
PACKAGECONFIG[cdio]     = "-Dcdio=enabled,-Dcdio=disabled,libcdio"
PACKAGECONFIG[a52dec]   = "-Da52dec=enabled,-Da52dec=disabled,liba52"
PACKAGECONFIG[dvdread]  = "-Ddvdread=enabled,-Ddvdread=disabled,libdvdread"
PACKAGECONFIG[mpeg2dec] = "-Dmpeg2dec=enabled,-Dmpeg2dec=disabled,mpeg2dec"
PACKAGECONFIG[x264]     = "-Dx264=enabled,-Dx264=disabled,x264"

EXTRA_OEMESON += " \
    -Ddoc=disabled \
    -Dsidplay=disabled \
"

FILES:${PN}-amrnb += "${datadir}/gstreamer-1.0/presets/GstAmrnbEnc.prs"
FILES:${PN}-x264 += "${datadir}/gstreamer-1.0/presets/GstX264Enc.prs"
