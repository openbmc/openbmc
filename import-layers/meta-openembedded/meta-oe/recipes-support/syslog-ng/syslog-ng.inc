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
LIC_FILES_CHKSUM = "file://COPYING;md5=24c0c5cb2c83d9f2ab725481e4df5240"

# util-linux added to get libuuid
DEPENDS = "libpcre flex eventlog glib-2.0 openssl util-linux"

SRC_URI = "https://github.com/balabit/syslog-ng/releases/download/${BP}/${BP}.tar.gz \
           file://syslog-ng.conf \
           file://initscript \
           file://volatiles.03_syslog-ng \
           file://configure.ac-add-option-enable-thread-tls-to-manage-.patch \
"

inherit autotools gettext systemd pkgconfig update-rc.d

EXTRA_OECONF = " \
    --enable-dynamic-linking \
    --disable-sub-streams \
    --disable-pacct \
    --localstatedir=${localstatedir}/lib/${BPN} \
    --sysconfdir=${sysconfdir}/${BPN} \
    --with-module-dir=${libdir}/${BPN} \
    --with-sysroot=${STAGING_DIR_HOST} \
    --without-mongoc --disable-mongodb \
    --with-librabbitmq-client=no \
    --disable-python \
    --disable-java --disable-java-modules \
    --with-pidfile-dir=${localstatedir}/run/${BPN} \
    ${CONFIG_TLS} \
"

CONFIG_TLS = "--enable-thread-tls"
CONFIG_TLS_arm = "${@base_conditional( "DEBUG_BUILD", "1", " --disable-thread-tls", " --enable-thread-tls", d )}"

PACKAGECONFIG ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6 systemd', d)} \
"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,,"
PACKAGECONFIG[systemd] = "--enable-systemd --with-systemdsystemunitdir=${systemd_unitdir}/system/,--disable-systemd --without-systemdsystemunitdir,systemd,"
PACKAGECONFIG[linux-caps] = "--enable-linux-caps,--disable-linux-caps,libcap,"
PACKAGECONFIG[dbi] = "--enable-sql,--disable-sql,libdbi,"
PACKAGECONFIG[libnet] = "--enable-libnet --with-libnet=${STAGING_BINDIR_CROSS},--disable-libnet,libnet,"
PACKAGECONFIG[http] = "--enable-http,--disable-http,curl,"
PACKAGECONFIG[smtp] = "--enable-smtp --with-libesmtp=${STAGING_LIBDIR},--disable-smtp,libesmtp,"
PACKAGECONFIG[json] = "--enable-json,--disable-json,json-c,"
PACKAGECONFIG[tcp-wrapper] = "--enable-tcp-wrapper,--disable-tcp-wrapper,tcp-wrappers,"
PACKAGECONFIG[geoip] = "--enable-geoip,--disable-geoip,geoip,"
PACKAGECONFIG[native] = "--enable-native,--disable-native,,"

do_configure_prepend() {
	olddir=$(pwd)
	cd ${AUTOTOOLS_SCRIPT_PATH}

	ACLOCAL="$ACLOCAL" autoreconf -Wcross --verbose --install --force ${EXTRA_AUTORECONF} -I ${S}/m4 ${ACLOCALEXTRAPATH} || die "extra autoreconf execution failed."

	cd $olddir
}

do_install_prepend() {
    sed -i -e 's,@SBINDIR@,${sbindir},g' ${S}/contrib/systemd/*.service
    sed -i -e 's,@LOCALSTATEDIR@,${localstatedir},g' ${S}/contrib/systemd/*.service
    sed -i -e 's,@BASEBINDIR@,${base_bindir},g' ${S}/contrib/systemd/*.service
}

do_install_append() {
    install -d ${D}/${sysconfdir}/${BPN}
    install ${WORKDIR}/syslog-ng.conf ${D}${sysconfdir}/${BPN}/${BPN}.conf
    install -d ${D}/${sysconfdir}/init.d
    install -m 755 ${WORKDIR}/initscript ${D}/${sysconfdir}/init.d/syslog
    install -d ${D}/${sysconfdir}/default/volatiles/
    install -m 755 ${WORKDIR}/volatiles.03_syslog-ng ${D}/${sysconfdir}/default/volatiles/03_syslog-ng
    install -d ${D}/${localstatedir}/lib/${BPN}
    # Remove /var/run as it is created on startup
    rm -rf ${D}${localstatedir}/run
}

FILES_${PN} += "${datadir}/include/scl/ ${datadir}/xsd ${datadir}/tools"
RDEPENDS_${PN} += "gawk"

# This overcomes the syslog-ng rdepends on syslog-ng-dev QA Error
PACKAGES =+ "${PN}-libs ${PN}-libs-dev ${PN}-libs-dbg"
FILES_${PN}-libs = "${libdir}/${BPN}/*.so ${libdir}/libsyslog-ng-*.so*"
FILES_${PN}-libs-dev = "${libdir}/${BPN}/lib*.la"
FILES_${PN}-libs-dbg = "${libdir}/${BPN}/.debug"
FILES_${PN}-staticdev += "${libdir}/${BPN}/libtest/*.a"
INSANE_SKIP_${PN}-libs = "dev-so"
RDEPENDS_${PN} += "${PN}-libs"

CONFFILES_${PN} = "${sysconfdir}/${BPN}.conf ${sysconfdir}/scl.conf"

# syslog initscript is handled explicitly because order of
# update-rc.d and update-alternatives is important
RDEPENDS_${PN} += " ${@base_conditional("ONLINE_PACKAGE_MANAGEMENT", "none", "", "update-rc.d", d)}"

RCONFLICTS_${PN} = "busybox-syslog sysklogd rsyslog"

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "${BPN}.service"

INITSCRIPT_NAME = "syslog"
INITSCRIPT_PARAMS = "start 20 2 3 4 5 . stop 90 0 1 6 ."
