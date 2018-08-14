require gst-plugins.inc

LICENSE = "GPLv2+ & LGPLv2.1+ & LGPLv2+"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://gst/synaesthesia/synaescope.h;beginline=1;endline=20;md5=99f301df7b80490c6ff8305fcc712838 \
                    file://tests/check/elements/xingmux.c;beginline=1;endline=21;md5=4c771b8af188724855cb99cadd390068 \
                    file://gst/mpegstream/gstmpegparse.h;beginline=1;endline=18;md5=ff65467b0c53cdfa98d0684c1bc240a9"

DEPENDS += "gst-plugins-base libid3tag libmad mpeg2dec liba52 lame"
PR = "r3"

inherit gettext

EXTRA_OECONF += "--with-plugins=a52dec,lame,id3tag,mad,mpeg2dec,mpegstream,mpegaudioparse,asfdemux,realmedia \
                 --disable-orc"

PACKAGECONFIG ??= ""
PACKAGECONFIG[x264] = "--enable-x264,--disable-x264,x264"
PACKAGECONFIG[cdio] = "--enable-cdio,--disable-cdio,libcdio"
PACKAGECONFIG[dvdread] = "--enable-dvdread,--disable-dvdread,libdvdread"
PACKAGECONFIG[amrnb] = "--enable-amrnb,--disable-amrnb,opencore-amr"
PACKAGECONFIG[amrwb] = "--enable-amrwb,--disable-amrwb,opencore-amr"

do_configure_prepend() {
    # This m4 file contains nastiness which conflicts with libtool 2.2.2
    rm ${S}/m4/lib-link.m4 || true
}

SRC_URI[md5sum] = "1d81c593e22a6cdf0f2b4f57eae93df2"
SRC_URI[sha256sum] = "1ca90059275c0f5dca71d4d1601a8f429b7852baed0723e820703b977e2c8df0"
SRC_URI += "file://0001-cdio-compensate-for-libcdio-s-recent-cd-text-api-cha.patch \
            file://0002-Fix-opencore-include-paths.patch"

FILES_${PN}-amrnb += "${datadir}/gstreamer-0.10/presets/GstAmrnbEnc.prs"
