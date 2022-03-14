DESCRIPTION = "A utility to manage self encrypting drives that conform \
to the Trusted Computing Group OPAL 2.0 SSC specification."
SUMMARY = "The Drive Trust Alliance Self Encrypting Drive Utility"
HOMEPAGE = "https://github.com/Drive-Trust-Alliance/sedutil"
SECTION = "console/utils"
LICENSE = "GPLv3"

LIC_FILES_CHKSUM = "file://Common/LICENSE.txt;md5=d32239bcb673463ab874e80d47fae504"

BASEPV = "1.15.1"
PV = "1.20.0"
SRCREV = "d3de8e45e06a21d31cca0046ceb16ced1ef3563a"
SRC_URI = "git://github.com/Drive-Trust-Alliance/sedutil.git;branch=master;protocol=https \
           file://0001-Fix-build-on-big-endian-architectures.patch \
           "

S = "${WORKDIR}/git"

inherit autotools-brokensep
