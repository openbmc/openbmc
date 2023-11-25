SUMMARY = "OpenAL is a cross-platform 3D audio API"
HOMEPAGE = "http://kcat.strangesoft.net/openal.html"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=0f159f19f9377e1895fbb477d5a7953e"

inherit cmake pkgconfig

DEPENDS = "zlib libsndfile1"

SRCREV = "d3875f333fb6abe2f39d82caca329414871ae53b"
SRC_URI = "git://github.com/kcat/openal-soft.git;protocol=https;branch=master"

S = "${WORKDIR}/git"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'alsa pipewire pulseaudio', d)}"
PACKAGECONFIG[alsa] = "-DALSOFT_BACKEND_ALSA=ON,-DALSOFT_BACKEND_ALSA=OFF,alsa-lib"
PACKAGECONFIG[oss] = "-DALSOFT_BACKEND_OSS=ON,-DALSOFT_BACKEND_OSS=OFF"
PACKAGECONFIG[pulseaudio] = "-DALSOFT_BACKEND_PULSEAUDIO=ON,-DALSOFT_BACKEND_PULSEAUDIO=OFF,pulseaudio"
PACKAGECONFIG[pipewire] = "-DALSOFT_BACKEND_PIPEWIRE=ON,-DALSOFT_BACKEND_PIPEWIRE=OFF,pipewire"
PACKAGECONFIG[examples] = "-DALSOFT_EXAMPLES=ON,-DALSOFT_EXAMPLES=OFF"
PACKAGECONFIG[sdl2] = "-DALSOFT_BACKEND_SDL2=ON,-DALSOFT_BACKEND_SDL2=OFF,libsdl2 ffmpeg"

FILES:${PN} += "${datadir}"
