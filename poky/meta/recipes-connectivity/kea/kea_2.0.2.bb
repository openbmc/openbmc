SUMMARY = "ISC Kea DHCP Server"
DESCRIPTION = "Kea is the next generation of DHCP software developed by ISC. It supports both DHCPv4 and DHCPv6 protocols along with their extensions, e.g. prefix delegation and dynamic updates to DNS."
HOMEPAGE = "http://kea.isc.org"
SECTION = "connectivity"
LICENSE = "MPL-2.0 & Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=b4ecee995eeb6780a17dd7e539e97abc"

DEPENDS = "boost log4cplus openssl"

SRC_URI = "http://ftp.isc.org/isc/kea/${PV}/${BP}.tar.gz \
           file://kea-dhcp4.service \
           file://kea-dhcp6.service \
           file://kea-dhcp-ddns.service \
           file://kea-dhcp4-server \
           file://kea-dhcp6-server \
           file://kea-dhcp-ddns-server \
           file://fix-multilib-conflict.patch \
           file://fix_pid_keactrl.patch \
           file://0001-src-lib-log-logger_unittest_support.cc-do-not-write-.patch \
           "
SRC_URI[sha256sum] = "8d28213bdc8e2bb870a383b30ac1e53d54e1eba43d2f86e5151b08b66aa6cf32"

inherit autotools systemd update-rc.d upstream-version-is-even

INITSCRIPT_NAME = "kea-dhcp4-server"
INITSCRIPT_PARAMS = "defaults 30"

SYSTEMD_SERVICE:${PN} = "kea-dhcp4.service kea-dhcp6.service kea-dhcp-ddns.service"
SYSTEMD_AUTO_ENABLE = "disable"

DEBUG_OPTIMIZATION:remove:mips = " -Og"
DEBUG_OPTIMIZATION:append:mips = " -O"
BUILD_OPTIMIZATION:remove:mips = " -Og"
BUILD_OPTIMIZATION:append:mips = " -O"

DEBUG_OPTIMIZATION:remove:mipsel = " -Og"
DEBUG_OPTIMIZATION:append:mipsel = " -O"
BUILD_OPTIMIZATION:remove:mipsel = " -Og"
BUILD_OPTIMIZATION:append:mipsel = " -O"

EXTRA_OECONF = "--with-boost-libs=-lboost_system \
                --with-log4cplus=${STAGING_DIR_TARGET}${prefix} \
                --with-openssl=${STAGING_DIR_TARGET}${prefix}"

do_configure:prepend() {
    # replace abs_top_builddir to avoid introducing the build path
    # don't expand the abs_top_builddir on the target as the abs_top_builddir is meanlingless on the target
    find ${S} -type f -name *.sh.in | xargs sed -i  "s:@abs_top_builddir@:@abs_top_builddir_placeholder@:g"
    sed -i "s:@abs_top_srcdir@:@abs_top_srcdir_placeholder@:g" ${S}/src/bin/admin/kea-admin.in
}

# patch out build host paths for reproducibility
do_compile:prepend:class-target() {
        sed -i -e "s,${WORKDIR},,g" ${B}/config.report
}

do_install:append() {
    install -d ${D}${sysconfdir}/init.d
    install -d ${D}${systemd_system_unitdir}

    install -m 0644 ${WORKDIR}/kea-dhcp*service ${D}${systemd_system_unitdir}
    install -m 0755 ${WORKDIR}/kea-*-server ${D}${sysconfdir}/init.d
    sed -i -e 's,@SBINDIR@,${sbindir},g' -e 's,@BASE_BINDIR@,${base_bindir},g' \
           -e 's,@LOCALSTATEDIR@,${localstatedir},g' -e 's,@SYSCONFDIR@,${sysconfdir},g' \
           ${D}${systemd_system_unitdir}/kea-dhcp*service ${D}${sbindir}/keactrl
}

do_install:append() {
    rm -rf "${D}${localstatedir}"
}

CONFFILES:${PN} = "${sysconfdir}/kea/keactrl.conf"

FILES:${PN}-staticdev += "${libdir}/kea/hooks/*.a ${libdir}/hooks/*.a"
FILES:${PN} += "${libdir}/hooks/*.so"

PARALLEL_MAKEINST = ""
