SUMMARY = "Phosphor User Manager Daemon"
DESCRIPTION = "Daemon that does user management"
HOMEPAGE = "http://github.com/openbmc/phosphor-user-manager"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-logging"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "boost"
DEPENDS += "nss-pam-ldapd"
DEPENDS += "systemd"
PACKAGE_BEFORE_PN = "phosphor-ldap"

inherit useradd

USERADD_PACKAGES = "${PN} phosphor-ldap"
DBUS_PACKAGES = "${USERADD_PACKAGES}"
# add groups needed for privilege maintenance
GROUPADD_PARAM_${PN} = "priv-admin; priv-operator; priv-user "
GROUPADD_PARAM_phosphor-ldap = "priv-admin; priv-operator; priv-user "

DBUS_SERVICE_${PN} += "xyz.openbmc_project.User.Manager.service"
FILES_phosphor-ldap += " \
        ${bindir}/phosphor-ldap-conf \
        ${bindir}/phosphor-ldap-mapper \
"
DBUS_SERVICE_phosphor-ldap = " \
        xyz.openbmc_project.Ldap.Config.service \
        xyz.openbmc_project.LDAP.PrivilegeMapper.service \
"
SRC_URI += "git://github.com/openbmc/phosphor-user-manager"
SRCREV = "9638afb9aa848aa0e696c2447e0fbc70a0aa5eed"
S = "${WORKDIR}/git"
