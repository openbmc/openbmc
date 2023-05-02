SUMMARY = "Fluidsynth is a software synthesizer"
HOMEPAGE = "http://www.fluidsynth.org/"
SECTION = "libs/multimedia"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fc178bcd425090939a8b634d1d6a9594"

SRC_URI = "git://github.com/FluidSynth/fluidsynth.git;branch=master;protocol=https"
SRCREV = "8b00644751578ba67b709a827cbe5133d849d339"
S = "${WORKDIR}/git"
PV = "2.2.6"

inherit cmake pkgconfig lib_package
