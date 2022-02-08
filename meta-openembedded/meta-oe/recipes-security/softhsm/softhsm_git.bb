SUMMARY = "PKCS#11 HSM/Token Emulator"
HOMEPAGE = "https://www.opendnssec.org/softhsm/"
LICENSE = "BSD-2-Clause & ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ef3f77a3507c3d91e75b9f2bdaee4210"
DEPENDS = "openssl"
PV = "2.5.0"

SRC_URI = "git://github.com/opendnssec/SoftHSMv2.git;branch=master;protocol=https"
SRCREV = "369df0383d101bc8952692c2a368ac8bc887d1b4"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

# EdDSA requires OpenSSL >= 1.1.1
EXTRA_OECONF = "--enable-eddsa --disable-gost"

BBCLASSEXTEND = "native"
