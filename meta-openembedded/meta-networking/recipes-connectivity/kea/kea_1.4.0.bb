SUMMARY = "ISC Kea DHCP Server"
DESCRIPTION = "Kea is the next generation of DHCP software developed by ISC. It supports both DHCPv4 and DHCPv6 protocols along with their extensions, e.g. prefix delegation and dynamic updates to DNS."
HOMEPAGE = "http://kea.isc.org"
SECTION = "connectivity"
LICENSE = "MPL-2.0 & Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=1666a9c4bb55adbc8939d85643096bd3"

DEPENDS += "kea-native"

SRC_URI = "\
    http://ftp.isc.org/isc/kea/${PV}/${BP}.tar.gz \
    file://0003-Makefile.am-update-hooksdir-for-lease_cmds.patch \
    file://0001-Fix-error-call-of-overloaded-distance-is-ambiguous.patch \
    file://kea-dhcp4.service \
    file://kea-dhcp6.service \
    file://kea-dhcp-ddns.service \
"
SRC_URI[md5sum] = "26eedd749101642757d788431605aaa5"
SRC_URI[sha256sum] = "c0f8ecb93657adfc5ab970c91706754e601084828493f053f159661d21b31128"

inherit autotools systemd

SYSTEMD_SERVICE_${PN} = "kea-dhcp4.service kea-dhcp6.service kea-dhcp-ddns.service"
SYSTEMD_AUTO_ENABLE = "disable"

do_configure_prepend_class-target() {
    mkdir -p ${B}/src/lib/log/compiler/
    ln -sf ${STAGING_BINDIR_NATIVE}/kea-msg-compiler ${B}/src/lib/log/compiler/kea-msg-compiler
}

do_install_append_class-target() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/kea-dhcp*service ${D}${systemd_system_unitdir}
    sed -i -e 's,@SBINDIR@,${sbindir},g' -e 's,@BASE_BINDIR@,${base_bindir},g' \
           -e 's,@LOCALSTATEDIR@,${localstatedir},g' -e 's,@SYSCONFDIR@,${sysconfdir},g' \
           ${D}${systemd_system_unitdir}/kea-dhcp*service
}

do_install_append() {
    rm -rf "${D}${localstatedir}"
}

PACKAGECONFIG ??= "openssl log4cplus boost"

PACKAGECONFIG[openssl] = "--with-openssl=${STAGING_DIR_TARGET}${prefix},,openssl,openssl"
PACKAGECONFIG[log4cplus] = "--with-log4cplus=${STAGING_DIR_TARGET}${prefix},,log4cplus,log4cplus"
PACKAGECONFIG[boost] = "--with-boost-include=${STAGING_INCDIR} --with-boost-lib-dir=${STAGING_LIBDIR} --with-boost-libs=-lboost_system,,boost,boost"

FILES_${PN}-staticdev += "${libdir}/kea/hooks/*.a ${libdir}/hooks/*.a"
FILES_${PN} += "${libdir}/hooks/*.so"

BBCLASSEXTEND += "native"
