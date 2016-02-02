SUMMARY = "Network DBUS object"
DESCRIPTION = "Network DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-networkd"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "python-dbus python-pygobject"

SRC_URI += "git://github.com/openbmc/phosphor-networkd \
            file://80-dhcp.network \
            "

SRCREV = "6b3d6af5b9c38d734f20e859394db275e141328e"

S = "${WORKDIR}/git"

do_install() {
        install -d ${D}/${sbindir}
        install ${S}/netman.py ${D}/${sbindir}
        install -d ${D}/etc/systemd/network/
        install ${WORKDIR}/80-dhcp.network ${D}/etc/systemd/network/
}

