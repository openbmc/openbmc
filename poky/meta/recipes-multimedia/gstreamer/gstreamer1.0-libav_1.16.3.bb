SUMMARY = "Libav-based GStreamer 1.x plugin"
HOMEPAGE = "http://gstreamer.freedesktop.org/"
SECTION = "multimedia"

LICENSE = "GPLv2+ & LGPLv2+ & ( (GPLv2+ & LGPLv2.1+) | (GPLv3+ & LGPLv3+) )"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=6762ed442b3822387a51c92d928ead0d \
                    file://ext/libav/gstav.h;beginline=1;endline=18;md5=a752c35267d8276fd9ca3db6994fca9c"

SRC_URI = "https://gstreamer.freedesktop.org/src/gst-libav/gst-libav-${PV}.tar.xz \
           "
SRC_URI[md5sum] = "d08fb5429f102d5a3f1eca3dee2a0add"
SRC_URI[sha256sum] = "d10c5eb1a00a91de97c85c0956c663aa6e99d268195cdec4534c179b831538ec"

S = "${WORKDIR}/gst-libav-${PV}"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base ffmpeg"

inherit meson pkgconfig upstream-version-is-even

FILES_${PN} += "${libdir}/gstreamer-1.0/*.so"
FILES_${PN}-staticdev += "${libdir}/gstreamer-1.0/*.a"

delete_unused_libav_copy() {
        # When building with meson, the internal libav copy is not used.
        # It is only present for legacy autotools based builds. In future
        # GStreamer versions, the autotools scripts will be gone, and so
        # will this internal copy. Until then, it will be present. In order
        # to make sure this copy is not included in the -src package, just
        # manually delete the copy.
        rm -rf "${S}/gst-libs/ext/libav/"
}

do_unpack[postfuncs] += " delete_unused_libav_copy"
