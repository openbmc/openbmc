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

LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=924958cefc9f7de3e0b818832b8a1cec"

# util-linux added to get libuuid
DEPENDS = "libpcre flex glib-2.0 openssl util-linux bison-native curl"

SRC_URI = "https://github.com/balabit/syslog-ng/releases/download/${BP}/${BP}.tar.gz \
           file://syslog-ng.conf.systemd \
           file://syslog-ng.conf.sysvinit \
           file://initscript \
           file://volatiles.03_syslog-ng \
           file://syslog-ng-tmp.conf \
           file://syslog-ng.service-the-syslog-ng-service.patch \
           file://0001-Fix-buildpaths-warning.patch \
           file://0001-macros-guard-ipv6-code-with-SYSLOG_NG_ENABLE_IPV6.patch \
"
SRC_URI:append:powerpc64le = " file://0001-plugin.c-workaround-powerpc64le-segfaults-error.patch"

SRC_URI[sha256sum] = "b69e3360dfb96a754a4e1cbead4daef37128b1152a23572356db4ab64a475d4f"

UPSTREAM_CHECK_URI = "https://github.com/balabit/syslog-ng/releases"

CVE_STATUS[CVE-2022-38725] = "cpe-incorrect: cve-check wrongly matches cpe:2.3:a:oneidentity:syslog-ng:*:*:*:*:premium:*:*:* < 7.0.32"

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
"

PACKAGECONFIG ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6 systemd', d)} \
"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,,"
PACKAGECONFIG[systemd] = "--enable-systemd --with-systemdsystemunitdir=${systemd_unitdir}/system/,--disable-systemd --without-systemdsystemunitdir,systemd,"
PACKAGECONFIG[linux-caps] = "--enable-linux-caps,--disable-linux-caps,libcap,"
PACKAGECONFIG[dbi] = "--enable-sql,--disable-sql,libdbi,"
PACKAGECONFIG[spoof-source] = "--enable-spoof-source --with-libnet=${STAGING_BINDIR_CROSS},--disable-spoof-source,libnet,"
PACKAGECONFIG[http] = "--enable-http,--disable-http,curl,"
PACKAGECONFIG[smtp] = "--enable-smtp --with-libesmtp=${STAGING_LIBDIR},--disable-smtp,libesmtp,"
PACKAGECONFIG[json] = "--enable-json,--disable-json,json-c,"
PACKAGECONFIG[tcp-wrapper] = "--enable-tcp-wrapper,--disable-tcp-wrapper,tcp-wrappers,"
PACKAGECONFIG[geoip] = "--enable-geoip,--disable-geoip,geoip,"
PACKAGECONFIG[native] = "--enable-native,--disable-native,,"

do_configure:prepend() {
	olddir=$(pwd)
	cd ${AUTOTOOLS_SCRIPT_PATH}

	ACLOCAL="$ACLOCAL" autoreconf -Wcross --verbose --install --force ${EXTRA_AUTORECONF} -I ${S}/m4 ${ACLOCALEXTRAPATH} || die "extra autoreconf execution failed."

	cd $olddir
}

