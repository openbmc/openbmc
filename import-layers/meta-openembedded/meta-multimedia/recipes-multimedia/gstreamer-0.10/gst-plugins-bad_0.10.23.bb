require gst-plugins.inc

LICENSE = "GPLv2+ & LGPLv2+ & LGPLv2.1+ "
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
                    file://gst/tta/filters.h;beginline=12;endline=29;md5=629b0c7a665d155a6677778f4460ec06 \
                    file://COPYING.LIB;md5=55ca817ccb7d5b5b66355690e9abc605 \
                    file://gst/tta/crc32.h;beginline=12;endline=29;md5=71a904d99ce7ae0c1cf129891b98145c"

DEPENDS += "gst-plugins-base"

PR = "r4"

SRC_URI += "file://buffer-overflow-mp4.patch"

inherit gettext gsettings

EXTRA_OECONF += "--disable-experimental \
                 --disable-sdl --disable-cdaudio --disable-directfb \
                 --disable-vdpau --disable-apexsink"

PACKAGECONFIG ??= "bzip curl \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'rsvg', '', d)}"

PACKAGECONFIG[bzip] = "--enable-bz2,--disable-bz2,bzip2"
PACKAGECONFIG[curl] = "--enable-curl,--disable-curl,curl"
PACKAGECONFIG[rsvg] = "--enable-rsvg,--disable-rsvg,librsvg,"
PACKAGECONFIG[orc] = "--enable-orc,--disable-orc,orc"
PACKAGECONFIG[neon] = "--enable-neon,--disable-neon,neon"
PACKAGECONFIG[mms] = "--enable-libmms,--disable-libmms,libmms"
PACKAGECONFIG[cog] = "--enable-cog,--disable-cog,libpng"
PACKAGECONFIG[faad] = "--enable-faad,--disable-faad,faad2"
PACKAGECONFIG[jp2k] = "--enable-jp2k,--disable-jp2k,jasper"
PACKAGECONFIG[modplug] = "--enable-modplug,--disable-modplug,libmodplug"
PACKAGECONFIG[opus] = "--enable-opus,--disable-opus,libopus"
PACKAGECONFIG[sndfile] = "--enable-sndfile,--disable-sndfile,libsndfile1"
PACKAGECONFIG[vp8] = "--enable-vp8,--disable-vp8,libvpx"
PACKAGECONFIG[ass] = "--enable-assrender,--disable-assrender,libass"
PACKAGECONFIG[openal] = "--enable-openal,--disable-openal,openal-soft"
PACKAGECONFIG[schro] = "--enable-schro,--disable-schro,schroedinger"
PACKAGECONFIG[dc1394] = "--enable-dc1394,--disable-dc1394,libdc1394"
PACKAGECONFIG[faac] = "--enable-faac,--disable-faac,faac"
PACKAGECONFIG[rtmp] = "--enable-rtmp,--disable-rtmp,rtmpdump"
PACKAGECONFIG[voamrwbenc] = "--enable-voamrwbenc,--disable-voamrwbenc,vo-amrwbenc"
PACKAGECONFIG[voaacenc] = "--enable-voaacenc,--disable-voaacenc,vo-aacenc"
PACKAGECONFIG[resindvd] = "--enable-resindvd,--disable-resindvd,libdvdnav libdvdread"

ARM_INSTRUCTION_SET = "arm"

PACKAGES =+ "${PN}-resindvd"
FILES_${PN}-resindvd = "${libdir}/gstreamer-${LIBV}/libresindvd.so"
FILES_${PN}-dev += "${libdir}/gstreamer-${LIBV}/libresindvd.la"
FILES_${PN}-voamrwbenc += "${datadir}/gstreamer-${LIBV}/presets/GstVoAmrwbEnc.prs"

do_configure_prepend() {
	# This m4 file contains nastiness which conflicts with libtool 2.2.2
	rm ${S}/m4/lib-link.m4 || true
}

SRC_URI[md5sum] = "fcb09798114461955260e4d940db5987"
SRC_URI[sha256sum] = "0eae7d1a1357ae8377fded6a1b42e663887beabe0e6cc336e2ef9ada42e11491"
