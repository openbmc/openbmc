SUMMARY = "NSS and PAM module for using LDAP as a naming service"
DESCRIPTION = "\
 daemon for NSS and PAM lookups using LDAP \
 This package provides a daemon for retrieving user accounts and similar \
 system information from LDAP. It is used by the libnss-ldapd and \
 libpam-ldapd packages but is not very useful by itself. \
 "
HOMEPAGE = "https://github.com/arthurdejong/nss-pam-ldapd"
S = "${WORKDIR}/git"
SECTION = "base"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
DEPENDS += "libpam openldap krb5"

SRC_URI = "\
        git://github.com/arthurdejong/nss-pam-ldapd;branch=master;protocol=https \
        file://nslcd.init \
        file://nslcd.service \
        "
SRCREV="47fd03bc80d470de881c025ff934325bd7def0b5"

SYSTEMD_SERVICE:${PN} = "nslcd.service"

inherit autotools
inherit update-rc.d systemd

EXTRA_OECONF = "\
        --disable-pynslcd \
        --libdir=${base_libdir} \
        --with-pam-seclib-dir=${base_libdir}/security \
        "

do_install:append() {
        install -D -m 0755 ${UNPACKDIR}/nslcd.init ${D}${sysconfdir}/init.d/nslcd
        sed -i -e 's/^uid nslcd/# uid nslcd/;' ${D}${sysconfdir}/nslcd.conf
        sed -i -e 's/^gid nslcd/# gid nslcd/;' ${D}${sysconfdir}/nslcd.conf
        sed -i -e 's/^base dc=example,dc=com/base ${LDAP_DN}/;' ${D}${sysconfdir}/nslcd.conf
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/nslcd.service ${D}${systemd_system_unitdir}
}

RDEPENDS:${PN} += "nscd"

FILES:${PN} += "${base_libdir}/security ${datadir}"
FILES:${PN}-dbg += "${base_libdir}/security/.debug"

CONFFILES:${PN} += "${sysconfdir}/nslcd.conf"

INITSCRIPT_NAME = "nslcd"
INITSCRIPT_PARAMS = "defaults"

LDAP_DN ?= "dc=my-domain,dc=com"
