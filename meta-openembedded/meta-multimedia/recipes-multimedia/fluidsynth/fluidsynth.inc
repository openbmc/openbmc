SUMMARY = "Fluidsynth is a software synthesizer"
HOMEPAGE = "http://www.fluidsynth.org/"
SECTION = "libs/multimedia"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fc178bcd425090939a8b634d1d6a9594"

SRC_URI = "git://github.com/FluidSynth/fluidsynth.git;branch=2.0.x"
SRCREV = "c2f4683bf57f61451d14d219fdb25cf9f29cb7fe"
S = "${WORKDIR}/git"
PV = "2.0.9"

inherit cmake pkgconfig lib_package
