SUMMARY = "An L2TP client/server, designed for VPN use."
DESCRIPTION = "OpenL2TP is an open source L2TP client / server, written \
specifically for Linux. It has been designed for use as an enterprise \
L2TP VPN server or in commercial, Linux-based, embedded networking \
products and is able to support hundreds of sessions, each with \
different configuration. It is used by several ISPs to provide \
L2TP services and by corporations to implement L2TP VPNs."
HOMEPAGE = "http://www.openl2tp.org/"
SECTION = "net"

# cli and usl use license LGPL-2.1
LICENSE = "GPL-2.0 & LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=e9d9259cbbf00945adc25a470c1d3585 \
                    file://LICENSE;md5=f8970abd5ea9be701a0deedf5afd77a5 \
                    file://cli/LICENSE;md5=9c1387a3c5213aa40671438af3e00793 \
                    file://usl/LICENSE;md5=9c1387a3c5213aa40671438af3e00793 \
                    "

DEPENDS = "popt flex readline"

SRC_URI = "ftp://ftp.openl2tp.org/releases/${BP}/${BP}.tar.gz \
           file://Makefile-modify-CFLAGS-to-aviod-build-error.patch \
           file://openl2tp-simplify-gcc-warning-hack.patch \
           file://Makefile-obey-LDFLAGS.patch \
           file://0001-test-pppd_dummy.c-Fix-return-value.patch \
           file://0001-Use-1-instead-of-WAIT_ANY.patch \
           file://0002-cli-include-fcntl.h-for-O_CREAT-define.patch \
           file://0003-cli-Define-_GNU_SOURCE-for-getting-sighandler_t.patch \
           file://0001-l2tp_api-Included-needed-headers.patch \
           file://openl2tpd-initscript-fix.patch \
           file://openl2tpd-initscript-fix-sysconfig.patch \
           file://openl2tpd-initscript-fix-warning.patch \
           file://openl2tpd.service \
           "

SRC_URI_append_libc-musl = "\
           file://0004-Adjust-for-linux-kernel-headers-assumptions-on-glibc.patch \
           file://0002-user-ipv6-structures.patch \
           file://0001-l2tp_api.c-include-rpc-clnt.h-for-resultproc_t.patch \
           "
SRC_URI[md5sum] = "e3d08dedfb9e6a9a1e24f6766f6dadd0"
SRC_URI[sha256sum] = "1c97704d4b963a87fbc0e741668d4530933991515ae9ab0dffd11b5444f4860f"

inherit autotools-brokensep pkgconfig systemd

SYSTEMD_SERVICE_${PN} = "openl2tpd.service"
SYSTEMD_AUTO_ENABLE = "disable"

DEPENDS_append_libc-musl = " libtirpc"
CPPFLAGS_append_libc-musl = " -I${STAGING_INCDIR}/tirpc"
CFLAGS_append_libc-musl = " -I${STAGING_INCDIR}/tirpc"
LDFLAGS_append_libc-musl = " -ltirpc"

PARALLEL_MAKE = ""
EXTRA_OEMAKE = 'CFLAGS="${CFLAGS} -Wno-unused-but-set-variable" CPPFLAGS="${CPPFLAGS}" OPT_CFLAGS="${CFLAGS}"'

do_compile_prepend() {
    sed -i -e "s:SYS_LIBDIR=.*:SYS_LIBDIR=${libdir}:g" \
        -e 's:$(CROSS_COMPILE)as:${AS}:g' \
        -e 's:$(CROSS_COMPILE)ld:${LD}:g' \
        -e 's:$(CROSS_COMPILE)gcc:${CC}:g' \
        -e 's:$(CROSS_COMPILE)ar:${AR}:g' \
        -e 's:$(CROSS_COMPILE)nm:${NM}:g' \
        -e 's:$(CROSS_COMPILE)strip:${STRIP}:g' \
        -e 's:$(CROSS_COMPILE)install:install:g' \
        -e 's:CPPFLAGS-y:CPPFLAGS:g' \
        ${S}/Makefile
}

do_install_append () {
    install -d ${D}${sysconfdir}/init.d
    install -d ${D}${sysconfdir}/default
    install -m 0755 ${S}/etc/rc.d/init.d/openl2tpd ${D}${sysconfdir}/init.d/openl2tpd
    install -m 0755 ${S}/etc/sysconfig/openl2tpd ${D}${sysconfdir}/default/openl2tpd

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -D -m 0644 ${WORKDIR}/openl2tpd.service ${D}${systemd_system_unitdir}/openl2tpd.service
        sed -i -e 's,@STATEDIR@,${localstatedir},g' \
               -e 's,@SYSCONFDIR@,${sysconfdir},g' \
               -e 's,@SBINDIR@,${sbindir},g' \
               -e 's,@BINDIR@,${bindir},g' \
               -e 's,@BASE_SBINDIR@,${base_sbindir},g' \
               -e 's,@BASE_BINDIR@,${base_bindir},g' \
               ${D}${systemd_system_unitdir}/openl2tpd.service
    fi
}

RDEPENDS_${PN} = "ppp ppp-l2tp bash"
