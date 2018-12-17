SUMMARY = "Smart card library and applications"
DESCRIPTION = "OpenSC is a tool for accessing smart card devices. Basic\
functionality (e.g. SELECT FILE, READ BINARY) should work on any ISO\
7816-4 compatible smart card. Encryption and decryption using private\
keys on the smart card is possible with PKCS\
such as the FINEID (Finnish Electronic IDentity) card. Swedish Posten\
eID cards have also been confirmed to work."

HOMEPAGE = "http://www.opensc-project.org/opensc/"
SECTION = "System Environment/Libraries"

SRC_URI = "https://snapshot.debian.org/archive/debian/20180521T101428Z/pool/main/o/opensc/opensc_0.18.0.orig.tar.gz \
           file://0001-Fixed-gcc-8-compilation-errors-1353.patch \
          "

SRC_URI[md5sum] = "bce516f752e0db5327aa06cc0136fe27"
SRC_URI[sha256sum] = "6ef62b00e8fdbe3e386c3ee25c2cadb56c1931ea42f1a11dce8c947f51b45033"

DEPENDS = "openct pcsc-lite virtual/libiconv openssl"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

inherit autotools pkgconfig

S = "${WORKDIR}/OpenSC-${PV}"
EXTRA_OECONF = " \
    --disable-static \
    --enable-openct \
    --disable-pcsc \
    --disable-ctapi \
    --disable-doc \
"
EXTRA_OEMAKE = "DESTDIR=${D}"

RDEPENDS_${PN} = "readline"

FILES_${PN} += "\
    ${libdir}/opensc-pkcs11.so \
    ${libdir}/onepin-opensc-pkcs11.so \
    ${libdir}/pkcs11-spy.so \
"
FILES_${PN}-dev += "\
    ${libdir}/pkcs11/opensc-pkcs11.so \
    ${libdir}/pkcs11/onepin-opensc-pkcs11.so \
    ${libdir}/pkcs11/pkcs11-spy.so \
"
