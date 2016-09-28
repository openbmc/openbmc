SUMMARY = "Network DBUS object"
DESCRIPTION = "Network DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-networkd"
PR = "r1"

inherit autotools pkgconfig
inherit obmc-phosphor-license
inherit obmc-phosphor-dbus-service

DBUS_SERVICE_${PN} += "org.openbmc.NetworkManager.service"
SYSTEMD_SERVICE_${PN} += "network-update-dns.service"

DEPENDS += "autoconf-archive-native"
RDEPENDS_${PN} += "python-dbus python-pygobject python-ipy"

SRC_URI += "git://github.com/openbmc/phosphor-networkd"
SRCREV = "3e0ee34ed8d25385199cf68136b3abd054975f64"

S = "${WORKDIR}/git"

do_install_append() {
        install -d ${D}/${sbindir}
        install ${S}/netman.py ${D}/${sbindir}
}
