SUMMARY = "Fluidsynth is a software synthesizer"
HOMEPAGE = "http://www.fluidsynth.org/"
SECTION = "libs/multimedia"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4bf661c1e3793e55c8d1051bc5e0ae21"

DEPENDS = "glib-2.0"

SRC_URI = " \
    git://github.com/FluidSynth/fluidsynth.git;name=fluidsynth;branch=master;protocol=https \
    git://github.com/kthohr/gcem.git;name=gcem;subdir=${S}/gcem;protocol=https;nobranch=1 \
    git://github.com/Signalsmith-Audio/basics.git;name=signalsmith-audio-basics;subdir=${S}/signalsmith-audio-basics;protocol=https;nobranch=1 \
    file://0003-Use-ARM-NEON-accelaration-for-float-multithreaded-se.patch \
"
SRCREV_FORMAT = "fluidsynth"
SRCREV_fluidsynth = "6b8fabbd60f0df3b6e2f5b5df8478a1b43315acd"
SRCREV_gcem = "012ae73c6d0a2cb09ffe86475f5c6fba3926e200"
SRCREV_signalsmith-audio-basics = "012d2be17b0eb6839628f8c73687c4ccccc1bb01"

inherit cmake pkgconfig lib_package

EXTRA_OECMAKE = "-Denable-floats=ON -DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')} -DGCEM_INCLUDE_DIR=${S}/gcem/include"

do_install:append() {
    sed -i -e 's|${STAGING_LIBDIR}|${libdir}|g' ${D}${libdir}/pkgconfig/fluidsynth.pc
    sed -i -e 's|${STAGING_LIBDIR}|${libdir}|g' ${D}${libdir}/cmake/fluidsynth/FluidSynth-shared-targets.cmake
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
PACKAGECONFIG[sdl] = "-Denable-sdl3=ON,-Denable-sdl3=OFF,libsdl3"
PACKAGECONFIG[sndfile] = "-Denable-libsndfile=ON,-Denable-libsndfile=OFF,libsndfile1"
PACKAGECONFIG[systemd] = "-Denable-systemd=ON,-Denable-systemd=OFF,systemd"

