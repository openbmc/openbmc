SUMMARY = "User DBUS object"
DESCRIPTION = "User DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-networkd"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-user-mgmt
inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "python-dbus python-pygobject python-pexpect"

SRC_URI += "git://github.com/openbmc/phosphor-networkd"

SRCREV = "75757c08579200677391f5319aee68cafcae0bf1"

S = "${WORKDIR}/git"
INSTALL_NAME = "userman.py"

do_install() {
echo "***installing $INSTALL_NAME"
        install -d ${D}/${sbindir}
        install ${S}/${INSTALL_NAME} ${D}/${sbindir}/obmc-phosphor-userd
}

