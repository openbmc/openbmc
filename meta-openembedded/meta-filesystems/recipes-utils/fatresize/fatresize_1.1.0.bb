SUMMARY = "Resize FAT partitions using libparted"
SECTION = "console/tools"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "git://salsa.debian.org/parted-team/fatresize.git;protocol=https;branch=master \
           file://0001-build-Do-not-build-.sgml-file.patch \
           file://0001-configure-Do-not-add-D_FILE_OFFSET_BITS-to-CFLAGS.patch \
          "
SRCREV = "12da22087de2ec43f0fe5af1237389e94619c483"

S = "${WORKDIR}/git"

DEPENDS = "parted"

inherit autotools pkgconfig
