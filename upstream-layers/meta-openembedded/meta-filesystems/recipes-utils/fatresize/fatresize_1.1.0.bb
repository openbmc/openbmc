SUMMARY = "Resize FAT partitions using libparted"
SECTION = "console/tools"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

PV = "1.1.0-3"

SRC_URI = "git://salsa.debian.org/parted-team/fatresize.git;protocol=https;branch=master;tag=debian/${PV} \
           file://0001-build-Do-not-build-.sgml-file.patch \
          "
SRCREV = "7494fba0f14f706c17b9d89ae273efd113c944ca"

DEPENDS = "parted"

inherit autotools pkgconfig
