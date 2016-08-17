SUMMARY = "An HTTP and WebDAV client library with a C interface"
HOMEPAGE = "http://www.webdav.org/neon/"
SECTION = "libs"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://src/COPYING.LIB;md5=f30a9716ef3762e3467a2f62bf790f0a \
                    file://src/ne_utils.h;beginline=1;endline=20;md5=2caca609538eddaa6f6adf120a218037"

SRC_URI = "http://www.webdav.org/${BPN}/${BPN}-${PV}.tar.gz \
           file://pkgconfig.patch \
           file://gnutls_4.3_fixup.patch"

SRC_URI[md5sum] = "231adebe5c2f78fded3e3df6e958878e"
SRC_URI[sha256sum] = "00c626c0dc18d094ab374dbd9a354915bfe4776433289386ed489c2ec0845cdd"

inherit autotools binconfig-disabled lib_package pkgconfig

# Enable gnutls or openssl, not both
PACKAGECONFIG ?= "expat gnutls libproxy webdav zlib"
PACKAGECONFIG_class-native = "expat gnutls webdav zlib"

PACKAGECONFIG[expat] = "--with-expat,--without-expat,expat"
PACKAGECONFIG[gnutls] = "--with-ssl=gnutls,,gnutls"
PACKAGECONFIG[gssapi] = "--with-gssapi,--without-gssapi,krb5"
PACKAGECONFIG[libproxy] = "--with-libproxy,--without-libproxy,libproxy"
PACKAGECONFIG[libxml2] = "--with-libxml2,--without-libxml2,libxml2"
PACKAGECONFIG[openssl] = "--with-ssl=openssl,,openssl"
PACKAGECONFIG[webdav] = "--enable-webdav,--disable-webdav,"
PACKAGECONFIG[zlib] = "--with-zlib,--without-zlib,zlib"

EXTRA_OECONF += "--enable-shared"

do_compile_append() {
	oe_runmake -C test
}

BINCONFIG = "${bindir}/neon-config"

BBCLASSEXTEND = "native"
