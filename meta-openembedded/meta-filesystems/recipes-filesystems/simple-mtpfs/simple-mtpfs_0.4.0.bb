DESCRIPTION = "SIMPLE-MTPFS is a FUSE based filsystem for MTP devices connected via USB"
HOMEPAGE = "https://github.com/phatina/simple-mtpfs"
BUGTRACKER = "19e7bb9b608b0c0dce2ee6f56fac75901bc69529"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=75859989545e37968a99b631ef42722e"

DEPENDS = "fuse libmtp autoconf-archive"

inherit autotools pkgconfig

SRC_URI = "git://github.com/phatina/simple-mtpfs.git;protocol=https;branch=master"
SRCREV = "19e7bb9b608b0c0dce2ee6f56fac75901bc69529"

S = "${WORKDIR}/git"
