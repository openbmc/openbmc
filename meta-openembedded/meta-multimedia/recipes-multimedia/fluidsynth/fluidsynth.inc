SUMMARY = "Fluidsynth is a software synthesizer"
HOMEPAGE = "http://www.fluidsynth.org/"
SECTION = "libs/multimedia"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fc178bcd425090939a8b634d1d6a9594"

SRC_URI = "git://github.com/FluidSynth/fluidsynth.git;branch=2.1.x"
SRCREV = "fd9bf6a6b0d10a984d2eb0e0e549700b7aa590ec"
S = "${WORKDIR}/git"
PV = "2.1.6"

inherit cmake pkgconfig lib_package
