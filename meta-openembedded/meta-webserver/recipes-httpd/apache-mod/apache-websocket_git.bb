SUMMARY = "Websocket module for Apache web server"
DESCRIPTION = "Process requests using the WebSocket protocol (RFC 6455)"
HOMEPAGE = "https://github.com/jchampio/apache-websocket/"
SECTION = "net"
LICENSE = "Apache-2.0"

inherit autotools-brokensep pkgconfig

DEPENDS = "apache2 apache2-native pbzip2-native"
RDEPENDS_${PN} += "apache2"

# Original (github.com/disconnect/apache-websocket) is dead since 2012, the
# fork contains patches from the modules ML and fixes CVE compliance issues
SRC_URI = "git://github.com/jchampio/apache-websocket.git"

SRCREV = "6968083264b90b89b1b9597a4ca03ba29e7ea2e1"

PV = "0.1.1+git${SRCPV}"

S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

EXTRA_OECONF = "APACHECTL=${STAGING_DIR_TARGET}${sbindir}/apachectl \
                LIBTOOL=${STAGING_DIR_TARGET}${bindir_crossscripts}/${HOST_SYS}-libtool"

do_install() {
    install -d ${D}${libexecdir}/apache2/modules
    install -m 755 ${B}/.libs/mod_websocket.so ${D}${libexecdir}/apache2/modules
}

FILES_${PN} += " ${libexecdir}/apache2/modules/* "
FILES_${PN}-dbg += " ${libexecdir}/apache2/modules/.debug/* "
