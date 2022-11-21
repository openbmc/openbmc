SUMMARY = "Phosphor User Manager Daemon"
DESCRIPTION = "Daemon that does user management"
HOMEPAGE = "http://github.com/openbmc/phosphor-user-manager"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-logging"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "boost"
DEPENDS += "nss-pam-ldapd"
DEPENDS += "systemd"
SRCREV = "fef63038e468283cc37b6c8a7942ed27f15a2ca5"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI += "git://github.com/openbmc/phosphor-user-manager;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig
inherit obmc-phosphor-dbus-service
inherit useradd

EXTRA_OEMESON = "-Dtests=disabled"

FILES:phosphor-ldap += " \
        ${bindir}/phosphor-ldap-conf \
"
FILES:${PN} += " \
        ${systemd_unitdir} \
        ${datadir}/dbus-1 \
        ${datadir}/phosphor-certificate-manager \
"

USERADD_PACKAGES = "${PN} phosphor-ldap"

PACKAGE_BEFORE_PN = "phosphor-ldap"
DBUS_PACKAGES = "${USERADD_PACKAGES}"
# add groups needed for privilege maintenance
GROUPADD_PARAM:${PN} = "priv-admin; priv-operator; priv-user "
GROUPADD_PARAM:phosphor-ldap = "priv-admin; priv-operator; priv-user "
DBUS_SERVICE:${PN} += "xyz.openbmc_project.User.Manager.service"
DBUS_SERVICE:phosphor-ldap = " \
        xyz.openbmc_project.Ldap.Config.service \
"
