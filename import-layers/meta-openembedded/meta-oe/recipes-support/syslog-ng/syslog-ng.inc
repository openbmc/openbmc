SUMMARY = "Alternative system logger daemon"
DESCRIPTION = "syslog-ng, as the name shows, is a syslogd replacement, \
but with new functionality for the new generation. The original syslogd \
allows messages only to be sorted based on priority/facility pairs; \
syslog-ng adds the possibility to filter based on message contents using \
regular expressions. The new configuration scheme is intuitive and powerful. \
Forwarding logs over TCP and remembering all forwarding hops makes it \
ideal for firewalled environments. \
"
HOMEPAGE = "http://www.balabit.com/network-security/syslog-ng/opensource-logging-system"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=e0e8658d9be248f01b7933df24dc1408"

# util-linux added to get libuuid
DEPENDS = "libpcre flex eventlog glib-2.0 util-linux"

SRC_URI = "http://www.balabit.com/downloads/files/syslog-ng/sources/${PV}/source/${BPN}_${PV}.tar.gz \
           file://syslog-ng.conf \
           file://initscript \
           file://volatiles.03_syslog-ng \
           file://configure.ac-add-option-enable-thread-tls-to-manage-.patch \
"

inherit autotools systemd pkgconfig update-alternatives update-rc.d

EXTRA_OECONF = " \
    --enable-dynamic-linking \
    --disable-sub-streams \
    --disable-pacct \
    --localstatedir=${localstatedir}/lib/${BPN} \
    --sysconfdir=${sysconfdir}/${BPN} \
    --with-module-dir=${libdir}/${BPN} \
    --with-sysroot=${STAGING_DIR_HOST} \
    --with-libmongo-client=no --disable-mongodb \
    --with-librabbitmq-client=no \
    ${CONFIG_TLS} \
"

CONFIG_TLS = "--enable-thread-tls"
CONFIG_TLS_arm = "${@base_conditional( "DEBUG_BUILD", "1", " --disable-thread-tls", " --enable-thread-tls", d )}"

PACKAGECONFIG ??= "openssl \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'ipv6', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)} \
"
PACKAGECONFIG[openssl] = "--enable-ssl,--disable-ssl,openssl,"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,,"
PACKAGECONFIG[systemd] = "--enable-systemd --with-systemdsystemunitdir=${systemd_unitdir}/system/,--disable-systemd --without-systemdsystemunitdir,systemd,"
PACKAGECONFIG[linux-caps] = "--enable-linux-caps,--disable-linux-caps,libcap,"
PACKAGECONFIG[pcre] = "--enable-pcre,--disable-pcre,libpcre,"
PACKAGECONFIG[dbi] = "--enable-sql,--disable-sql,libdbi,"
PACKAGECONFIG[libnet] = "--enable-libnet --with-libnet=${STAGING_BINDIR_CROSS},--disable-libnet,libnet,"
PACKAGECONFIG[smtp] = "--enable-smtp --with-libesmtp=${STAGING_LIBDIR},--disable-smtp,libesmtp,"
PACKAGECONFIG[json] = "--enable-json,--disable-json,json-c,"
PACKAGECONFIG[tcp-wrapper] = "--enable-tcp-wrapper,--disable-tcp-wrapper,tcp-wrappers,"
PACKAGECONFIG[geoip] = "--enable-geoip,--disable-geoip,geoip,"

do_configure_prepend() {
    eval "${@bb.utils.contains('DISTRO_FEATURES', 'largefile', '', 'sed -i -e "s/-D_LARGEFILE_SOURCE//" -e "s/-D_FILE_OFFSET_BITS=64//" ${S}/configure.in', d)}"
}

do_install_append() {
    install -d ${D}/${sysconfdir}/${BPN}
    install ${WORKDIR}/syslog-ng.conf ${D}${sysconfdir}/${BPN}/${BPN}.conf
    install -d ${D}/${sysconfdir}/init.d
    install -m 755 ${WORKDIR}/initscript ${D}/${sysconfdir}/init.d/syslog.${BPN}
    install -d ${D}/${sysconfdir}/default/volatiles/
    install -m 755 ${WORKDIR}/volatiles.03_syslog-ng ${D}/${sysconfdir}/default/volatiles/03_syslog-ng
    install -d ${D}/${localstatedir}/lib/${BPN}
    # Remove /var/run as it is created on startup
    rm -rf ${D}${localstatedir}/run
    sed -i -e 's,@SBINDIR@,${sbindir},g' ${S}/contrib/systemd/*.service
    sed -i -e 's,@LOCALSTATEDIR@,${localstatedir},g' ${S}/contrib/systemd/*.service
    sed -i -e 's,@BASEBINDIR@,${base_bindir},g' ${S}/contrib/systemd/*.service
}

FILES_${PN} += "${datadir}/include/scl/ ${datadir}/xsd ${datadir}/tools"
RDEPENDS_${PN} += "gawk"

# This overcomes the syslog-ng rdepends on syslog-ng-dev QA Error
PACKAGES =+ "${PN}-libs ${PN}-libs-dev ${PN}-libs-dbg"
FILES_${PN}-libs = "${libdir}/${BPN}/*.so ${libdir}/libsyslog-ng-*.so*"
FILES_${PN}-libs-dev = "${libdir}/${BPN}/lib*.la"
FILES_${PN}-libs-dbg = "${libdir}/${BPN}/.debug"
FILES_${PN}-staticdev = "${libdir}/${BPN}/libtest/*.a"
INSANE_SKIP_${PN}-libs = "dev-so"
RDEPENDS_${PN} += "${PN}-libs"

CONFFILES_${PN} = "${sysconfdir}/${BPN}.conf ${sysconfdir}/scl.conf"

# syslog initscript is handled explicitly because order of
# update-rc.d and update-alternatives is important
RDEPENDS_${PN} += " ${@base_conditional("ONLINE_PACKAGE_MANAGEMENT", "none", "", "update-rc.d", d)}"

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "${BPN}.service"

# no syslog-init for systemd
python () {
    if bb.utils.contains('DISTRO_FEATURES', 'sysvinit', True, False, d):
        pn = d.getVar('PN', True)
        sysconfdir = d.getVar('sysconfdir', True)
        d.appendVar('ALTERNATIVE_%s' % (pn), ' syslog-init')
        d.setVarFlag('ALTERNATIVE_PRIORITY', 'syslog-init', '200')
        d.setVarFlag('ALTERNATIVE_LINK_NAME', 'syslog-init', '%s/init.d/syslog' % (sysconfdir))

    if bb.utils.contains('DISTRO_FEATURES', 'systemd', True, False, d):
        pn = d.getVar('PN', True)
        d.appendVar('ALTERNATIVE_%s' % (pn), ' syslog-service')
        d.setVarFlag('ALTERNATIVE_LINK_NAME', 'syslog-service', '%s/systemd/system/syslog.service' % (d.getVar('sysconfdir', True)))
        d.setVarFlag('ALTERNATIVE_TARGET', 'syslog-service', '%s/system/${BPN}.service' % (d.getVar('systemd_unitdir', True)))
}

INITSCRIPT_NAME = "syslog"
INITSCRIPT_PARAMS = "start 20 2 3 4 5 . stop 90 0 1 6 ."
