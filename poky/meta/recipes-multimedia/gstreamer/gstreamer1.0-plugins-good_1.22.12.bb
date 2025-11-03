require gstreamer1.0-plugins-common.inc

SUMMARY = "'Good' GStreamer plugins"
HOMEPAGE = "https://gstreamer.freedesktop.org/"
BUGTRACKER = "https://gitlab.freedesktop.org/gstreamer/gst-plugins-good/-/issues"

SRC_URI = "https://gstreamer.freedesktop.org/src/gst-plugins-good/gst-plugins-good-${PV}.tar.xz \
           file://0001-qt-include-ext-qt-gstqtgl.h-instead-of-gst-gl-gstglf.patch \
           file://0001-v4l2-Define-ioctl_req_t-for-posix-linux-case.patch \
           file://0001-qtdemux-Skip-zero-sized-boxes-instead-of-stopping-to.patch \
           file://0002-qtdemux-Fix-integer-overflow-when-allocating-the-sam.patch \
           file://0003-qtdemux-Fix-debug-output-during-trun-parsing.patch \
           file://0004-qtdemux-Don-t-iterate-over-all-trun-entries-if-none-.patch \
           file://0005-qtdemux-Check-sizes-of-stsc-stco-stts-before-trying-.patch \
           file://0006-qtdemux-Make-sure-only-an-even-number-of-bytes-is-pr.patch \
           file://0007-qtdemux-Make-sure-enough-data-is-available-before-re.patch \
           file://0008-qtdemux-Fix-length-checks-and-offsets-in-stsd-entry-.patch \
           file://0009-qtdemux-Fix-error-handling-when-parsing-cenc-sample-.patch \
           file://0010-qtdemux-Make-sure-there-are-enough-offsets-to-read-w.patch \
           file://0011-qtdemux-Actually-handle-errors-returns-from-various-.patch \
           file://0012-qtdemux-Check-for-invalid-atom-length-when-extractin.patch \
           file://0013-qtdemux-Add-size-check-for-parsing-SMI-SEQH-atom.patch \
           file://0014-gdkpixbufdec-Check-if-initializing-the-video-info-ac.patch \
           file://0015-matroskademux-Only-unmap-GstMapInfo-in-WavPack-heade.patch \
           file://0016-matroskademux-Fix-off-by-one-when-parsing-multi-chan.patch \
           file://0017-matroskademux-Check-for-big-enough-WavPack-codec-pri.patch \
           file://0018-matroskademux-Don-t-take-data-out-of-an-empty-adapte.patch \
           file://0019-matroskademux-Skip-over-laces-directly-when-postproc.patch \
           file://0020-matroskademux-Skip-over-zero-sized-Xiph-stream-heade.patch \
           file://0021-matroskademux-Put-a-copy-of-the-codec-data-into-the-.patch \
           file://0022-jpegdec-Directly-error-out-on-negotiation-failures.patch \
           file://0023-qtdemux-Avoid-integer-overflow-when-parsing-Theora-e.patch \
           file://0024-avisubtitle-Fix-size-checks-and-avoid-overflows-when.patch \
           file://0025-wavparse-Check-for-short-reads-when-parsing-headers-.patch \
           file://0026-wavparse-Make-sure-enough-data-for-the-tag-list-tag-.patch \
           file://0027-wavparse-Fix-parsing-of-acid-chunk.patch \
           file://0028-wavparse-Check-that-at-least-4-bytes-are-available-b.patch \
           file://0029-wavparse-Check-that-at-least-32-bytes-are-available-.patch \
           file://0030-wavparse-Fix-clipping-of-size-to-the-file-size.patch \
           file://0031-wavparse-Check-size-before-reading-ds64-chunk.patch \
           file://CVE-2025-47183-001.patch \
           file://CVE-2025-47183-002.patch \
           file://CVE-2025-47219.patch \
          "

SRC_URI[sha256sum] = "9c1913f981900bd8867182639b20907b28ed78ef7a222cfbf2d8ba9dab992fa7"

S = "${WORKDIR}/gst-plugins-good-${PV}"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://gst/replaygain/rganalysis.c;beginline=1;endline=23;md5=b60ebefd5b2f5a8e0cab6bfee391a5fe"

DEPENDS += "gstreamer1.0-plugins-base libcap zlib"
RPROVIDES:${PN}-pulseaudio += "${PN}-pulse"
RPROVIDES:${PN}-soup += "${PN}-souphttpsrc"
RDEPENDS:${PN}-soup += "${MLPREFIX}${@bb.utils.contains('PACKAGECONFIG', 'soup2', 'libsoup-2.4', 'libsoup', d)}"

PACKAGECONFIG_SOUP ?= "soup3"

