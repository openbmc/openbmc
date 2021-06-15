DESCRIPTION = "SIMPLE-MTPFS is a FUSE based filsystem for MTP devices connected via USB"
HOMEPAGE = "https://github.com/phatina/simple-mtpfs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=75859989545e37968a99b631ef42722e"

DEPENDS = "fuse libmtp"

inherit autotools pkgconfig

# 0.3.0
SRC_URI = "git://github.com/phatina/simple-mtpfs.git;protocol=https;branch=master"
SRCREV = "c9a691fc52fafaa55d26ac629856153c0514015a"

S = "${WORKDIR}/git"
