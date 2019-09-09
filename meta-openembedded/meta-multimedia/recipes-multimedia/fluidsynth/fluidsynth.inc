SUMMARY = "Fluidsynth is a software synthesizer"
HOMEPAGE = "http://www.fluidsynth.org/"
SECTION = "libs/multimedia"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fc178bcd425090939a8b634d1d6a9594"

SRC_URI = "git://github.com/FluidSynth/fluidsynth.git"
SRCREV = "f78486a50bd785200412ea16434b38edc4a45a6c"
S = "${WORKDIR}/git"
PV = "2.0.6"

inherit cmake pkgconfig lib_package
