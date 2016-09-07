SUMMARY = "User DBUS object"
DESCRIPTION = "User DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-networkd"
PR = "r1"

inherit allarch
inherit obmc-phosphor-license
inherit obmc-phosphor-user-mgmt
inherit obmc-phosphor-dbus-service

RDEPENDS_${PN} += "python-dbus python-pygobject python-pexpect"

SRC_URI += "git://github.com/openbmc/phosphor-networkd"

SRCREV = "15d498e2568b6e104de75e7423caab0c9a487485"

S = "${WORKDIR}/git"
INSTALL_NAME = "userman.py"

DBUS_SERVICE_${PN} += "org.openbmc.UserManager.service"

# Since base_do_compile finds a makefile (from networkd) it tries to
# compile.  Short-circuit that because we just need to copy a python
# file in this package.
do_compile() {
    :
}

do_install_append() {
echo "***installing $INSTALL_NAME"
        install -d ${D}/${sbindir}
        install ${S}/${INSTALL_NAME} ${D}/${sbindir}/obmc-phosphor-userd
}

