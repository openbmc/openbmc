SUMMARY = "Network DBUS object"
DESCRIPTION = "Network DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-networkd"
PR = "r1"

inherit autotools pkgconfig
inherit pythonnative
inherit obmc-phosphor-license
inherit obmc-phosphor-dbus-service

DBUS_SERVICE_${PN} += "org.openbmc.NetworkManager.service"
SYSTEMD_SERVICE_${PN} += "network-update-dns.service"

DEPENDS += "systemd"
DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus sdbusplus-native"
DEPENDS += "phosphor-dbus-interfaces phosphor-dbus-interfaces-native"
DEPENDS += "phosphor-logging"

RDEPENDS_${PN} += "libsystemd"
RDEPENDS_${PN} += "python-dbus python-pygobject python-ipy"
RDEPENDS_${PN} += "sdbusplus phosphor-dbus-interfaces"
RDEPENDS_${PN} += "phosphor-logging"

SRC_URI += "git://github.com/openbmc/phosphor-networkd"
SRCREV = "968d203ef934d68ded7e026d38dc77835116dedd"

S = "${WORKDIR}/git"

do_install_append() {
        install -d ${D}/${sbindir}
        install ${S}/netman.py ${D}/${sbindir}
        install ${S}/conf/network-manager.conf ${D}/${sysconfdir}
}
