DEFAULT_PREFERENCE = "-1"

include gstreamer1.0-libav.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=6762ed442b3822387a51c92d928ead0d \
                    file://ext/libav/gstav.h;beginline=1;endline=18;md5=a752c35267d8276fd9ca3db6994fca9c \
                    file://gst-libs/ext/libav/LICENSE.md;md5=acda96fe91ccaabc9cd9d541806a0d37 \
                    file://gst-libs/ext/libav/COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://gst-libs/ext/libav/COPYING.GPLv3;md5=d32239bcb673463ab874e80d47fae504 \
                    file://gst-libs/ext/libav/COPYING.LGPLv2.1;md5=bd7a443320af8c812e4c18d1b79df004 \
                    file://gst-libs/ext/libav/COPYING.LGPLv3;md5=e6a600fd5e1d9cbde2d983680233ad02"

# To build using the system libav/ffmpeg, append "libav" to PACKAGECONFIG
# and remove the ffmpeg sources from SRC_URI below. However, first note the
# warnings in gstreamer1.0-libav.inc
SRC_URI = " \
    git://anongit.freedesktop.org/gstreamer/gst-libav;name=base \
    git://anongit.freedesktop.org/gstreamer/common;destsuffix=git/common;name=common \
    git://source.ffmpeg.org/ffmpeg;destsuffix=git/gst-libs/ext/libav;name=ffmpeg;branch=release/3.0 \
    file://0001-Disable-yasm-for-libav-when-disable-yasm.patch \
    file://workaround-to-build-gst-libav-for-i586-with-gcc.patch \
"

PV = "1.7.2+git${SRCPV}"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"

SRCREV_base = "6c4878b6abc916b7f6f25b8926c4859119acf1ec"
SRCREV_common = "b64f03f6090245624608beb5d2fff335e23a01c0"
SRCREV_ffmpeg = "c40983a6f631d22fede713d535bb9c31d5c9740c"
SRCREV_FORMAT = "base"

S = "${WORKDIR}/git"

do_configure_prepend() {
	${S}/autogen.sh --noconfigure
}
