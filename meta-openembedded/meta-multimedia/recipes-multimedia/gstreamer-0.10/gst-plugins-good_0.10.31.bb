require gst-plugins.inc

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://common/coverage/coverage-report.pl;beginline=2;endline=17;md5=622921ffad8cb18ab906c56052788a3f \
                    file://gst/replaygain/rganalysis.c;beginline=1;endline=23;md5=b60ebefd5b2f5a8e0cab6bfee391a5fe"

PR = "r8"

PACKAGECONFIG ?= "jpeg v4l \
    ${@bb.utils.filter('DISTRO_FEATURES', 'pulseaudio x11', d)} \
"
PACKAGECONFIG[pulseaudio] = "--enable-pulse,--disable-pulse,pulseaudio"
PACKAGECONFIG[jack] = "--enable-jack,--disable-jack,jack"
PACKAGECONFIG[jpeg] = "--enable-jpeg,--disable-jpeg,jpeg"
PACKAGECONFIG[wavpack] = "--enable-wavpack,--disable-wavpack,wavpack"
PACKAGECONFIG[gdkpixbuf] = "--enable-gdk_pixbuf,--disable-gdk_pixbuf,gdk-pixbuf"
PACKAGECONFIG[v4l] = "--enable-gst_v4l2 --with-gudev,--disable-gst_v4l2 --without-gudev,libgudev"
# sub-feature of v4l, but control separately since libv4l is not part of oe-core
PACKAGECONFIG[libv4l] = "--with-libv4l2,--without-libv4l2,libv4l"
PACKAGECONFIG[bzip2] = "--enable-bz2,--disable-bz2,bzip2"
PACKAGECONFIG[orc] = "--enable-orc,--disable-orc,orc"
PACKAGECONFIG[x11] = "--enable-x,--disable-x,virtual/libx11 libxfixes libxdamage"
PACKAGECONFIG[dv1394] = "--enable-dv1394,--disable-dv1394,libraw1394 libiec61883 libavc1394"

DEPENDS += "gst-plugins-base gconf cairo libpng zlib libid3tag flac \
            speex libsoup-2.4 libcap"

inherit gettext gconf

SRC_URI += "file://0001-v4l2-fix-build-with-recent-kernels-the-v4l2_buffer-i.patch \
            file://0001-v4l2_calls-define-V4L2_CID_HCENTER-and-V4L2_CID_VCEN.patch \
            file://0407-mulawdec-fix-integer-overrun.patch \
"
EXTRA_OECONF += "--disable-aalib --disable-esd --disable-shout2 --disable-libcaca --disable-hal \
                 --disable-examples --disable-taglib"

do_configure_prepend() {
    # This m4 file contains nastiness which conflicts with libtool 2.2.2
    rm ${S}/m4/lib-link.m4 || true
}

SRC_URI[md5sum] = "24f98a294a2b521e1b29412bdadae2e6"
SRC_URI[sha256sum] = "7e27840e40a7932ef2dc032d7201f9f41afcaf0b437daf5d1d44dc96d9e35ac6"

FILES_${PN}-gconfelements += "${sysconfdir}/gconf/schemas/gstreamer-0.10.schemas"
FILES_${PN}-equalizer += "${datadir}/gstreamer-0.10/presets/*.prs"
