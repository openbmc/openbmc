SUMMARY = "OpenAL is a cross-platform 3D audio API"
HOMEPAGE = "http://kcat.strangesoft.net/openal.html"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=0f159f19f9377e1895fbb477d5a7953e"

inherit cmake pkgconfig

# openal-soft-1.19.1
SRCREV = "6761218e51699f46bf25c377e65b3e9ea5e434b9"
SRC_URI = "git://github.com/kcat/openal-soft;branch=master;protocol=https \
           file://0001-Use-BUILD_CC-to-compile-native-tools.patch \
           file://0002-makehrtf-Disable-Wstringop-truncation.patch \
           "

S = "${WORKDIR}/git"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'alsa pulseaudio', d)}"
PACKAGECONFIG[alsa] = "-DALSA=TRUE, -DALSA=FALSE, alsa-lib"
PACKAGECONFIG[pulseaudio] = "-DPULSEAUDIO=TRUE, -DPULSEAUDIO=FALSE, pulseaudio"
# currently doesn't work with libav-9
# PKG_CHECK_MODULES(FFMPEG libavcodec>=53.61.100 libavformat>=53.32.100 libavutil>=51.35.100)
# but alffmpeg.c:418:44: error: 'AV_CH_LAYOUT_MONO' undeclared (first use in this function)
PACKAGECONFIG[examples] = "-DEXAMPLES=TRUE, -DEXAMPLES=FALSE, libav"

FILES_${PN} += "${datadir}/openal"
