SUMMARY = "MCPP is a portable C/C++ preprocessor"
HOMEPAGE = "http://mcpp.sourceforge.net/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5ca370b75ec890321888a00cea9bc1d5"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz \
           file://ice-mcpp.patch \
           file://0001-configure-Fix-checks-for-system-headers.patch \
           file://CVE-2019-14274.patch"
SRC_URI[md5sum] = "512de48c87ab023a69250edc7a0c7b05"
SRC_URI[sha256sum] = "3b9b4421888519876c4fc68ade324a3bbd81ceeb7092ecdbbc2055099fcb8864"

inherit autotools

EXTRA_OECONF = " --enable-mcpplib "

BBCLASSEXTEND = "native nativesdk"

