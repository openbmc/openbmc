SUMMARY = "ISC Kea DHCP Server"
DESCRIPTION = "Kea is the next generation of DHCP software developed by ISC. It supports both DHCPv4 and DHCPv6 protocols along with their extensions, e.g. prefix delegation and dynamic updates to DNS."
HOMEPAGE = "http://kea.isc.org"
SECTION = "connectivity"
LICENSE = "MPL-2.0 & Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=68d95543d2096459290a4e6b9ceccffa"

DEPENDS += "kea-native"

SRC_URI = "\
    http://ftp.isc.org/isc/kea/${PV}/${BP}.tar.gz \
    file://0001-remove-AC_TRY_RUN.patch \
    file://kea-dhcp4.service \
    file://kea-dhcp6.service \
    file://kea-dhcp-ddns.service \
"
SRC_URI[md5sum] = "4f8d1251fd41ef2e822a4eb3f0797d46"
SRC_URI[sha256sum] = "0bba8b045672884a928ff4b2a8575ac5ba420eb6ba47a9338f1932bc38dcf866"

inherit autotools systemd

SYSTEMD_SERVICE_${PN} = "kea-dhcp4.service kea-dhcp6.service kea-dhcp-ddns.service"
SYSTEMD_AUTO_ENABLE = "disable"

DEBUG_OPTIMIZATION_remove_mips = " -Og"
DEBUG_OPTIMIZATION_append_mips = " -O"
BUILD_OPTIMIZATION_remove_mips = " -Og"
BUILD_OPTIMIZATION_append_mips = " -O"

DEBUG_OPTIMIZATION_remove_mipsel = " -Og"
DEBUG_OPTIMIZATION_append_mipsel = " -O"
BUILD_OPTIMIZATION_remove_mipsel = " -Og"
BUILD_OPTIMIZATION_append_mipsel = " -O"

do_configure_prepend_class-target() {
    mkdir -p ${B}/src/lib/log/compiler/
    ln -sf ${STAGING_BINDIR_NATIVE}/kea-msg-compiler ${B}/src/lib/log/compiler/kea-msg-compiler
    # replace abs_top_builddir to avoid introducing the build path
    # don't expand the abs_top_builddir on the target as the abs_top_builddir is meanlingless on the target
    find ${S} -type f -name *.sh.in | xargs sed -i  "s:@abs_top_builddir@:@abs_top_builddir_placeholder@:g"
    sed -i "s:@abs_top_srcdir@:@abs_top_srcdir_placeholder@:g" ${S}/src/bin/admin/kea-admin.in
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
PACKAGECONFIG[boost] = "--with-boost-libs=-lboost_system,,boost,boost"

FILES_${PN}-staticdev += "${libdir}/kea/hooks/*.a ${libdir}/hooks/*.a"
FILES_${PN} += "${libdir}/hooks/*.so"

BBCLASSEXTEND += "native"

PARALLEL_MAKEINST = ""
