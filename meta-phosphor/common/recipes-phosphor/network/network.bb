SUMMARY = "Network DBUS object"
DESCRIPTION = "Network DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-networkd"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "python-dbus python-pygobject"

SRC_URI += "git://github.com/openbmc/phosphor-networkd"

SRCREV = "a657afc9cc76dc6678edb8de9df569f92dd108e1"

S = "${WORKDIR}/git"

do_install() {
        install -d ${D}/${sbindir}
        install ${S}/netman.py ${D}/${sbindir}
}

