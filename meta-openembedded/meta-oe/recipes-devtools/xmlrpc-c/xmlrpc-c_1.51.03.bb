DESCRIPTION = "XML-RPC for C/C++ is programming libraries and related tools to help you \
write an XML-RPC server or client in C or C++."

HOMEPAGE = "http://xmlrpc-c.sourceforge.net/"
LICENSE = "BSD & MIT"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=aefbf81ba0750f02176b6f86752ea951"

SRC_URI = "git://github.com/mirror/xmlrpc-c.git \
           file://0001-test-cpp-server_abyss-Fix-build-with-clang-libc.patch \
           file://0002-fix-formatting-issues.patch \
           "
#Release 1.51.03
SRCREV = "343a3b98e54999d67edb644bcd395aa9784fb16e"

S = "${WORKDIR}/git/stable"

DEPENDS = "libxml2"

inherit autotools-brokensep binconfig pkgconfig

TARGET_CFLAGS += "-Wno-narrowing"

EXTRA_OEMAKE += "CC_FOR_BUILD='${BUILD_CC}' \
                 LD_FOR_BUILD='${BUILD_LD}' \
                 CFLAGS_FOR_BUILD='${BUILD_CFLAGS}' \
                 LDFLAGS_FOR_BUILD='${BUILD_LDFLAGS}' \
                 "

EXTRA_OECONF += "--disable-libwww-client --disable-wininet-client"

PACKAGECONFIG ??= "curl cplusplus"

PACKAGECONFIG[abyss] = "--enable-abyss-server --enable-abyss-threads --enable-abyss-openssl,--disable-abyss-server --disable-abyss-threads --disable-abyss-openssl,openssl,"
PACKAGECONFIG[cplusplus] = "--enable-cplusplus,--disable-cplusplus,,"
PACKAGECONFIG[curl] = "--enable-curl-client,--disable-curl-client,curl,curl"

do_configure() {
        install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}
        install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}
        autoconf
        oe_runconf
        # license is incompatible with lib/util/getoptx.*
        rm -fv ${S}/tools/turbocharger/mod_gzip.c
}

BBCLASSEXTEND = "native"

CLEANBROKEN = "1"
