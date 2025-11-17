SUMMARY = "Phosphor User Manager Daemon"
DESCRIPTION = "Daemon that does user management"
HOMEPAGE = "http://github.com/openbmc/phosphor-user-manager"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-logging"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "systemd"
SRCREV = "2e6614668ae19c5dc7e26fb87f9fe529a31819f1"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-user-manager;branch=master;protocol=https"
SRC_URI += "file://upgrade_hostconsole_group.sh"

S = "${WORKDIR}/git"

inherit meson pkgconfig
inherit obmc-phosphor-dbus-service
inherit useradd

EXTRA_OEMESON = "-Dtests=disabled"

PACKAGECONFIG ?= " \
    root-user-mgmt\
    ${@bb.utils.filter('DISTRO_FEATURES', 'ldap', d)} \
    "
PACKAGECONFIG[root-user-mgmt] = "-Droot_user_mgmt=enabled, -Droot_user_mgmt=disabled"
PACKAGECONFIG[ldap] = "-Dldap=enabled, -Dldap=disabled, nss-pam-ldapd"


do_install:append() {
  install -d ${D}${libexecdir}
  install -m 0755 ${UNPACKDIR}/upgrade_hostconsole_group.sh ${D}${libexecdir}/upgrade_hostconsole_group.sh
}

FILES:phosphor-ldap += " \
        ${bindir}/phosphor-ldap-conf \
"
FILES:${PN} += " \
        ${systemd_unitdir} \
        ${datadir}/dbus-1 \
        ${datadir}/phosphor-certificate-manager \
"

USERADD_PACKAGES = "${PN} phosphor-ldap"
RDEPENDS:${PN}:append:df-google-authenticator-libpam = " pam-google-authenticator google-authenticator-libpam"

PACKAGE_BEFORE_PN = "phosphor-ldap"
DBUS_PACKAGES = "${USERADD_PACKAGES}"
# add groups needed for privilege maintenance
GROUPADD_PARAM:${PN} = "priv-admin; priv-operator; priv-user "
GROUPADD_PARAM:phosphor-ldap = "priv-admin; priv-operator; priv-user "
DBUS_SERVICE:${PN} += "xyz.openbmc_project.User.Manager.service"
DBUS_SERVICE:phosphor-ldap = " \
        xyz.openbmc_project.Ldap.Config.service \
"

EXTRA_USERS_PARAMS += " \
   groupadd hostconsole; \
   "

EXTRA_USERS_PARAMS += " \
  usermod --append --groups hostconsole root; \
  "
