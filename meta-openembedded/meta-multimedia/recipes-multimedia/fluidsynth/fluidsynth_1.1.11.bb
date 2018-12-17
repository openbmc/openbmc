SUMMARY = "Fluidsynth is a software synthesizer"
HOMEPAGE = "http://www.fluidsynth.org/"
SECTION = "libs/multimedia"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fc178bcd425090939a8b634d1d6a9594"

DEPENDS = "alsa-lib ncurses glib-2.0"

SRC_URI = " \
    git://github.com/FluidSynth/fluidsynth.git;branch=1.1.x \
    file://0001-Use-ARM-NEON-accelaration-for-float-multithreaded-se.patch \
"
SRCREV = "f65c6ba25fb2c7e37c89fc6a4afc5aa645e208c2"
S = "${WORKDIR}/git"

inherit cmake pkgconfig lib_package

EXTRA_OECMAKE = "-Denable-floats=ON -DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'pulseaudio', d)}"
PACKAGECONFIG[sndfile] = "-Denable-libsndfile-support=ON,-Denable-libsndfile-support=OFF,libsndfile1"
PACKAGECONFIG[jack] = "-Denable-jack-support=ON,-Denable-jack-support=OFF,jack"
PACKAGECONFIG[pulseaudio] = "-Denable-pulseaudio=ON,-Denable-pulseaudio=OFF,pulseaudio"
PACKAGECONFIG[portaudio] = "-Denable-portaudio=ON,-Denable-portaudio=OFF,portaudio-v19"
