SUMMARY = "Fluidsynth is a software synthesizer"
HOMEPAGE = "http://www.fluidsynth.org/"
SECTION = "libs/multimedia"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fc178bcd425090939a8b634d1d6a9594"

DEPENDS = "glib-2.0"

SRC_URI = " \
    git://github.com/FluidSynth/fluidsynth.git;branch=master;protocol=https \
    file://0002-fluid_synth_nwrite_float-Allow-zero-pointer-for-left.patch \
    file://0003-Use-ARM-NEON-accelaration-for-float-multithreaded-se.patch \
"
SRCREV = "5ecdc4568e45123216c6888892caad07918ef127"

S = "${WORKDIR}/git"

inherit cmake pkgconfig lib_package

EXTRA_OECMAKE = "-Denable-floats=ON -DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')}"

do_install:append() {
    sed -i -e 's|${STAGING_LIBDIR}|${libdir}|g' ${D}${libdir}/pkgconfig/fluidsynth.pc
    sed -i -e 's|${STAGING_LIBDIR}|${libdir}|g' ${D}${libdir}/cmake/fluidsynth/FluidSynthTargets.cmake
}

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'pulseaudio pipewire systemd alsa dbus', d)}"
PACKAGECONFIG[alsa] = "-Denable-alsa=ON,-Denable-alsa=OFF,alsa-lib"
PACKAGECONFIG[dbus] = "-Denable-dbus=ON,-Denable-dbus=OFF,dbus"
PACKAGECONFIG[jack] = "-Denable-jack=ON,-Denable-jack=OFF,jack"
PACKAGECONFIG[oss] = "-Denable-oss=ON,-Denable-oss=OFF"
PACKAGECONFIG[pipewire] = "-Denable-pipewire=ON,-Denable-pipewire=OFF,pipewire"
PACKAGECONFIG[portaudio] = "-Denable-portaudio=ON,-Denable-portaudio=OFF,portaudio-v19"
PACKAGECONFIG[profiling] = "-Denable-profiling=ON,-Denable-profiling=OFF"
PACKAGECONFIG[pulseaudio] = "-Denable-pulseaudio=ON,-Denable-pulseaudio=OFF,pulseaudio"
PACKAGECONFIG[readline] = "-Denable-readline=ON,-Denable-readline=OFF,readline"
PACKAGECONFIG[sdl] = "-Denable-sdl2=ON,-Denable-sdl2=OFF,libsdl2"
PACKAGECONFIG[sndfile] = "-Denable-libsndfile=ON,-Denable-libsndfile=OFF,libsndfile1"
PACKAGECONFIG[systemd] = "-Denable-systemd=ON,-Denable-systemd=OFF,systemd"
