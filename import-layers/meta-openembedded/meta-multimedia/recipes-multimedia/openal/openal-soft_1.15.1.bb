SUMMARY = "OpenAL is a cross-platform 3D audio API"
HOMEPAGE = "http://kcat.strangesoft.net/openal.html"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=facc3a8f452930083bbb95d82b989c35"

inherit cmake

SRC_URI = "http://kcat.strangesoft.net/openal-releases/${BP}.tar.bz2"
SRC_URI[md5sum] = "ea83dec3b9655a27d28e7bc7cae9cd71"
SRC_URI[sha256sum] = "0e29a162f0841ccb4135ce76e92e8a704589b680a85eddf76f898de5236eb056"

PACKAGECONFIG ?= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'alsa', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'pulseaudio', '', d)} \
"
PACKAGECONFIG[alsa] = "-DALSA=TRUE, -DALSA=FALSE, alsa-lib"
PACKAGECONFIG[pulseaudio] = "-DPULSEAUDIO=TRUE, -DPULSEAUDIO=FALSE, pulseaudio"
# currently doesn't work with libav-9
# PKG_CHECK_MODULES(FFMPEG libavcodec>=53.61.100 libavformat>=53.32.100 libavutil>=51.35.100)
# but alffmpeg.c:418:44: error: 'AV_CH_LAYOUT_MONO' undeclared (first use in this function)
PACKAGECONFIG[examples] = "-DEXAMPLES=TRUE, -DEXAMPLES=FALSE, libav"

FILES_${PN} += "${datadir}/openal"
