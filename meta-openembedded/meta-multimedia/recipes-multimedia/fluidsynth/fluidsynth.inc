SUMMARY = "Fluidsynth is a software synthesizer"
HOMEPAGE = "http://www.fluidsynth.org/"
SECTION = "libs/multimedia"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fc178bcd425090939a8b634d1d6a9594"

SRC_URI = "git://github.com/FluidSynth/fluidsynth.git"
SRCREV = "90a6c52e1c347d1c4283b0de9f860ed2fbc413bc"
S = "${WORKDIR}/git"
PV = "2.2.1"

inherit cmake pkgconfig lib_package
