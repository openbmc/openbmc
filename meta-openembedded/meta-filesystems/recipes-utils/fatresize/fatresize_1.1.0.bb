SUMMARY = "Resize FAT partitions using libparted"
SECTION = "console/tools"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "git://salsa.debian.org/parted-team/fatresize.git;protocol=https \
           file://0001-build-Do-not-build-.sgml-file.patch \
          "
SRCREV = "12da22087de2ec43f0fe5af1237389e94619c483"

PV = "1.1.0"

S = "${WORKDIR}/git"

DEPENDS = "parted"

inherit autotools pkgconfig
