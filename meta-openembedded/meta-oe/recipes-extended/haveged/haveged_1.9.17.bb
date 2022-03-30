SUMMARY = "haveged - A simple entropy daemon"
DESCRIPTION = "The haveged project is an attempt to provide an easy-to-use, unpredictable random number generator based upon an adaptation of the HAVEGE algorithm. Haveged was created to remedy low-entropy conditions in the Linux random device that can occur under some workloads, especially on headless servers."

AUTHOR = "Gary Wuertz"
HOMEPAGE = "https://www.issihosts.com/haveged/index.html"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM="file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

# v1.9.17
SRCREV = "1ff76272aba9803501e6f473dd5083f0e09289ac"
SRC_URI = "git://github.com/jirka-h/haveged.git;branch=master;protocol=https \
"
S = "${WORKDIR}/git"

UPSTREAM_CHECK_URI = "https://github.com/jirka-h/haveged/releases"

inherit autotools

EXTRA_OECONF = "\
    --enable-nistest=yes \
    --enable-olt=yes \
    --enable-threads=no \
"

MIPS_INSTRUCTION_SET = "mips"
