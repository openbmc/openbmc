SUMMARY = "Fluidsynth is a software synthesizer"
HOMEPAGE = "http://www.fluidsynth.org/"
SECTION = "libs/multimedia"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fc178bcd425090939a8b634d1d6a9594"

SRC_URI = "git://github.com/FluidSynth/fluidsynth.git"
SRCREV = "d9ad6a0725840388a48f45e38eda903a65ec1cf5"
S = "${WORKDIR}/git"
PV = "2.1.2"

inherit cmake pkgconfig lib_package
