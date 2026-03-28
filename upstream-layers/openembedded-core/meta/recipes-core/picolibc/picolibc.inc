SUMMARY = "C Libraries for Smaller Embedded Systems"
HOMEPAGE = "https://keithp.com/picolibc"
DESCRIPTION = "Picolibc is a set of standard C libraries, both libc and libm, designed for smaller embedded systems with limited ROM and RAM. Picolibc includes code from Newlib and AVR Libc."
SECTION = "libs"

# Newlib based code but GPL related bits removed, test/printf-tests.c and test/testcases.c
# are GPLv2 and GeneratePicolibcCrossFile.sh is AGPL3 but not part of the artifacts.
LICENSE = "BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = " \
		file://COPYING.picolibc;md5=e44132dded508048efb22b15ccd40d7d \
		"

BASEVER = "1.8.11"
PV = "${BASEVER}+git"
SRC_URI = "git://github.com/picolibc/picolibc.git;protocol=https;branch=main"
SRCREV = "f7edb0869e64b9d687f46961adeef7154921fe92"

B = "${WORKDIR}/build"
