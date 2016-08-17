DESCRIPTION = "SIMPLE-MTPFS is a FUSE based filsystem for MTP devices connected via USB"
HOMEPAGE = "https://github.com/phatina/simple-mtpfs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=75859989545e37968a99b631ef42722e"

DEPENDS = "fuse libmtp"

inherit autotools pkgconfig

SRC_URI = "git://github.com/phatina/simple-mtpfs.git;protocol=https;branch=master"
SRCREV = "a7ab64c7e4d7aca155cbc7ce9412aaf68ef6e404"

S = "${WORKDIR}/git"
