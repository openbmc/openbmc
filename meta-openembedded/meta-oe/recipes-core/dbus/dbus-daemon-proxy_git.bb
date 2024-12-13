SUMMARY = "dbus forwarding daemon"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://dbus-daemon-proxy.c;endline=19;md5=41df6d21fe1c97d6a1cc22a5bf374cba"
DEPENDS = "dbus dbus-glib"
SRCREV = "1226a0a1374628ff191f6d8a56000be5e53e7608"
PV = "0.0.0+git"

SRC_URI = "git://github.com/alban/dbus-daemon-proxy;branch=master;protocol=https \
           file://0001-dbus-daemon-proxy-Return-DBUS_HANDLER_RESULT_NOT_YET.patch \
           "

# Upstream repo does not tag
UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

inherit pkgconfig

do_compile() {
    ${CC} ${CFLAGS} -o dbus-daemon-proxy dbus-daemon-proxy.c `pkg-config --cflags --libs dbus-glib-1` ${LDFLAGS}
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 dbus-daemon-proxy ${D}${bindir}
}
