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
LIC_FILES_CHKSUM = "file://LICENSE;md5=edd508404db7339042dfc861a3a690ad"

SRC_URI = "https://www.inet.no/dante/files/dante-${PV}.tar.gz \
          "
SRC_URI[sha256sum] = "418a065fe1a4b8ace8fbf77c2da269a98f376e7115902e76cda7e741e4846a5d"

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

REQUIRED_DISTRO_FEATURES = "pam"

EXTRA_AUTORECONF = "-I ${S}"

PACKAGECONFIG[libwrap] = ",--disable-libwrap,tcp-wrappers,libwrap"

PACKAGECONFIG ??= ""

do_install:append() {
    install -d ${D}${sysconfdir}
    cp ${S}/example/sock[sd].conf ${D}${sysconfdir}
}

PACKAGES =+ "${PN}-sockd ${PN}-libdsocks "

FILES:${PN}-libdsocks = "${libdir}/libdsocks.so"
FILES:${PN}-sockd = "${bindir}/sockd ${sysconfdir}/sockd.conf"

INSANE_SKIP:${PN}-libdsocks = "dev-elf"
