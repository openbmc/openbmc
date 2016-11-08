SUMMARY = "User DBUS object"
DESCRIPTION = "User DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-networkd"
PR = "r1"

inherit allarch
inherit obmc-phosphor-license
inherit obmc-phosphor-dbus-service

PROVIDES += "virtual/obmc-user-mgmt"
RPROVIDES_${PN} += "virtual-obmc-user-mgmt"

RDEPENDS_${PN} += " \
        python-dbus \
        python-pygobject \
        python-subprocess \
        python-pexpect"

SRC_URI += "git://github.com/openbmc/phosphor-networkd"

SRCREV = "8613b87dfd9bf25bd85bb9ef551f261e73ef707f"

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

