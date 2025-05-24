DESCRIPTION = "A utility to manage self encrypting drives that conform \
to the Trusted Computing Group OPAL 2.0 SSC specification."
SUMMARY = "The Drive Trust Alliance Self Encrypting Drive Utility"
HOMEPAGE = "https://github.com/Drive-Trust-Alliance/sedutil"
SECTION = "console/utils"
LICENSE = "GPL-3.0-only"

LIC_FILES_CHKSUM = "file://Common/LICENSE.txt;md5=d32239bcb673463ab874e80d47fae504"

PV = "1.20.0+git"

SRCREV = "6270fb0e3c110572d96936a473b5e0d21a39884d"
SRC_URI = "git://github.com/Drive-Trust-Alliance/sedutil.git;branch=master;protocol=https \
           file://0001-Fix-build-on-big-endian-architectures.patch \
           "

S = "${WORKDIR}/git"

inherit autotools-brokensep
