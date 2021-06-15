SUMMARY = "libsass command line driver "
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2f8a76980411a3f1f1480b141ce06744"

DEPENDS = "libsass"

inherit autotools pkgconfig

SRC_URI = "git://github.com/sass/sassc.git"
SRCREV = "46748216ba0b60545e814c07846ca10c9fefc5b6"
S = "${WORKDIR}/git"
PV = "3.6.1"

BBCLASSEXTEND = "native"
