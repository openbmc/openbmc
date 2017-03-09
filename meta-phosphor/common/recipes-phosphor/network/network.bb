SUMMARY = "Network DBUS object"
DESCRIPTION = "Network DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-networkd"
PR = "r1"

inherit autotools pkgconfig
inherit obmc-phosphor-license
inherit obmc-phosphor-dbus-service

DBUS_SERVICE_${PN} += "org.openbmc.NetworkManager.service"
SYSTEMD_SERVICE_${PN} += "network-update-dns.service"

DEPENDS += "systemd"
DEPENDS += "autoconf-archive-native"
RDEPENDS_${PN} += "libsystemd"
RDEPENDS_${PN} += "python-dbus python-pygobject python-ipy"

SRC_URI += "git://github.com/openbmc/phosphor-networkd"
SRCREV = "ec01f6e0ebf62949762434c07b5cb13630286df6"

S = "${WORKDIR}/git"

do_install_append() {
        install -d ${D}/${sbindir}
        install ${S}/netman.py ${D}/${sbindir}
        install ${S}/conf/network-manager.conf ${D}/${sysconfdir}
}
