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

SRCREV = "65e5abefd6b73c8f383a335291c32111ae787be0"

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

