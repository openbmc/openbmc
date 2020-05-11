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
DEPENDS = "libpcre flex glib-2.0 openssl util-linux"

# We expect the recipe to add anything specific for that version to these defaults.
#
SRC_URI = "https://github.com/balabit/syslog-ng/releases/download/${BP}/${BP}.tar.gz \
           file://syslog-ng.conf.systemd \
           file://syslog-ng.conf.sysvinit \
           file://initscript \
           file://volatiles.03_syslog-ng \
           file://configure.ac-add-option-enable-thread-tls-to-manage-.patch \
"

UPSTREAM_CHECK_URI = "https://github.com/balabit/syslog-ng/releases"

inherit autotools gettext systemd pkgconfig update-rc.d multilib_header

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
CONFIG_TLS_arm = "${@oe.utils.conditional( "DEBUG_BUILD", "1", " --disable-thread-tls", " --enable-thread-tls", d )}"

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

do_install_append() {
    install -d ${D}${sysconfdir}/${BPN}
    install -d ${D}${sysconfdir}/init.d
    install -m 755 ${WORKDIR}/initscript ${D}${sysconfdir}/init.d/syslog

    install -d ${D}${sysconfdir}/default/volatiles/
    install -m 644 ${WORKDIR}/volatiles.03_syslog-ng ${D}${sysconfdir}/default/volatiles/03_syslog-ng
    install -d ${D}${sysconfdir}/tmpfiles.d/
    install -m 644 ${WORKDIR}/syslog-ng-tmp.conf ${D}${sysconfdir}/tmpfiles.d/syslog-ng.conf

    install -d ${D}${localstatedir}/lib/${BPN}
    # Remove /var/run as it is created on startup
    rm -rf ${D}${localstatedir}/run

    # support for systemd
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -m 644 ${WORKDIR}/syslog-ng.conf.systemd ${D}${sysconfdir}/${BPN}/${BPN}.conf

        install -d ${D}${systemd_unitdir}/system/
        install -m 644 ${S}/contrib/systemd/${BPN}@.service ${D}${systemd_unitdir}/system/${BPN}@.service
        install -m 644 ${S}/contrib/systemd/${BPN}@default ${D}${sysconfdir}/default/${BPN}@default

        sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_unitdir}/system/${BPN}@.service ${D}${sysconfdir}/default/${BPN}@default
        sed -i -e 's,@LOCALSTATEDIR@,${localstatedir},g' ${D}${systemd_unitdir}/system/${BPN}@.service ${D}${sysconfdir}/default/${BPN}@default
        sed -i -e 's,@BASEBINDIR@,${base_bindir},g' ${D}${systemd_unitdir}/system/${BPN}@.service ${D}${sysconfdir}/default/${BPN}@default

        install -d ${D}${systemd_unitdir}/system/multi-user.target.wants
        ln -sf ../${BPN}@.service ${D}${systemd_unitdir}/system/multi-user.target.wants/${BPN}@default.service
    else
        install -m 644 ${WORKDIR}/syslog-ng.conf.sysvinit ${D}${sysconfdir}/${BPN}/${BPN}.conf
    fi

    oe_multilib_header syslog-ng/syslog-ng-config.h
}

FILES_${PN} += "${datadir}/include/scl/ ${datadir}/xsd ${datadir}/tools ${systemd_unitdir}/system/multi-user.target.wants/*"
RDEPENDS_${PN} += "gawk ${@bb.utils.contains('PACKAGECONFIG','json','${PN}-jconf','',d)}"

FILES_${PN}-jconf += " \
${datadir}/${BPN}/include/scl/cim \
${datadir}/${BPN}/include/scl/elasticsearch \
${datadir}/${BPN}/include/scl/ewmm \
${datadir}/${BPN}/include/scl/graylog2 \
${datadir}/${BPN}/include/scl/loggly \
${datadir}/${BPN}/include/scl/logmatic \
"

# This overcomes the syslog-ng rdepends on syslog-ng-dev QA Error
PACKAGES =+ "${PN}-jconf ${PN}-libs ${PN}-libs-dev"
RPROVIDES_${PN}-dbg += "${PN}-libs-dbg"
FILES_${PN}-libs = "${libdir}/${BPN}/*.so ${libdir}/libsyslog-ng-*.so*"
FILES_${PN}-libs-dev = "${libdir}/${BPN}/lib*.la"
FILES_${PN}-staticdev += "${libdir}/${BPN}/libtest/*.a"
FILES_${PN} += "${systemd_unitdir}/system/*.service"
INSANE_SKIP_${PN}-libs = "dev-so"
RDEPENDS_${PN} += "${PN}-libs"

CONFFILES_${PN} = "${sysconfdir}/${BPN}.conf ${sysconfdir}/scl.conf"

# syslog initscript is handled explicitly because order of
# update-rc.d and update-alternatives is important
RDEPENDS_${PN} += " ${@oe.utils.conditional("ONLINE_PACKAGE_MANAGEMENT", "none", "", "update-rc.d", d)}"

RCONFLICTS_${PN} = "busybox-syslog sysklogd rsyslog"
RCONFLICTS_${PN}-libs = "busybox-syslog sysklogd rsyslog"

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "${BPN}@.service"

INITSCRIPT_NAME = "syslog"
INITSCRIPT_PARAMS = "start 20 2 3 4 5 . stop 90 0 1 6 ."
