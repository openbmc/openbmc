SUMMARY = "JUCE's Projucer"
DESCRIPTION = "Projucer is used to build and generate support files and build infrastructure for all \
JUCE supported platforms, including Linux and Embedded Linux."
SECTION = "utils"
HOMEPAGE = "http://juce.com/"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=eff9da77cdb81a803f8cbca6c16801d8"

LICENSE_FLAGS = "commercial"

inherit pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS = "libx11 libxext libxinerama libxrandr libxcursor freetype alsa-lib curl bzip2 libpng harfbuzz glib-2.0 libpcre"
DEPENDS:append:libc-musl = " libexecinfo"

SRCREV = "2f980209cc4091a4490bb1bafc5d530f16834e58"
BRANCH = "master"
SRC_URI = "git://github.com/WeAreROLI/JUCE.git;protocol=https;branch=${BRANCH} \
           file://0001-StandardHeader-Include-utility-for-GCC-12-compatibil.patch \
           "

S = "${WORKDIR}/git"
PV = "6.1.6"

JUCE_PROJUCER_BUILD_PATH = "${B}/extras/Projucer/Builds"
JUCE_PROJUCER_MAKEFILE_PATH = "${JUCE_PROJUCER_BUILD_PATH}/LinuxMakefile"
JUCE_PROJUCER = "${JUCE_PROJUCER_MAKEFILE_PATH}/build/Projucer"

# export dummy TARGET_ARCH that doesn't do much,
# but disables "-march=native" optimization which does
# not work on cross builds
EXTRA_OEMAKE = "V=1 JUCE_ARCH_LABEL=${TARGET_ARCH} TARGET_ARCH='-g'"

LDFLAGS:append:mipsarcho32 = " -latomic"
LDFLAGS:append:powerpc = " -latomic"
LDFLAGS:append:riscv64 = " -latomic"
LDFLAGS:append:riscv32 = " -latomic"
LDFLAGS:append:armv5 = " -latomic"

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
# error: '_NL_IDENTIFICATION_LANGUAGE' was not declared in this scope
COMPATIBLE_HOST:libc-musl = "null"
