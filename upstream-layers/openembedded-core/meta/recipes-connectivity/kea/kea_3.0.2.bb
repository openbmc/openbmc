SUMMARY = "ISC Kea DHCP Server"
DESCRIPTION = "Kea is the next generation of DHCP software developed by ISC. It supports both DHCPv4 and DHCPv6 protocols along with their extensions, e.g. prefix delegation and dynamic updates to DNS."
HOMEPAGE = "http://kea.isc.org"
SECTION = "connectivity"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=fb634ed1d923b8b8fd1ed7ffc9b70ae4"

DEPENDS = "boost log4cplus openssl"

SRC_URI = "http://ftp.isc.org/isc/kea/${PV}/${BP}.tar.xz \
           file://kea-dhcp4.service \
           file://kea-dhcp6.service \
           file://kea-dhcp-ddns.service \
           file://kea-dhcp4-server \
           file://kea-dhcp6-server \
           file://kea-dhcp-ddns-server \
           file://kea.volatiles \
           file://fix-multilib-conflict.patch \
           file://0001-src-lib-log-logger_unittest_support.cc-do-not-write-.patch \
           file://0001-build-boost-1.89.0-fixes.patch \
           file://0001-meson-use-a-runtime-safe-interpreter-string.patch \
           file://0001-mk_cfgrpt.sh-strip-prefixes.patch \
           file://0001-d2-dhcp-46-radius-dhcpsrv-Avoid-Boost-lexical_cast-o.patch \
           file://0001-src-lib-log-logger_level_impl.cc-add-a-missing-inclu.patch \
           "
SRC_URI[sha256sum] = "29f4e44fa48f62fe15158d17411e003496203250db7b3459c2c79c09f379a541"

inherit meson pkgconfig systemd update-rc.d upstream-version-is-even

EXTRA_OEMESON += "-Dcrypto=openssl -Drunstatedir=${runtimedir} -Dkrb5=disabled -Dnetconf=disabled --install-umask=0022"

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

CXXFLAGS:remove = "-fvisibility-inlines-hidden"

do_configure:prepend() {
    # replace abs_top_builddir to avoid introducing the build path
    # don't expand the abs_top_builddir on the target as the abs_top_builddir is meanlingless on the target
    find ${S} -type f -name *.sh.in | xargs sed -i  "s:@abs_top_builddir@:@abs_top_builddir_placeholder@:g"
    sed -i "s:@abs_top_builddir@:@abs_top_builddir_placeholder@:g" ${S}/src/bin/admin/kea-admin.in
    export STRIP_PREFIXES="${RECIPE_SYSROOT_NATIVE}:${RECIPE_SYSROOT}:${WORKDIR}:${B}"
}

# patch out build host paths for reproducibility
do_compile:prepend:class-target() {
    sed -i -e "s,${WORKDIR},,g" -e "s,${HOSTTOOLS_DIR}/,,g" \
        ${B}/config.report \
        ${B}/src/lib/process/cfgrpt/config_report.cc
}

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -d ${D}/${sysconfdir}/default/volatiles

        install -m 0755 ${UNPACKDIR}/kea-*-server ${D}${sysconfdir}/init.d
        install -m 0644 ${UNPACKDIR}/kea.volatiles ${D}/${sysconfdir}/default/volatiles/99_kea
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/kea-dhcp*service ${D}${systemd_system_unitdir}

        sed -i -e 's,@SBINDIR@,${sbindir},g' -e 's,@BASE_BINDIR@,${base_bindir},g' \
            -e 's,@LOCALSTATEDIR@,${localstatedir},g' -e 's,@SYSCONFDIR@,${sysconfdir},g' \
            ${D}${systemd_system_unitdir}/kea-dhcp*service
    fi

    sed -i -e "s:${B}:@abs_top_builddir_placeholder@:g" \
           -e "s:${S}:@abs_top_srcdir_placeholder@:g" \
           ${D}${sbindir}/kea-admin

    rm -rf ${D}${datadir}/${BPN}/meson-info
    rm -rf ${D}${runtimedir}
    rm -rf ${D}${localstatedir}

    # Remove keactrl
    rm -f ${D}${sbindir}/keactrl ${D}${sysconfdir}/kea/keactrl.conf
}

CONFFILES:${PN} = "${sysconfdir}/kea/kea-ctrl-agent.conf \
                   ${sysconfdir}/kea/kea-dhcp-ddns.conf \
                   ${sysconfdir}/kea/kea-dhcp4.conf \
                   ${sysconfdir}/kea/kea-dhcp6.conf \
                  "

PACKAGES =+ "${PN}-python"
FILES:${PN}-python = "${nonarch_libdir}/python*/site-packages/*"
RDEPENDS:${PN}-python = "python3"

FILES:${PN}-staticdev += "${libdir}/kea/hooks/*.a ${libdir}/hooks/*.a"
FILES:${PN} += "${libdir}/hooks/*.so"
