SUMMARY = "Fixed-point decoder"
DESCRIPTION = "tremor is a fixed point implementation of the vorbis codec."
SECTION = "libs"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=db1b7a668b2a6f47b2af88fb008ad555 \
                    file://os.h;beginline=3;endline=14;md5=5c0af5e1bedef3ce8178c89f48cd6f1f"
DEPENDS = "libogg"
SRCDATE = "${PV}"
PR = "r1"

# SVN support for upstream version check isn't implemented yet
RECIPE_UPSTREAM_VERSION = "20150107"
RECIPE_UPSTREAM_DATE = "Jan 07, 2015"
CHECK_DATE = "Aug 12, 2015"

SRC_URI = "svn://svn.xiph.org/trunk;module=Tremor;rev=19427;protocol=http \
           file://obsolete_automake_macros.patch;striplevel=0 \
           file://tremor-arm-thumb2.patch \
"

S = "${WORKDIR}/Tremor"

inherit autotools pkgconfig

EXTRA_OECONF = "--enable-shared"

ARM_INSTRUCTION_SET = "arm"
