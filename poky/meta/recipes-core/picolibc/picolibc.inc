SUMMARY = "C Libraries for Smaller Embedded Systems"
HOMEPAGE = "https://keithp.com/picolibc"
DESCRIPTION = "Picolibc is a set of standard C libraries, both libc and libm, designed for smaller embedded systems with limited ROM and RAM. Picolibc includes code from Newlib and AVR Libc."
SECTION = "libs"

# Newlib based code but GPL related bits removed, test/printf-tests.c and test/testcases.c
# are GPLv2 and GeneratePicolibcCrossFile.sh is AGPL3 but not part of the artifacts.
LICENSE = "BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = " \
		file://COPYING.GPL2;md5=59530bdf33659b29e73d4adb9f9f6552 \
		file://COPYING.NEWLIB;md5=08ae03456feb75b81cfdb359e0f1ef85 \
		file://COPYING.picolibc;md5=e50fa9458a40929689861ed472d46bc7 \
		"

BASEVER = "1.8.6"
PV = "${BASEVER}+git"
SRC_URI = "git://github.com/picolibc/picolibc.git;protocol=https;branch=main"
SRCREV = "764ef4e401a8f4c6a86ab723533841f072885a5b"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"
