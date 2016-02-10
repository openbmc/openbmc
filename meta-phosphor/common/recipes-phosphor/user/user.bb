SUMMARY = "User DBUS object"
DESCRIPTION = "User DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-networkd"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "python-dbus python-pygobject python-pexpect"

SRC_URI += "git://github.com/openbmc/phosphor-networkd"

SRCREV = "9f804290dd0bf200a1ba28e107eae55bdb4076da"

S = "${WORKDIR}/git"

do_install() {
        install -d ${D}/${sbindir}
        install ${S}/userman.py ${D}/${sbindir}
}

