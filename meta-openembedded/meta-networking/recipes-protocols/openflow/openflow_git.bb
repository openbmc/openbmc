SUMMARY = "OpenFlow communications protocol"
DESCRIPTION = "\
Open standard that enables researchers to run experimental protocols in \
contained networks.  OpenFlow is a communications interface between \
control and forwarding planes of a software-defined networking architecture.\
"
HOMEPAGE = "http://www.openflow.org"

SECTION = "net"
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://COPYING;md5=e870c934e2c3d6ccf085fd7cf0a1e2e2"

SRCREV = "82ad07d997b0b2ee70e1b2c7e82fcc6d0ccf23ea"

PV = "1.0+git"

SRC_URI = "git://github.com/mininet/openflow;protocol=https;branch=master \
           file://0001-Check-and-use-strlcpy-from-libc-before-defining-own.patch \
           file://0002-lib-netdev-Adjust-header-include-sequence.patch \
           file://0001-generate-not-static-get_dh-functions.patch \
           file://0001-socket-util-Include-sys-stat.h-for-fchmod.patch \
           file://0001-Makefile.am-Specify-export-dynamic-directly-to-linke.patch \
"
CVE_STATUS[CVE-2015-1611] = "not-applicable-config: Not referred to our implementation of openflow"
CVE_STATUS[CVE-2015-1612] = "not-applicable-config: Not referred to our implementation of openflow"
CVE_STATUS[CVE-2018-1078] = "cpe-incorrect: This CVE is not for this product but cve-check assumes it is \
because two CPE collides when checking the NVD database"

PACKAGECONFIG ??= ""
PACKAGECONFIG[openssl] = "--enable-ssl,--disable-ssl, openssl openssl-native, libssl"

EXTRA_OECONF += " \
                 KARCH=${TARGET_ARCH} \
                 ${@bb.utils.contains('PACKAGECONFIG', 'openssl', 'SSL_LIBS="-lssl -lcrypto"', '', d)} \
                "

DEPENDS:append:libc-musl = " libexecinfo"
LDFLAGS:append:libc-musl = " -lexecinfo"

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig

do_configure:prepend() {
    ./boot.sh
}

do_install:append() {
    # Remove /var/run as it is created on startup
    rm -rf ${D}${localstatedir}/run

    # /var/log/openflow needs to be created in runtime. Use rmdir to catch if
    # upstream stops creating /var/log/openflow, or adds something else in
    # /var/log.
    rmdir ${D}${localstatedir}/log/${BPN} ${D}${localstatedir}/log
    rmdir --ignore-fail-on-non-empty ${D}${localstatedir}

    # Create /var/log/openflow in runtime.
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}" ]; then
        install -d ${D}${nonarch_libdir}/tmpfiles.d
        echo "d ${localstatedir}/log/${BPN} - - - -" > ${D}${nonarch_libdir}/tmpfiles.d/${BPN}.conf
    fi
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'sysvinit', d)}" ]; then
        install -d ${D}${sysconfdir}/default/volatiles
        echo "d root root 0755 ${localstatedir}/log/${BPN} none" > ${D}${sysconfdir}/default/volatiles/99_${BPN}
    fi
}

FILES:${PN} += "${nonarch_libdir}/tmpfiles.d"
