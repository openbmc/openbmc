SUMMARY = "Provides a way to load and enumerate PKCS#11 modules"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=02933887f609807fbb57aa4237d14a50"

inherit autotools gettext pkgconfig upstream-version-is-even

DEPENDS = "libtasn1 libffi"

SRC_URI = "http://p11-glue.freedesktop.org/releases/${BP}.tar.gz"
SRC_URI[md5sum] = "4e9bea1106628ffb820bdad24a819fac"
SRC_URI[sha256sum] = "ef3a339fcf6aa0e32c8c23f79ba7191e57312be2bda8b24e6d121c2670539a5c"

EXTRA_OECONF = "--without-trust-paths"

FILES_${PN} += " \
    ${libdir}/p11-kit-proxy.so \
    ${libdir}/pkcs11/*.so \
    ${libdir}/pkcs11/*.la"

# PN contains p11-kit-proxy.so, a symlink to a loadable module
INSANE_SKIP_${PN} = "dev-so"
