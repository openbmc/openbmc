SUMMARY = "Network DBUS object"
DESCRIPTION = "Network DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-networkd"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-dbus-service

DBUS_SERVICE_${PN} += "org.openbmc.NetworkManager.service"
SYSTEMD_SERVICE_${PN} += "network-update-dns.service"

DEPENDS += "systemd"
RDEPENDS_${PN} += "python-dbus python-pygobject python-ipy"

SRC_URI += "git://github.com/openbmc/phosphor-networkd"

SRCREV = "92a5d1ec6ac1fe77e515e62c64f3ea01497fb63a"

S = "${WORKDIR}/git"

do_install_append() {
        install -d ${D}/${sbindir}
        install ${S}/netman.py ${D}/${sbindir}
        install ${S}/netman_watch_dns ${D}/${sbindir}
}

