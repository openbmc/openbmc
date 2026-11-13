SUMMARY = "haveged - A simple entropy daemon"
DESCRIPTION = "The haveged project is an attempt to provide an easy-to-use, unpredictable random number generator based upon an adaptation of the HAVEGE algorithm. Haveged was created to remedy low-entropy conditions in the Linux random device that can occur under some workloads, especially on headless servers."

HOMEPAGE = "https://www.issihosts.com/haveged/index.html"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRCREV = "54d8dd95f90c2af87d1b778f0469fae4cc225aa6"
SRC_URI = "git://github.com/jirka-h/haveged.git;branch=master;protocol=https;tag=v${PV} \
"

UPSTREAM_CHECK_URI = "https://github.com/jirka-h/haveged/releases"

inherit autotools

EXTRA_OECONF = "\
    --enable-nistest=yes \
    --enable-olt=yes \
    --enable-threads=no \
"

MIPS_INSTRUCTION_SET = "mips"

CVE_STATUS[CVE-2026-41054] = "fixed-version: fixed since v1.9.21"
