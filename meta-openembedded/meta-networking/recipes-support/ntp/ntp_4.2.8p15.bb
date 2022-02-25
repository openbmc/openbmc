SUMMARY = "Network Time Protocol daemon and utilities"
DESCRIPTION = "The Network Time Protocol (NTP) is used to \
synchronize the time of a computer client or server to \
another server or reference time source, such as a radio \
or satellite receiver or modem."
HOMEPAGE = "http://support.ntp.org"
SECTION = "net"
LICENSE = "NTP"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=4190b39435611e92a4da74e682623f19"

DEPENDS = "libevent"

SRC_URI = "http://www.eecis.udel.edu/~ntp/ntp_spool/ntp4/ntp-4.2/ntp-${PV}.tar.gz \
           file://ntp-4.2.4_p6-nano.patch \
           file://reproducibility-fixed-path-to-posix-shell.patch \
           file://0001-libntp-Do-not-use-PTHREAD_STACK_MIN-on-glibc.patch \
           file://0001-test-Fix-build-with-new-compiler-defaults-to-fno-com.patch \
           file://ntpd \
           file://ntp.conf \
           file://ntpdate \
           file://ntpdate.default \
           file://ntpdate.service \
           file://ntpd.service \
           file://sntp.service \
           file://sntp \
           file://ntpd.list \
"

SRC_URI[sha256sum] = "f65840deab68614d5d7ceb2d0bb9304ff70dcdedd09abb79754a87536b849c19"

# CVE-2016-9312 is only for windows.
CVE_CHECK_IGNORE += "CVE-2016-9312"

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
    install -m 644 ${WORKDIR}/ntp.conf ${D}${sysconfdir}
    install -m 755 ${WORKDIR}/ntpd ${D}${sysconfdir}/init.d
    install -d ${D}${bindir}
    install -m 755 ${WORKDIR}/ntpdate ${D}${bindir}/ntpdate-sync

    install -m 755 -d ${D}${NTP_USER_HOME}
    chown ntp:ntp ${D}${NTP_USER_HOME}

    # Fix hardcoded paths in scripts
    sed -i 's!/usr/sbin/!${sbindir}/!g' ${D}${sysconfdir}/init.d/ntpd ${D}${bindir}/ntpdate-sync
    sed -i 's!/usr/bin/!${bindir}/!g' ${D}${sysconfdir}/init.d/ntpd ${D}${bindir}/ntpdate-sync
    sed -i 's!/etc/!${sysconfdir}/!g' ${D}${sysconfdir}/init.d/ntpd ${D}${bindir}/ntpdate-sync
    sed -i 's!/var/!${localstatedir}/!g' ${D}${sysconfdir}/init.d/ntpd ${D}${bindir}/ntpdate-sync
    sed -i 's!^PATH=.*!PATH=${base_sbindir}:${base_bindir}:${sbindir}:${bindir}!' ${D}${bindir}/ntpdate-sync
    sed -i '1s,#!.*perl -w,#! ${bindir}/env perl,' ${D}${sbindir}/ntptrace
    sed -i '/use/i use warnings;' ${D}${sbindir}/ntptrace
    sed -i '1s,#!.*perl,#! ${bindir}/env perl,' ${D}${sbindir}/ntp-wait
    sed -i '/use/i use warnings;' ${D}${sbindir}/ntp-wait
    sed -i '1s,#!.*perl -w,#! ${bindir}/env perl,' ${D}${sbindir}/calc_tickadj
    sed -i '/use/i use warnings;' ${D}${sbindir}/calc_tickadj

    install -d ${D}/${sysconfdir}/default
    install -m 644 ${WORKDIR}/ntpdate.default ${D}${sysconfdir}/default/ntpdate
    install -m 0644 ${WORKDIR}/sntp ${D}${sysconfdir}/default/

    install -d ${D}/${sysconfdir}/network/if-up.d
    ln -s ${bindir}/ntpdate-sync ${D}/${sysconfdir}/network/if-up.d

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/ntpdate.service ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/ntpd.service ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/sntp.service ${D}${systemd_unitdir}/system/

    install -d ${D}${systemd_unitdir}/ntp-units.d
    install -m 0644 ${WORKDIR}/ntpd.list ${D}${systemd_unitdir}/ntp-units.d/60-ntpd.list

    # Remove an empty libexecdir.
    rmdir --ignore-fail-on-non-empty ${D}${libexecdir}
}

PACKAGES += "ntpdate sntp ntpdc ntpq ${PN}-tickadj ${PN}-utils"
# NOTE: you don't need ntpdate, use "ntpd -q -g -x"

# ntp originally includes tickadj. It's split off for inclusion in small firmware images on platforms
# with wonky clocks (e.g. OpenSlug)
RDEPENDS:${PN} = "${PN}-tickadj"
# ntpd require libgcc for execution
RDEPENDS:${PN} += "libgcc"
# Handle move from bin to utils package
RPROVIDES:${PN}-utils = "${PN}-bin"
RREPLACES:${PN}-utils = "${PN}-bin"
RCONFLICTS:${PN}-utils = "${PN}-bin"
# ntpdc and ntpq were split out of ntp-utils
RDEPENDS:${PN}-utils = "ntpdc ntpq"

SYSTEMD_PACKAGES = "${PN} ntpdate sntp"
SYSTEMD_SERVICE:${PN} = "ntpd.service"
SYSTEMD_SERVICE:ntpdate = "ntpdate.service"
SYSTEMD_SERVICE:sntp = "sntp.service"
SYSTEMD_AUTO_ENABLE:sntp = "disable"

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"

RPROVIDES:ntpdate += "ntpdate-systemd"
RREPLACES:ntpdate += "ntpdate-systemd"
RCONFLICTS:ntpdate += "ntpdate-systemd"

RSUGGESTS:${PN} = "iana-etc"

FILES:${PN} = "${sbindir}/ntpd.ntp ${sysconfdir}/ntp.conf ${sysconfdir}/init.d/ntpd \
    ${NTP_USER_HOME} \
    ${systemd_unitdir}/ntp-units.d/60-ntpd.list \
"
FILES:${PN}-tickadj = "${sbindir}/tickadj"
FILES:${PN}-utils = "${sbindir} ${datadir}/ntp/lib"
RDEPENDS:${PN}-utils += "perl"
FILES:ntpdate = "${sbindir}/ntpdate \
    ${sysconfdir}/network/if-up.d/ntpdate-sync \
    ${bindir}/ntpdate-sync \
    ${sysconfdir}/default/ntpdate \
    ${systemd_unitdir}/system/ntpdate.service \
"
FILES:sntp = "${sbindir}/sntp \
              ${sysconfdir}/default/sntp \
              ${systemd_unitdir}/system/sntp.service \
             "
FILES:ntpdc = "${sbindir}/ntpdc"
FILES:ntpq = "${sbindir}/ntpq"

CONFFILES:${PN} = "${sysconfdir}/ntp.conf"
CONFFILES:ntpdate = "${sysconfdir}/default/ntpdate"

INITSCRIPT_NAME = "ntpd"
# No dependencies, so just go in at the standard level (20)
INITSCRIPT_PARAMS = "defaults"

pkg_postinst:ntpdate() {
    if ! grep -q -s ntpdate $D/var/spool/cron/root; then
        echo "adding crontab"
        test -d $D/var/spool/cron || mkdir -p $D/var/spool/cron
        echo "30 * * * *    ${bindir}/ntpdate-sync silent" >> $D/var/spool/cron/root
    fi
}

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE:${PN} = "ntpd"
ALTERNATIVE_LINK_NAME[ntpd] = "${sbindir}/ntpd"
