SUMMARY = "A PKCS"
DESCRIPTION = "\
Engine_pkcs11 is an implementation of an engine for OpenSSL. It can be \
loaded using code, config file or command line and will pass any function \
call by openssl to a PKCS cards and software for using smart cards in PKCS"
HOMEPAGE = "https://github.com/OpenSC/engine_pkcs11"
SECTION = "Development/Libraries"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://src/engine_pkcs11.h;startline=1;endline=26;md5=973a19f8a6105de047f2adfbbfc04c33"
DEPENDS = "openssl libp11"

SRC_URI = "git://github.com/OpenSC/engine_pkcs11.git;protocol=http"
SRCREV = "6909d6761c8820e1750fa0bf4fa8532c82f34e35"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OECONF = "\
    --disable-static \
    --libdir ${libdir}/openssl \
"

do_install_append () {
    rm -f ${D}${libdir}/openssl/engines/libpkcs11.la
}

FILES_${PN} += "${libdir}/openssl/engines/libpkcs11.so*"
FILES_${PN}-dbg += "${libdir}/openssl/engines/.debug/*"
