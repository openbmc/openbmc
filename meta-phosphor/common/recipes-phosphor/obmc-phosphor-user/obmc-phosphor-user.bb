SUMMARY = "User DBUS object"
DESCRIPTION = "User DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-networkd"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-user-mgmt
inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "python-dbus python-pygobject python-pexpect"

SRC_URI += "git://github.com/openbmc/phosphor-networkd"

SRCREV = "448e8d839d37532d2667b9a38bb3aadb6c804e2e"

S = "${WORKDIR}/git"
INSTALL_NAME = "userman.py"

do_install() {
echo "***installing $INSTALL_NAME"
        install -d ${D}/${sbindir}
        install ${S}/${INSTALL_NAME} ${D}/${sbindir}/obmc-phosphor-userd
}

