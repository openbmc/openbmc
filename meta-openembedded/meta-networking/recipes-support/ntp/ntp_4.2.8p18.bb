SUMMARY = "Network Time Protocol daemon and utilities"
DESCRIPTION = "The Network Time Protocol (NTP) is used to \
synchronize the time of a computer client or server to \
another server or reference time source, such as a radio \
or satellite receiver or modem."
HOMEPAGE = "http://support.ntp.org"
SECTION = "net"
LICENSE = "NTP"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=2311915f6d5142b06395231b0ffeaf29"

DEPENDS = "libevent"

SRC_URI = "http://www.eecis.udel.edu/~ntp/ntp_spool/ntp4/ntp-4.2/ntp-${PV}.tar.gz \
           file://ntp-4.2.4_p6-nano.patch \
           file://reproducibility-fixed-path-to-posix-shell.patch \
           file://0001-libntp-Do-not-use-PTHREAD_STACK_MIN-on-glibc.patch \
           file://0001-test-Fix-build-with-new-compiler-defaults-to-fno-com.patch \
           file://0001-sntp-Fix-types-in-check-for-pthread_detach.patch \
           file://ntpd \
           file://ntp.conf \
           file://ntpd.service \
           file://sntp.service \
           file://sntp \
           file://ntpd.list \
"

SRC_URI[sha256sum] = "cf84c5f3fb1a295284942624d823fffa634144e096cfc4f9969ac98ef5f468e5"

CVE_STATUS[CVE-2016-9312] = "not-applicable-platform: Issue only applies on Windows"
CVE_STATUS[CVE-2019-11331] = "upstream-wontfix: inherent to RFC 5905 and cannot be fixed without breaking compatibility"
CVE_STATUS_GROUPS += "CVE_STATUS_NTP"
CVE_STATUS_NTP[status] = "fixed-version: Yocto CVE check can not handle 'p' in ntp version"
CVE_STATUS_NTP = " \
    CVE-2015-5146 \
    CVE-2015-5300 \
    CVE-2015-7975 \
    CVE-2015-7976 \
    CVE-2015-7977 \
    CVE-2015-7978 \
    CVE-2015-7979 \
    CVE-2015-8138 \
    CVE-2015-8139 \
    CVE-2015-8140 \
    CVE-2015-8158 \
    CVE-2016-1547 \
    CVE-2016-2516 \
    CVE-2016-2517 \
    CVE-2016-2519 \
    CVE-2016-7429 \
    CVE-2016-7433 \
    CVE-2016-9310 \
    CVE-2016-9311 \
"


inherit autotools update-rc.d useradd systemd pkgconfig

# The ac_cv_header_readline_history is to stop ntpdc depending on either
# readline or curses
EXTRA_OECONF += "--with-net-snmp-config=no \
                 --without-ntpsnmpd \
                 ac_cv_header_readline_history_h=no \
                 --with-yielding_select=yes \
                 --with-locfile=redhat \
                 --without-rpath \
                 "
CFLAGS:append = " -DPTYS_ARE_GETPT -DPTYS_ARE_SEARCHED"

USERADD_PACKAGES = "${PN}"
NTP_USER_HOME ?= "/var/lib/ntp"
USERADD_PARAM:${PN} = "--system --home-dir ${NTP_USER_HOME} \
                       --no-create-home \
                       --shell /bin/false --user-group ntp"

# NB: debug is default-enabled by NTP; keep it default-enabled here.
PACKAGECONFIG ??= "cap debug refclocks openssl \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
"
PACKAGECONFIG[openssl] = "--with-openssl-libdir=${STAGING_LIBDIR} \
                          --with-openssl-incdir=${STAGING_INCDIR} \
                          --with-crypto, \
                          --without-openssl --without-crypto, \
                          openssl"
PACKAGECONFIG[cap] = "--enable-linuxcaps,--disable-linuxcaps,libcap"
PACKAGECONFIG[readline] = "--with-lineeditlibs,--without-lineeditlibs,readline"
PACKAGECONFIG[refclocks] = "--enable-all-clocks,--disable-all-clocks,pps-tools"
PACKAGECONFIG[debug] = "--enable-debugging,--disable-debugging"
PACKAGECONFIG[mdns] = "ac_cv_header_dns_sd_h=yes,ac_cv_header_dns_sd_h=no,mdns"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

