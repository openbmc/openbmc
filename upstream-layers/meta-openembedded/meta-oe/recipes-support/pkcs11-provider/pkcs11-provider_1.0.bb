SUMMARY = "An OpenSSL provider that allows direct interfacing with pkcs11 drivers"
DESCRIPTION = "\
This is an Openssl 3.x provider to access Hardware or Software Tokens using \
the PKCS#11 Cryptographic Token Interface\
\
This code targets version 3.1 of the interface but should be backwards \
compatible to previous versions as well.\
"
HOMEPAGE = "https://github.com/latchset/pkcs11-provider"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=b53b787444a60266932bd270d1cf2d45"
DEPENDS = "\
    openssl \
    p11-kit \
"

SRCREV = "8f6b94409d4872265076df310492da1e5f6abdf7"

SRC_URI = "git://github.com/latchset/${BPN}.git;branch=main;protocol=https"


inherit meson pkgconfig

# Overwrite default pkcs11 module path
#EXTRA_OEMESON += "-Ddefault_pkcs11_module=/path/to/mymodule.so"

FILES:${PN} += "${libdir}/ossl-modules/pkcs11.so"
