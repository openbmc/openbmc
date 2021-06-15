SUMMARY = "Resize FAT partitions using libparted"
SECTION = "console/tools"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "git://salsa.debian.org/parted-team/fatresize.git;protocol=https"
SRCREV = "3f80afc76ad82d4a1b852a6c8dea24cd9f5e7a24"

PV = "1.0.2-11"

S = "${WORKDIR}/git"

DEPENDS = "parted"

inherit autotools pkgconfig