do_install:append() {
    install -d ${D}${sysconfdir}/init.d
    install -m 644 ${UNPACKDIR}/ntp.conf ${D}${sysconfdir}
    install -m 755 ${UNPACKDIR}/ntpd ${D}${sysconfdir}/init.d

    install -m 755 -d ${D}${NTP_USER_HOME}
    chown ntp:ntp ${D}${NTP_USER_HOME}

    # Fix hardcoded paths in scripts
    sed -i 's!/usr/sbin/!${sbindir}/!g' ${D}${sysconfdir}/init.d/ntpd
    sed -i 's!/usr/bin/!${bindir}/!g' ${D}${sysconfdir}/init.d/ntpd
    sed -i 's!/etc/!${sysconfdir}/!g' ${D}${sysconfdir}/init.d/ntpd
    sed -i 's!/var/!${localstatedir}/!g' ${D}${sysconfdir}/init.d/ntpd
    sed -i '1s,#!.*perl -w,#! ${bindir}/env perl,' ${D}${sbindir}/ntptrace
    sed -i '/use/i use warnings;' ${D}${sbindir}/ntptrace
    sed -i '1s,#!.*perl,#! ${bindir}/env perl,' ${D}${sbindir}/ntp-wait
    sed -i '/use/i use warnings;' ${D}${sbindir}/ntp-wait
    sed -i '1s,#!.*perl -w,#! ${bindir}/env perl,' ${D}${sbindir}/calc_tickadj
    sed -i '/use/i use warnings;' ${D}${sbindir}/calc_tickadj

    install -d ${D}/${sysconfdir}/default
    install -m 0644 ${UNPACKDIR}/sntp ${D}${sysconfdir}/default/

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/ntpd.service ${D}${systemd_unitdir}/system/
    install -m 0644 ${UNPACKDIR}/sntp.service ${D}${systemd_unitdir}/system/

    install -d ${D}${systemd_unitdir}/ntp-units.d
    install -m 0644 ${UNPACKDIR}/ntpd.list ${D}${systemd_unitdir}/ntp-units.d/60-ntpd.list

    # Remove the empty libexecdir and bindir.
    rmdir --ignore-fail-on-non-empty ${D}${libexecdir}
    rmdir --ignore-fail-on-non-empty ${D}${bindir}
}

PACKAGES += "sntp ntpdc ntpq ${PN}-tickadj ${PN}-utils"

# ntp originally includes tickadj. It's split off for inclusion in small firmware images on platforms
# with wonky clocks (e.g. OpenSlug)
RDEPENDS:${PN} = "${PN}-tickadj"
# ntpd & sntp require libgcc for execution due to phtread_cancel/pthread_exit calls
RDEPENDS:${PN} += "libgcc"
RDEPENDS:sntp += "libgcc"
# Handle move from bin to utils package
RPROVIDES:${PN}-utils = "${PN}-bin"
RREPLACES:${PN}-utils = "${PN}-bin"
RCONFLICTS:${PN}-utils = "${PN}-bin"
# ntpdc and ntpq were split out of ntp-utils
RDEPENDS:${PN}-utils = "ntpdc ntpq \
                        perl-module-lib \
                        perl-module-exporter \
                        perl-module-carp \
                        perl-module-version \
                        perl-module-socket \
                        perl-module-getopt-long \
                       "

SYSTEMD_PACKAGES = "${PN} sntp"
SYSTEMD_SERVICE:${PN} = "ntpd.service"
SYSTEMD_SERVICE:sntp = "sntp.service"
SYSTEMD_AUTO_ENABLE:sntp = "disable"

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"

RSUGGESTS:${PN} = "iana-etc"

FILES:${PN} = "${sbindir}/ntpd.ntp ${sysconfdir}/ntp.conf ${sysconfdir}/init.d/ntpd \
    ${NTP_USER_HOME} \
    ${systemd_unitdir}/ntp-units.d/60-ntpd.list \
"
FILES:${PN}-tickadj = "${sbindir}/tickadj"
FILES:${PN}-utils = "${sbindir} ${datadir}/ntp/lib"
RDEPENDS:${PN}-utils += "perl"
FILES:sntp = "${sbindir}/sntp \
              ${sysconfdir}/default/sntp \
              ${systemd_unitdir}/system/sntp.service \
             "
FILES:ntpdc = "${sbindir}/ntpdc"
FILES:ntpq = "${sbindir}/ntpq"

CONFFILES:${PN} = "${sysconfdir}/ntp.conf"

INITSCRIPT_NAME = "ntpd"
# No dependencies, so just go in at the standard level (20)
INITSCRIPT_PARAMS = "defaults"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE:${PN} = "ntpd"
ALTERNATIVE_LINK_NAME[ntpd] = "${sbindir}/ntpd"
