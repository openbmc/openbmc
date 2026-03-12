SECTION = "console/utils"
SUMMARY = "A free SOCKS server"
DESCRIPTION = "Dante consists of a SOCKS server and a SOCKS client,\
implementing RFC 1928 and related standards. It is a flexible product\
that can be used to provide convenient and secure network\
connectivity. Once installed, Dante can in most cases be made\
transparent to clients, providing functionality somewhat similar to\
what could be described as a non-transparent Layer 4 router."
HOMEPAGE = "http://www.inet.no/dante/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b3a8e7dc09bb720460f28c9d3796afa5"

SRC_URI = "https://www.inet.no/dante/files/dante-${PV}.tar.gz \
          "
SRC_URI[sha256sum] = "1973c7732f1f9f0a4c0ccf2c1ce462c7c25060b25643ea90f9b98f53a813faec"

# without --without-gssapi, config.log will contain reference to /usr/lib
# as a consequence of GSSAPI path being set to /usr by default.
# --with-gssapi-path=PATH specify gssapi path
# --without-gssapi        disable gssapi support
# --enable-release        build prerelease as full release
EXTRA_OECONF += "--without-gssapi --sbindir=${bindir}"

DEPENDS += "flex-native bison-native libpam libtirpc"
inherit autotools-brokensep features_check

CPPFLAGS += "-P"
CFLAGS += "-I${STAGING_INCDIR}/tirpc"
LIBS += "-ltirpc"

CFLAGS:append:libc-musl = " -D_GNU_SOURCE"

REQUIRED_DISTRO_FEATURES = "pam"

EXTRA_AUTORECONF = "-I ${S}"

PACKAGECONFIG ??= ""

do_install:append() {
    install -d ${D}${sysconfdir}
    cp ${S}/example/sock[sd].conf ${D}${sysconfdir}
}

PACKAGES =+ "${PN}-sockd ${PN}-libdsocks "

FILES:${PN}-libdsocks = "${libdir}/libdsocks.so"
FILES:${PN}-sockd = "${bindir}/sockd ${sysconfdir}/sockd.conf"

INSANE_SKIP:${PN}-libdsocks = "dev-elf"
