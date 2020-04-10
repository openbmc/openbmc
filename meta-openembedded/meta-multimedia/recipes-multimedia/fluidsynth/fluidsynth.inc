SUMMARY = "Fluidsynth is a software synthesizer"
HOMEPAGE = "http://www.fluidsynth.org/"
SECTION = "libs/multimedia"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fc178bcd425090939a8b634d1d6a9594"

SRC_URI = "git://github.com/FluidSynth/fluidsynth.git"
SRCREV = "37c9ae2bf431a764032f023b3b2c0c0b86b7c272"
S = "${WORKDIR}/git"
PV = "2.1.0"

inherit cmake pkgconfig lib_package
