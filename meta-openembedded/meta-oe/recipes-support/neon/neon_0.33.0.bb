SUMMARY = "An HTTP and WebDAV client library with a C interface"
HOMEPAGE = "http://www.webdav.org/neon/"
SECTION = "libs"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://src/COPYING.LIB;md5=f30a9716ef3762e3467a2f62bf790f0a \
                    file://src/ne_utils.h;beginline=1;endline=20;md5=34c8e338bfa0237561e68d30c3c71133"

SRC_URI = "${DEBIAN_MIRROR}/main/n/neon27/neon27_${PV}.orig.tar.gz \
           file://pkgconfig.patch \
           file://0001-Disable-installing-documentation.patch \
           file://run-ptest \
           "

SRC_URI[sha256sum] = "659a5cc9cea05e6e7864094f1e13a77abbbdbab452f04d751a8c16a9447cf4b8"

inherit autotools-brokensep binconfig-disabled lib_package pkgconfig ptest

# Enable gnutls or openssl, not both
PACKAGECONFIG ?= "expat gnutls libproxy webdav zlib nls"
PACKAGECONFIG:class-native = "expat gnutls webdav zlib nls"
PACKAGECONFIG:remove:libc-musl = "nls"

PACKAGECONFIG[expat] = "--with-expat,--without-expat,expat"
PACKAGECONFIG[gnutls] = "--with-ssl=gnutls,,gnutls"
PACKAGECONFIG[gssapi] = "--with-gssapi,--without-gssapi,krb5"
PACKAGECONFIG[libproxy] = "--with-libproxy,--without-libproxy,libproxy"
PACKAGECONFIG[libxml2] = "--with-libxml2,--without-libxml2,libxml2"
PACKAGECONFIG[nls] = ",--disable-nls,gettext-native"
PACKAGECONFIG[openssl] = "--with-ssl=openssl,,openssl"
PACKAGECONFIG[webdav] = "--enable-webdav,--disable-webdav,"
PACKAGECONFIG[zlib] = "--with-zlib,--without-zlib,zlib"

EXTRA_OECONF += "--enable-shared --enable-threadsafe-ssl=posix"

# Do not install into /usr/local
EXTRA_OEMAKE:append:class-native = " prefix=${prefix_native}"

do_configure:prepend() {
    echo "${PV}" > ${S}/.version
}

do_compile:append() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'nls', 'true', 'false', d)}; then
        oe_runmake compile-gmo
    fi
    oe_runmake -C test
}

do_install_ptest(){
    BASIC_TESTS="auth basic redirect request session socket string-tests \
                 stubs uri-tests util-tests"
    DAV_TESTS="acl3744 lock oldacl props xml xmlreq"
    mkdir "${D}${PTEST_PATH}/test"
    for i in ${BASIC_TESTS} ${DAV_TESTS}
    do
        install -m 0755 "${B}/test/${i}" \
        "${D}${PTEST_PATH}/test"
    done
}

BINCONFIG = "${bindir}/neon-config"

BBCLASSEXTEND = "native"
