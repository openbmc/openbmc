SUMMARY = "Fluidsynth is a software synthesizer"
HOMEPAGE = "http://www.fluidsynth.org/"
SECTION = "libs/multimedia"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fc178bcd425090939a8b634d1d6a9594"

SRC_URI = "git://github.com/FluidSynth/fluidsynth.git;branch=2.1.x"
SRCREV = "6776569abe9a885bcec14141692fa93c0568d51c"
S = "${WORKDIR}/git"
PV = "2.1.5"

inherit cmake pkgconfig lib_package
