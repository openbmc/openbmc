DESCRIPTION = "Portable Bandwidth Monitor and rate estimator"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
# only works with libnl-0.5.0
DEPENDS = "libnl libconfuse ncurses"

SRCREV = "1b3f11bde315e221474f7d066ce4efb4ff4d39e3"
SRC_URI = "git://github.com/tgraf/bmon.git;branch=master;protocol=https"

inherit autotools pkgconfig

S = "${WORKDIR}/git"
