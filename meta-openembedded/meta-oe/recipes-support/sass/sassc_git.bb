SUMMARY = "libsass command line driver "
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2f8a76980411a3f1f1480b141ce06744"

DEPENDS = "libsass"

inherit autotools pkgconfig

SRC_URI = "git://github.com/sass/sassc.git"
SRCREV = "aa6d5c635ea8faf44d542a23aaf85d27e5777d48"
S = "${WORKDIR}/git"
PV = "3.5.0"

BBCLASSEXTEND = "native"