do_install:append() {
    install -d ${D}${sysconfdir}/${BPN}
    install -d ${D}${sysconfdir}/init.d
    install -m 755 ${UNPACKDIR}/initscript ${D}${sysconfdir}/init.d/syslog

    install -d ${D}${sysconfdir}/default/volatiles/
    install -m 644 ${UNPACKDIR}/volatiles.03_syslog-ng ${D}${sysconfdir}/default/volatiles/03_syslog-ng
    install -d ${D}${sysconfdir}/tmpfiles.d/
    install -m 644 ${UNPACKDIR}/syslog-ng-tmp.conf ${D}${sysconfdir}/tmpfiles.d/syslog-ng.conf

    install -d ${D}${localstatedir}/lib/${BPN}
    # Remove /var/run as it is created on startup
    rm -rf ${D}${localstatedir}/run

    # it causes install conflict when multilib enabled
    # since python support is disabled, not deliver it
    rm -f ${D}${bindir}/syslog-ng-update-virtualenv

    # support for systemd
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -m 644 ${UNPACKDIR}/syslog-ng.conf.systemd ${D}${sysconfdir}/${BPN}/${BPN}.conf

        install -d ${D}${systemd_unitdir}/system/
        install -m 644 ${S}/contrib/systemd/${BPN}@.service ${D}${systemd_unitdir}/system/${BPN}@.service
        install -m 644 ${S}/contrib/systemd/${BPN}@default ${D}${sysconfdir}/default/${BPN}@default

        sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_unitdir}/system/${BPN}@.service ${D}${sysconfdir}/default/${BPN}@default
        sed -i -e 's,@LOCALSTATEDIR@,${localstatedir},g' ${D}${systemd_unitdir}/system/${BPN}@.service ${D}${sysconfdir}/default/${BPN}@default
        sed -i -e 's,@BASEBINDIR@,${base_bindir},g' ${D}${systemd_unitdir}/system/${BPN}@.service ${D}${sysconfdir}/default/${BPN}@default

        install -d ${D}${systemd_unitdir}/system/multi-user.target.wants
        ln -sf ../${BPN}@.service ${D}${systemd_unitdir}/system/multi-user.target.wants/${BPN}@default.service
    else
        install -m 644 ${UNPACKDIR}/syslog-ng.conf.sysvinit ${D}${sysconfdir}/${BPN}/${BPN}.conf
    fi

    oe_multilib_header syslog-ng/syslog-ng-config.h
}

FILES:${PN} += "${datadir}/include/scl/ ${datadir}/xsd ${datadir}/tools ${systemd_unitdir}/system/multi-user.target.wants/*"
RDEPENDS:${PN} += "gawk ${@bb.utils.contains('PACKAGECONFIG','json','${PN}-jconf','',d)}"

FILES:${PN}-jconf += " \
${datadir}/${BPN}/include/scl/cim \
${datadir}/${BPN}/include/scl/elasticsearch \
${datadir}/${BPN}/include/scl/ewmm \
${datadir}/${BPN}/include/scl/graylog2 \
${datadir}/${BPN}/include/scl/loggly \
${datadir}/${BPN}/include/scl/logmatic \
"

# This overcomes the syslog-ng rdepends on syslog-ng-dev QA Error
PACKAGES =+ "${PN}-jconf ${PN}-libs ${PN}-libs-dev"
RPROVIDES:${PN}-dbg += "${PN}-libs-dbg"
FILES:${PN}-libs = "${libdir}/${BPN}/*.so ${libdir}/libsyslog-ng-*.so*"
FILES:${PN}-libs-dev = "${libdir}/${BPN}/lib*.la"
FILES:${PN}-staticdev += "${libdir}/${BPN}/libtest/*.a"
FILES:${PN} += "${systemd_unitdir}/system/*.service"
INSANE_SKIP:${PN}-libs = "dev-so"
RDEPENDS:${PN} += "${PN}-libs"

CONFFILES:${PN} = "${sysconfdir}/${BPN}.conf ${sysconfdir}/scl.conf"

RCONFLICTS:${PN} = "busybox-syslog sysklogd rsyslog"
RCONFLICTS:${PN}-libs = "busybox-syslog sysklogd rsyslog"

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"
SYSTEMD_SERVICE:${PN} = "${BPN}@.service"

INITSCRIPT_NAME = "syslog"
INITSCRIPT_PARAMS = "start 20 2 3 4 5 . stop 90 0 1 6 ."

# Fails only with 32bit MACHINEs
# http://errors.yoctoproject.org/Errors/Details/766956/
# syslog-ng-4.6.0/modules/secure-logging/slog.c:937:63: error: passing argument 4 of 'g_io_channel_write_chars' from incompatible pointer type [-Wincompatible-pointer-types]
# syslog-ng-4.6.0/modules/secure-logging/slog.c:955:99: error: passing argument 5 of 'cmac' from incompatible pointer type [-Wincompatible-pointer-types]
# syslog-ng-4.6.0/modules/secure-logging/slog.c:959:74: error: passing argument 4 of 'g_io_channel_write_chars' from incompatible pointer type [-Wincompatible-pointer-types]
# syslog-ng-4.6.0/modules/secure-logging/slog.c:975:107: error: passing argument 4 of 'g_io_channel_write_chars' from incompatible pointer type [-Wincompatible-pointer-types]
CFLAGS += "-Wno-error=incompatible-pointer-types"
