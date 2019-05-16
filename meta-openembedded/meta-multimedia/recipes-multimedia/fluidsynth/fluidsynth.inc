SUMMARY = "Fluidsynth is a software synthesizer"
HOMEPAGE = "http://www.fluidsynth.org/"
SECTION = "libs/multimedia"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fc178bcd425090939a8b634d1d6a9594"

SRC_URI = "git://github.com/FluidSynth/fluidsynth.git"
SRCREV = "7f11a9bf5c7304e04309a6ec9fc515ee815524bf"
S = "${WORKDIR}/git"
PV = "2.0.5"

inherit cmake pkgconfig lib_package
