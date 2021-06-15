SUMMARY = "PKCS#11 HSM/Token Emulator"
HOMEPAGE = "https://www.opendnssec.org/softhsm/"
LICENSE = "BSD-2-Clause & ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ef3f77a3507c3d91e75b9f2bdaee4210"

DEPENDS = "sqlite3"

SRC_URI = "https://dist.opendnssec.org/source/softhsm-2.6.1.tar.gz"
SRC_URI[sha256sum] = "61249473054bcd1811519ef9a989a880a7bdcc36d317c9c25457fc614df475f2"

inherit autotools pkgconfig siteinfo

EXTRA_OECONF += " --with-sqlite3=${STAGING_DIR_HOST}/usr"
EXTRA_OECONF += "${@oe.utils.conditional('SITEINFO_BITS', '64', ' --enable-64bit', '', d)}"

PACKAGECONFIG ?= "ecc eddsa pk11 openssl"

PACKAGECONFIG[npm] = ",--disable-non-paged-memory"
PACKAGECONFIG[ecc] = "--enable-ecc,--disable-ecc"
PACKAGECONFIG[gost] = "--enable-gost,--disable-gost"
PACKAGECONFIG[eddsa] = "--enable-eddsa, --disable-eddsa"
PACKAGECONFIG[fips] = "--enable-fips, --disable-fips"
PACKAGECONFIG[notvisable] = "--disable-visibility"
PACKAGECONFIG[openssl] = "--with-openssl=${STAGING_DIR_HOST}/usr --with-crypto-backend=openssl, --without-openssl, openssl, openssl"
PACKAGECONFIG[botan] = "--with-botan=${STAGING_DIR_HOST}/usr --with-crypto-backend=botan, --without-botan, botan"
PACKAGECONFIG[migrate] = "--with-migrate"
PACKAGECONFIG[pk11] = "--enable-p11-kit --with-p11-kit==${STAGING_DIR_HOST}/usr, --without-p11-kit, p11-kit, p11-kit"

RDEPENDS_${PN} = "sqlite3"
BBCLASSEXTEND = "native nativesdk"
