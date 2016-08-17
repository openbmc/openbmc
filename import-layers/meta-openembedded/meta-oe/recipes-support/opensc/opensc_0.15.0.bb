SUMMARY = "Smart card library and applications"
DESCRIPTION = "OpenSC is a tool for accessing smart card devices. Basic\
functionality (e.g. SELECT FILE, READ BINARY) should work on any ISO\
7816-4 compatible smart card. Encryption and decryption using private\
keys on the smart card is possible with PKCS\
such as the FINEID (Finnish Electronic IDentity) card. Swedish Posten\
eID cards have also been confirmed to work."

HOMEPAGE = "http://www.opensc-project.org/opensc/"
SECTION = "System Environment/Libraries"

SRC_URI = "${DEBIAN_MIRROR}/main/o/${BPN}/${BPN}_${PV}.orig.tar.gz"

SRC_URI[md5sum] = "f266024e5a9630821ffa0ac14f72e369"
SRC_URI[sha256sum] = "8f8f8cf52e0252334e4dfdccca829b876a3de6340deb772aa0bfe0c0cc10eaf5"

S = "${WORKDIR}/OpenSC-${PV}"
DEPENDS = "openct pcsc-lite virtual/libiconv openssl"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

inherit autotools pkgconfig

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
