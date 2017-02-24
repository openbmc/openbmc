SUMMARY = "JUCE's Projucer"
DESCRIPTION = "Projucer is used to build and generate support files and build infrastructure for all \
JUCE supported platforms, including Linux and Embedded Linux."
SECTION = "utils"
HOMEPAGE = "http://juce.com/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://README.txt;md5=9ab765ccda8890efc753f287911a1958"

inherit pkgconfig

DEPENDS = "libx11 libxext libxinerama libxrandr libxcursor freetype alsa-lib curl"

SRCREV = "a8a7fa28e2e9cb19db6a27fcccb567a8ccfe6109"
BRANCH = "develop"
SRC_URI = "git://github.com/julianstorer/JUCE.git;protocol=https;branch=${BRANCH}"

S = "${WORKDIR}/git"
PV = "4.2.3+git${SRCPV}"

JUCE_PROJUCER_BUILD_PATH = "${B}/extras/Projucer/Builds"
JUCE_PROJUCER_MAKEFILE_PATH = "${JUCE_PROJUCER_BUILD_PATH}/LinuxMakefile"
JUCE_PROJUCER = "${JUCE_PROJUCER_MAKEFILE_PATH}/build/Projucer"

do_configure() {
  cd ${JUCE_PROJUCER_MAKEFILE_PATH}
  CONFIG=Release oe_runmake clean
}

do_compile() {
  cd ${JUCE_PROJUCER_MAKEFILE_PATH}
  CONFIG=Release oe_runmake
}

do_install() {
  install -d ${D}${bindir}
  install -m 0755 ${JUCE_PROJUCER} ${D}${bindir}
}
