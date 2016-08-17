require gstreamer1.0-plugins.inc

LICENSE = "GPLv2+ & LGPLv2+ & LGPLv2.1+"

DEPENDS += "gstreamer1.0-plugins-base libpng jpeg"

inherit gettext bluetooth

SRC_URI_append = " \
    file://0001-Makefile.am-don-t-hardcode-libtool-name-when-running.patch \
"

# opengl packageconfig factored out to make it easy for distros
# and BSP layers to pick either (desktop) opengl, gles2, or no GL
PACKAGECONFIG_GL ?= "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'gles2', '', d)}"

# gtk is not in the PACKAGECONFIG variable by default until
# the transition to gtk+3 is finished
PACKAGECONFIG ??= " \
    ${GSTREAMER_ORC} \
    ${PACKAGECONFIG_GL} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'directfb', 'directfb', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} \
    bz2 curl dash dtls hls neon rsvg sbc smoothstreaming sndfile uvch264 webp \
"

PACKAGECONFIG[assrender]       = "--enable-assrender,--disable-assrender,libass"
PACKAGECONFIG[bluez]           = "--enable-bluez,--disable-bluez,${BLUEZ}"
PACKAGECONFIG[bz2]             = "--enable-bz2,--disable-bz2,bzip2"
PACKAGECONFIG[curl]            = "--enable-curl,--disable-curl,curl"
PACKAGECONFIG[dash]            = "--enable-dash,--disable-dash,libxml2"
PACKAGECONFIG[directfb]        = "--enable-directfb,--disable-directfb,directfb"
PACKAGECONFIG[dtls]            = "--enable-dtls,--disable-dtls,openssl"
PACKAGECONFIG[faac]            = "--enable-faac,--disable-faac,faac"
PACKAGECONFIG[faad]            = "--enable-faad,--disable-faad,faad2"
PACKAGECONFIG[flite]           = "--enable-flite,--disable-flite,flite-alsa"
PACKAGECONFIG[fluidsynth]      = "--enable-fluidsynth,--disable-fluidsynth,fluidsynth"
PACKAGECONFIG[gles2]           = "--enable-gles2,--disable-gles2,virtual/libgles2"
PACKAGECONFIG[gtk]             = "--enable-gtk3,--disable-gtk3,gtk+3"
PACKAGECONFIG[hls]             = "--enable-hls,--disable-hls,nettle"
PACKAGECONFIG[libmms]          = "--enable-libmms,--disable-libmms,libmms"
PACKAGECONFIG[libssh2]         = "--enable-libssh2,--disable-libssh2,libssh2"
PACKAGECONFIG[modplug]         = "--enable-modplug,--disable-modplug,libmodplug"
PACKAGECONFIG[neon]            = "--enable-neon,--disable-neon,neon"
PACKAGECONFIG[openal]          = "--enable-openal,--disable-openal,openal-soft"
PACKAGECONFIG[opencv]          = "--enable-opencv,--disable-opencv,opencv"
PACKAGECONFIG[opengl]          = "--enable-opengl,--disable-opengl,virtual/libgl libglu"
PACKAGECONFIG[opus]            = "--enable-opus,--disable-opus,libopus"
PACKAGECONFIG[resindvd]        = "--enable-resindvd,--disable-resindvd,libdvdread libdvdnav"
PACKAGECONFIG[rsvg]            = "--enable-rsvg,--disable-rsvg,librsvg"
PACKAGECONFIG[rtmp]            = "--enable-rtmp,--disable-rtmp,rtmpdump"
PACKAGECONFIG[sbc]             = "--enable-sbc,--disable-sbc,sbc"
PACKAGECONFIG[schroedinger]    = "--enable-schro,--disable-schro,schroedinger"
PACKAGECONFIG[smoothstreaming] = "--enable-smoothstreaming,--disable-smoothstreaming,libxml2"
PACKAGECONFIG[sndfile]         = "--enable-sndfile,--disable-sndfile,libsndfile1"
PACKAGECONFIG[srtp]            = "--enable-srtp,--disable-srtp,libsrtp"
PACKAGECONFIG[uvch264]         = "--enable-uvch264,--disable-uvch264,libusb1 libgudev"
PACKAGECONFIG[voaacenc]        = "--enable-voaacenc,--disable-voaacenc,vo-aacenc"
PACKAGECONFIG[voamrwbenc]      = "--enable-voamrwbenc,--disable-voamrwbenc,vo-amrwbenc"
PACKAGECONFIG[wayland]         = "--enable-wayland --enable-egl,--disable-wayland --disable-egl,wayland virtual/egl"
PACKAGECONFIG[webp]            = "--enable-webp,--disable-webp,libwebp"

