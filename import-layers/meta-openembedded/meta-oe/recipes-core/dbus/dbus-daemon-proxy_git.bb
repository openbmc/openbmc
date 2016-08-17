SUMMARY = "dbus forwarding daemon"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://dbus-daemon-proxy.c;endline=19;md5=41df6d21fe1c97d6a1cc22a5bf374cba"
DEPENDS = "dbus dbus-glib"
SRCREV = "1226a0a1374628ff191f6d8a56000be5e53e7608"
PV = "0.0.0+gitr${SRCPV}"
PR = "r1"

ASNEEDED_pn-dbus-daemon-proxy = ""

SRC_URI = "git://git.collabora.co.uk/git/user/alban/dbus-daemon-proxy"
S = "${WORKDIR}/git"

do_compile() {
    ${CC} ${LDFLAGS} `pkg-config --cflags --libs dbus-glib-1` -o dbus-daemon-proxy dbus-daemon-proxy.c
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 dbus-daemon-proxy ${D}${bindir}
}

