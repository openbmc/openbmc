SUMMARY = "Smart card library and applications"
DESCRIPTION = "OpenSC is a tool for accessing smart card devices. Basic\
functionality (e.g. SELECT FILE, READ BINARY) should work on any ISO\
7816-4 compatible smart card. Encryption and decryption using private\
keys on the smart card is possible with PKCS\
such as the FINEID (Finnish Electronic IDentity) card. Swedish Posten\
eID cards have also been confirmed to work."

HOMEPAGE = "http://www.opensc-project.org/opensc/"
SECTION = "System Environment/Libraries"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

#v0.19.0
SRCREV = "f1691fc91fc113191c3a8aaf5facd6983334ec47"
SRC_URI = "git://github.com/OpenSC/OpenSC \
           file://0001-Remove-redundant-logging.patch \
          "
DEPENDS = "openct pcsc-lite virtual/libiconv openssl"

S = "${WORKDIR}/git"
inherit autotools pkgconfig bash-completion

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
