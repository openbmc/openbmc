SUMMARY = "JUCE's Projucer"
DESCRIPTION = "Projucer is used to build and generate support files and build infrastructure for all \
JUCE supported platforms, including Linux and Embedded Linux."
SECTION = "utils"
HOMEPAGE = "http://juce.com/"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://README.md;md5=65c50b4ff3522b99436da100536ccd1c"

inherit pkgconfig

DEPENDS = "libx11 libxext libxinerama libxrandr libxcursor freetype alsa-lib curl"

SRCREV = "4f41f28b47d01b939559123d145b4e5860528bb7"
BRANCH = "master"
SRC_URI = "git://github.com/WeAreROLI/JUCE.git;protocol=https;branch=${BRANCH}"

S = "${WORKDIR}/git"
PV = "5.0.1"

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
