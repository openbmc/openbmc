SUMMARY = "Fixed-point decoder"
DESCRIPTION = "tremor is a fixed point implementation of the vorbis codec."
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=db1b7a668b2a6f47b2af88fb008ad555 \
                    file://os.h;beginline=3;endline=14;md5=5c0af5e1bedef3ce8178c89f48cd6f1f"
DEPENDS = "libogg"

SRC_URI = "git://gitlab.xiph.org/xiph/tremor.git;protocol=https \
           file://obsolete_automake_macros.patch;striplevel=0 \
           file://tremor-arm-thumb2.patch \
"
SRCREV = "7c30a66346199f3f09017a09567c6c8a3a0eedc8"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OECONF = "--enable-shared"

ARM_INSTRUCTION_SET = "arm"
