SUMMARY = "FFmpeg-based GStreamer plug-in"
SECTION = "multimedia"
LICENSE = "GPLv2+ & LGPLv2+ & ( (GPLv2+ & LGPLv2.1+) | (GPLv3+ & LGPLv3+) )"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://ext/libpostproc/gstpostproc.c;beginline=1;endline=18;md5=5896e445e41681324381f5869ee33d38 \
                    file://COPYING.LIB;md5=55ca817ccb7d5b5b66355690e9abc605 \
                    file://ext/ffmpeg/gstffmpeg.h;beginline=1;endline=18;md5=ff65467b0c53cdfa98d0684c1bc240a9 \
                    file://gst-libs/ext/libav/LICENSE;md5=abc3b8cb02856aa7823bbbd162d16232 \
                    file://gst-libs/ext/libav/COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://gst-libs/ext/libav/COPYING.GPLv3;md5=d32239bcb673463ab874e80d47fae504 \
                    file://gst-libs/ext/libav/COPYING.LGPLv2.1;md5=e344c8fa836c3a41c4cbd79d7bd3a379 \
                    file://gst-libs/ext/libav/COPYING.LGPLv3;md5=e6a600fd5e1d9cbde2d983680233ad02"
LICENSE_FLAGS = "commercial"
HOMEPAGE = "http://www.gstreamer.net/"
DEPENDS = "gstreamer gst-plugins-base zlib bzip2 yasm-native libpostproc"

inherit autotools-brokensep pkgconfig

SRC_URI = "http://gstreamer.freedesktop.org/src/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://lower-rank.diff \
           file://configure-fix.patch \
           file://h264_qpel_mmx.patch \
           file://libav_e500mc.patch \
           file://libav_e5500.patch \
           file://gst-ffmpeg-CVE-2013-3674.patch \
           file://0001-avformat-mpegtsenc-Check-data-array-size-in-mpegts_w.patch \
           file://0001-vqavideo-check-chunk-sizes-before-reading-chunks.patch \
           file://0001-avcodec-msrle-use-av_image_get_linesize-to-calculate.patch \
           file://0001-huffyuvdec-Skip-len-0-cases.patch \
           file://0001-huffyuvdec-Check-init_vlc-return-codes.patch \
           file://0001-alsdec-check-block-length.patch \
           file://0001-pgssubdec-check-RLE-size-before-copying.-Fix-out-of-.patch \
           file://0001-atrac3dec-Check-coding-mode-against-channels.patch \
           file://0001-eamad-fix-out-of-array-accesses.patch \
           file://0001-mjpegdec-check-SE.patch \
           file://0001-alac-fix-nb_samples-order-case.patch \
           file://0001-h264-correct-ref-count-check-and-limit-fix-out-of-ar.patch \
           file://0001-roqvideodec-check-dimensions-validity.patch \
           file://0001-aacdec-check-channel-count.patch \
           file://0001-pngdec-filter-dont-access-out-of-array-elements-at-t.patch \
           file://0001-error_concealment-Check-that-the-picture-is-not-in-a.patch \
           file://0001-vp3-fix-oob-read-for-negative-tokens-and-memleaks-on.patch \
           file://0001-vp3-Copy-all-3-frames-for-thread-updates.patch \
           file://0001-h264_sei-Fix-infinite-loop.patch \
           file://0001-avcodec-parser-reset-indexes-on-realloc-failure.patch \
           file://0001-avcodec-rpza-Perform-pointer-advance-and-checks-befo.patch \
           file://gst-ffmpeg-CVE-2013-0855.patch \
           file://0001-qdm2dec-fix-buffer-overflow.patch \
           file://0001-smackerdec-Check-that-the-last-indexes-are-within-th.patch \
           file://0001-avcodec-dsputil-fix-signedness-in-sizeof-comparissio.patch \
           file://0001-error-concealment-initialize-block-index.patch \
           file://0001-qdm2-check-array-index-before-use-fix-out-of-array-a.patch \
           file://0001-lavf-compute-probe-buffer-size-more-reliably.patch \
           file://0001-ffserver-set-oformat.patch \
           file://0001-h264-set-parameters-from-SPS-whenever-it-changes.patch \
           file://0001-h264-skip-error-concealment-when-SPS-and-slices-are-.patch \
           file://0001-avcodec-smc-fix-off-by-1-error.patch \
           file://0002-avcodec-mjpegdec-check-bits-per-pixel-for-changes-si.patch \
           file://libav-9.patch \
           file://gst-ffmpeg-fix-CVE-2011-4352.patch \
           file://gst-ffmpeg-fix-CVE-2014-7933.patch \
           file://gst-ffmpeg-fix-CVE-2014-8542.patch \
           file://gst-ffmpeg-fix-CVE-2014-8543.patch \
           file://gst-ffmpeg-fix-CVE-2014-8544.patch \
           file://gst-ffmpeg-fix-CVE-2014-8545.patch \
           file://gst-ffmpeg-fix-CVE-2014-8546.patch \
           file://gst-ffmpeg-fix-CVE-2014-8547.patch \
           file://gst-ffmpeg-fix-CVE-2014-9318.patch \
           file://gst-ffmpeg-fix-CVE-2014-9603.patch \
"

SRC_URI[md5sum] = "7f5beacaf1312db2db30a026b36888c4"
SRC_URI[sha256sum] = "76fca05b08e00134e3cb92fa347507f42cbd48ddb08ed3343a912def187fbb62"

PR = "r8"

GSTREAMER_DEBUG ?= "--disable-debug"

FFMPEG_EXTRA_CONFIGURE = "--with-ffmpeg-extra-configure"
# pass --cpu for powerpc. get cpu name by stripping "ppc" or "ppc64"
# from DEFAULTTUNE
FFMPEG_CPU_powerpc = "--cpu=${@d.getVar('DEFAULTTUNE', False)[3:]}"
FFMPEG_CPU_powerpc64 = "--cpu=${@d.getVar('DEFAULTTUNE', False)[5:]}"
FFMPEG_EXTRA_CONFIGURE_COMMON_ARG = "--target-os=linux ${FFMPEG_CPU} \
  --cc='${CC}' --as='${CC}' --ld='${CC}' --nm='${NM}' --ar='${AR}' \
  --ranlib='${RANLIB}' \
  ${GSTREAMER_DEBUG}"
FFMPEG_EXTRA_CONFIGURE_COMMON = \
'${FFMPEG_EXTRA_CONFIGURE}="${FFMPEG_EXTRA_CONFIGURE_COMMON_ARG}"'

EXTRA_OECONF = "${FFMPEG_EXTRA_CONFIGURE_COMMON}"

PACKAGECONFIG ??= "external-libav"
PACKAGECONFIG[external-libav] = "--with-system-ffmpeg,,libav"
PACKAGECONFIG[orc] = "--enable-orc,--disable-orc,orc"

FILES_${PN} += "${libdir}/gstreamer-0.10/*.so"
FILES_${PN}-dbg += "${libdir}/gstreamer-0.10/.debug"
FILES_${PN}-dev += "${libdir}/gstreamer-0.10/*.la"
FILES_${PN}-staticdev += "${libdir}/gstreamer-0.10/*.a"

# http://errors.yoctoproject.org/Errors/Details/40736/
PNBLACKLIST[gst-ffmpeg] ?= "Not compatible with currently used ffmpeg 3"
