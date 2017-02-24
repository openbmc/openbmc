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

SRC_URI = "git://github.com/OpenSC/engine_pkcs11.git"
SRCREV = "132fcf2c8b319f9f4b2ebdc8dcb54ff496dc0519"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OECONF = "\
    --disable-static \
    --libdir ${libdir}/engines \
"

do_install_append () {
    rm -f ${D}${libdir}/engines/libpkcs11.la
}

FILES_${PN} += "${libdir}/engines/libpkcs11${SOLIBSDEV}"
FILES_${PN}-dbg += "${libdir}/engines/.debug/"

RDEPENDS_${PN} += "openssl libp11 opensc"
