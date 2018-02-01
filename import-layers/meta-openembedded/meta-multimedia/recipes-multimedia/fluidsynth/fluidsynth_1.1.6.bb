SUMMARY = "Fluidsynth is a software synthesizer"
HOMEPAGE = "http://www.fluidsynth.org/"
SECTION = "libs/multimedia"
LICENSE = "LGPL-2.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=e198e9aac94943d0ec29a7dae8c29416"

DEPENDS = "alsa-lib ncurses glib-2.0"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}/${BP}/${BP}.tar.gz"
SRC_URI[md5sum] = "ae5aca6de824b4173667cbd3a310b263"
SRC_URI[sha256sum] = "50853391d9ebeda9b4db787efb23f98b1e26b7296dd2bb5d0d96b5bccee2171c"

inherit autotools-brokensep pkgconfig lib_package

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'pulseaudio', d)}"
PACKAGECONFIG[sndfile] = "--enable-libsndfile-support,--disable-libsndfile-support,libsndfile1"
PACKAGECONFIG[jack] = "--enable-jack-support,--disable-jack-support,jack"
PACKAGECONFIG[pulseaudio] = "--enable-pulse-support,--disable-pulse-support,pulseaudio"
PACKAGECONFIG[portaudio] = "--enable-portaudio-support,--disable-portaudio-support,portaudio-v19"

do_configure_prepend () {
    rm -f ${S}/m4/*
}