PACKAGECONFIG ??= " \
    ${GSTREAMER_ORC} \
    ${PACKAGECONFIG_SOUP} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'pulseaudio x11', d)} \
    ${@bb.utils.contains('TUNE_FEATURES', 'm64', 'asm', '', d)} \
    bz2 cairo flac gdk-pixbuf gudev jpeg lame libpng mpg123 speex taglib v4l2 \
"

X11DEPENDS = "virtual/libx11 libsm libxrender libxfixes libxdamage"
X11ENABLEOPTS = "-Dximagesrc=enabled -Dximagesrc-xshm=enabled -Dximagesrc-xfixes=enabled -Dximagesrc-xdamage=enabled"
X11DISABLEOPTS = "-Dximagesrc=disabled -Dximagesrc-xshm=disabled -Dximagesrc-xfixes=disabled -Dximagesrc-xdamage=disabled"

QT5WAYLANDDEPENDS = "${@bb.utils.contains("DISTRO_FEATURES", "wayland", "qtwayland", "", d)}"

PACKAGECONFIG[asm]        = "-Dasm=enabled,-Dasm=disabled,nasm-native"
PACKAGECONFIG[bz2]        = "-Dbz2=enabled,-Dbz2=disabled,bzip2"
PACKAGECONFIG[cairo]      = "-Dcairo=enabled,-Dcairo=disabled,cairo"
PACKAGECONFIG[dv1394]     = "-Ddv1394=enabled,-Ddv1394=disabled,libiec61883 libavc1394 libraw1394"
PACKAGECONFIG[flac]       = "-Dflac=enabled,-Dflac=disabled,flac"
PACKAGECONFIG[gdk-pixbuf] = "-Dgdk-pixbuf=enabled,-Dgdk-pixbuf=disabled,gdk-pixbuf"
PACKAGECONFIG[gtk]        = "-Dgtk3=enabled,-Dgtk3=disabled,gtk+3"
PACKAGECONFIG[gudev]      = "-Dv4l2-gudev=enabled,-Dv4l2-gudev=disabled,libgudev"
PACKAGECONFIG[jack]       = "-Djack=enabled,-Djack=disabled,jack"
PACKAGECONFIG[jpeg]       = "-Djpeg=enabled,-Djpeg=disabled,jpeg"
PACKAGECONFIG[lame]       = "-Dlame=enabled,-Dlame=disabled,lame"
PACKAGECONFIG[libpng]     = "-Dpng=enabled,-Dpng=disabled,libpng"
PACKAGECONFIG[libv4l2]    = "-Dv4l2-libv4l2=enabled,-Dv4l2-libv4l2=disabled,v4l-utils"
PACKAGECONFIG[mpg123]     = "-Dmpg123=enabled,-Dmpg123=disabled,mpg123"
PACKAGECONFIG[pulseaudio] = "-Dpulse=enabled,-Dpulse=disabled,pulseaudio"
PACKAGECONFIG[qt5]        = "-Dqt5=enabled,-Dqt5=disabled,qtbase qtdeclarative qtbase-native qttools-native ${QT5WAYLANDDEPENDS}"
PACKAGECONFIG[soup2]      = "-Dsoup=enabled,,libsoup-2.4,,,soup3"
PACKAGECONFIG[soup3]      = "-Dsoup=enabled,,libsoup,,,soup2"
PACKAGECONFIG[speex]      = "-Dspeex=enabled,-Dspeex=disabled,speex"
PACKAGECONFIG[rpi]        = "-Drpicamsrc=enabled,-Drpicamsrc=disabled,userland"
PACKAGECONFIG[taglib]     = "-Dtaglib=enabled,-Dtaglib=disabled,taglib"
PACKAGECONFIG[v4l2]       = "-Dv4l2=enabled -Dv4l2-probe=true,-Dv4l2=disabled -Dv4l2-probe=false"
PACKAGECONFIG[vpx]        = "-Dvpx=enabled,-Dvpx=disabled,libvpx"
PACKAGECONFIG[wavpack]    = "-Dwavpack=enabled,-Dwavpack=disabled,wavpack"
PACKAGECONFIG[x11]        = "${X11ENABLEOPTS},${X11DISABLEOPTS},${X11DEPENDS}"

EXTRA_OEMESON += " \
    -Ddoc=disabled \
    -Daalib=disabled \
    -Ddirectsound=disabled \
    -Ddv=disabled \
    -Dlibcaca=disabled \
    -Doss=enabled \
    -Doss4=disabled \
    -Dosxaudio=disabled \
    -Dosxvideo=disabled \
    -Dshout2=disabled \
    -Dtwolame=disabled \
    -Dwaveform=disabled \
"

FILES:${PN}-equalizer += "${datadir}/gstreamer-1.0/presets/*.prs"