# these plugins have not been ported to 1.0 (yet):
#   apexsink dc1394 lv2 linsys musepack nas timidity teletextdec sdl xvid wininet
#   acm gsettings sndio cdxaparse dccp faceoverlay hdvparse tta mve nuvdemux
#   patchdetect real sdi videomeasure gsettings

# these plugins have no corresponding library in OE-core or meta-openembedded:
#   openni2 winks direct3d directsound winscreencap
#   apple_media android_media avc bs2b chromaprint daala dts gme gsm kate ladspa
#   libde265 mimic mpeg2enc mplex ofa openh264 opensles pvr soundtouch spandsp
#   spc vdpau wasapi x265 zbar

# qt5 support is disabled, because it is not present in OE core, and requires more work than
# just adding a packageconfig (it requires access to moc, uic, rcc, and qmake paths).
# This is better done in a separate qt5 layer (which then should add a "qt5" packageconfig
# in a gstreamer1.0-plugins-bad bbappend).

EXTRA_OECONF += " \
    --enable-decklink \
    --enable-dvb \
    --enable-fbdev \
    --enable-shm \
    --enable-vcd \
    --disable-acm \
    --disable-android_media \
    --disable-apexsink \
    --disable-apple_media \
    --disable-avc \
    --disable-bs2b \
    --disable-chromaprint \
    --disable-cocoa \
    --disable-daala \
    --disable-dc1394 \
    --disable-direct3d \
    --disable-directsound \
    --disable-dts \
    --disable-gme \
    --disable-gsm \
    --disable-kate \
    --disable-ladspa \
    --disable-libde265 \
    --disable-libvisual \
    --disable-linsys \
    --disable-lv2 \
    --disable-mimic \
    --disable-mpeg2enc \
    --disable-mplex \
    --disable-musepack \
    --disable-nas \
    --disable-ofa \
    --disable-openexr \
    --disable-openh264 \
    --disable-openjpeg \
    --disable-openni2 \
    --disable-opensles \
    --disable-pvr \
    --disable-qt \
    --disable-sdl \
    --disable-sdltest \
    --disable-sndio \
    --disable-soundtouch \
    --disable-spandsp \
    --disable-spc \
    --disable-teletextdec \
    --disable-timidity \
    --disable-vdpau \
    --disable-wasapi \
    --disable-wildmidi \
    --disable-wininet \
    --disable-winks \
    --disable-winscreencap \
    --disable-x265 \
    --disable-xvid \
    --disable-zbar \
    ${@bb.utils.contains("TUNE_FEATURES", "mx32", "--disable-yadif", "", d)} \
"

export OPENCV_PREFIX = "${STAGING_DIR_TARGET}${prefix}"

ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"

FILES_${PN}-dev += "${libdir}/gstreamer-${LIBV}/include/gst/gl/gstglconfig.h"
FILES_${PN}-freeverb += "${datadir}/gstreamer-${LIBV}/presets/GstFreeverb.prs"
FILES_${PN}-opencv += "${datadir}/gst-plugins-bad/${LIBV}/opencv*"
FILES_${PN}-voamrwbenc += "${datadir}/gstreamer-${LIBV}/presets/GstVoAmrwbEnc.prs"
