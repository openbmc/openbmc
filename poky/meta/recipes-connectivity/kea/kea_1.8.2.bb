SUMMARY = "ISC Kea DHCP Server"
DESCRIPTION = "Kea is the next generation of DHCP software developed by ISC. It supports both DHCPv4 and DHCPv6 protocols along with their extensions, e.g. prefix delegation and dynamic updates to DNS."
HOMEPAGE = "http://kea.isc.org"
SECTION = "connectivity"
LICENSE = "MPL-2.0 & Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=68d95543d2096459290a4e6b9ceccffa"

DEPENDS = "boost log4cplus openssl"

SRC_URI = "http://ftp.isc.org/isc/kea/${PV}/${BP}.tar.gz \
           file://0001-keactrl.in-create-var-lib-kea-and-var-run-kea-folder.patch \
           file://kea-dhcp4.service \
           file://kea-dhcp6.service \
           file://kea-dhcp-ddns.service \
           file://kea-dhcp4-server \
           file://kea-dhcp6-server \
           file://kea-dhcp-ddns-server \
           file://fix-multilib-conflict.patch \
           file://fix_pid_keactrl.patch \
           file://0001-src-lib-log-logger_unittest_support.cc-do-not-write-.patch \
           file://0001-ax_cpp11.m4-Include-memory-header.patch \
           file://0001-include-limits.h.patch \
           "
SRC_URI[sha256sum] = "486ca7abedb9d6fdf8e4344ad8688d1171f2ef0f5506d118988aadeae80a1d39"

inherit autotools systemd update-rc.d upstream-version-is-even

INITSCRIPT_NAME = "kea-dhcp4-server"
INITSCRIPT_PARAMS = "defaults 30"

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

EXTRA_OECONF = "--with-boost-libs=-lboost_system \
                --with-log4cplus=${STAGING_DIR_TARGET}${prefix} \
                --with-openssl=${STAGING_DIR_TARGET}${prefix}"

do_configure_prepend() {
    # replace abs_top_builddir to avoid introducing the build path
    # don't expand the abs_top_builddir on the target as the abs_top_builddir is meanlingless on the target
    find ${S} -type f -name *.sh.in | xargs sed -i  "s:@abs_top_builddir@:@abs_top_builddir_placeholder@:g"
    sed -i "s:@abs_top_srcdir@:@abs_top_srcdir_placeholder@:g" ${S}/src/bin/admin/kea-admin.in
}

# patch out build host paths for reproducibility
do_compile_prepend_class-target() {
        sed -i -e "s,${WORKDIR},,g" ${B}/config.report
}

do_install_append() {
    install -d ${D}${sysconfdir}/init.d
    install -d ${D}${systemd_system_unitdir}

    install -m 0644 ${WORKDIR}/kea-dhcp*service ${D}${systemd_system_unitdir}
    install -m 0755 ${WORKDIR}/kea-*-server ${D}${sysconfdir}/init.d
    sed -i -e 's,@SBINDIR@,${sbindir},g' -e 's,@BASE_BINDIR@,${base_bindir},g' \
           -e 's,@LOCALSTATEDIR@,${localstatedir},g' -e 's,@SYSCONFDIR@,${sysconfdir},g' \
           ${D}${systemd_system_unitdir}/kea-dhcp*service ${D}${sbindir}/keactrl
}

do_install_append() {
    rm -rf "${D}${localstatedir}"
}

CONFFILES_${PN} = "${sysconfdir}/kea/keactrl.conf"

FILES_${PN}-staticdev += "${libdir}/kea/hooks/*.a ${libdir}/hooks/*.a"
FILES_${PN} += "${libdir}/hooks/*.so"

PARALLEL_MAKEINST = ""
