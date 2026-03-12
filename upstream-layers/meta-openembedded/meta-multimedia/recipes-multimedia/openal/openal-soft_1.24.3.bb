SUMMARY = "OpenAL is a cross-platform 3D audio API"
HOMEPAGE = "http://kcat.strangesoft.net/openal.html"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=0f159f19f9377e1895fbb477d5a7953e"

inherit cmake pkgconfig

DEPENDS = "zlib libsndfile1"

SRCREV = "dc7d7054a5b4f3bec1dc23a42fd616a0847af948"
SRC_URI = "git://github.com/kcat/openal-soft.git;protocol=https;branch=master \
           file://0001-Add-missing-include-for-malloc-free.patch \
          "

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'alsa pipewire pulseaudio', d)}"
PACKAGECONFIG[alsa] = "-DALSOFT_BACKEND_ALSA=ON,-DALSOFT_BACKEND_ALSA=OFF,alsa-lib"
PACKAGECONFIG[oss] = "-DALSOFT_BACKEND_OSS=ON,-DALSOFT_BACKEND_OSS=OFF"
PACKAGECONFIG[pulseaudio] = "-DALSOFT_BACKEND_PULSEAUDIO=ON,-DALSOFT_BACKEND_PULSEAUDIO=OFF,pulseaudio"
PACKAGECONFIG[pipewire] = "-DALSOFT_BACKEND_PIPEWIRE=ON,-DALSOFT_BACKEND_PIPEWIRE=OFF,pipewire"
PACKAGECONFIG[examples] = "-DALSOFT_EXAMPLES=ON,-DALSOFT_EXAMPLES=OFF"
PACKAGECONFIG[sdl2] = "-DALSOFT_BACKEND_SDL2=ON,-DALSOFT_BACKEND_SDL2=OFF,virtual/libsdl2 ffmpeg"

FILES:${PN} += "${datadir}"
