SUMMARY = "Fluidsynth is a software synthesizer"
HOMEPAGE = "http://www.fluidsynth.org/"
SECTION = "libs/multimedia"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fc178bcd425090939a8b634d1d6a9594"

SRC_URI = "git://github.com/FluidSynth/fluidsynth.git"
SRCREV = "9671e412feedb90325e038c3aaf5d543eebc20c0"
S = "${WORKDIR}/git"
PV = "2.0.4"

inherit cmake pkgconfig lib_package
