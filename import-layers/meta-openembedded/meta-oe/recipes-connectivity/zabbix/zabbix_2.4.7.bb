SUMMARY = "Open-source monitoring solution for your IT infrastructure"
DESCRIPTION = "\
ZABBIX is software that monitors numerous parameters of a network and the \
health and integrity of servers. ZABBIX uses a flexible notification \
mechanism that allows users to configure e-mail based alerts for virtually \
any event. This allows a fast reaction to server problems. ZABBIX offers \
excellent reporting and data visualisation features based on the stored \
data. This makes ZABBIX ideal for capacity planning. \
\
ZABBIX supports both polling and trapping. All ZABBIX reports and \
statistics, as well as configuration parameters are accessed through a \
web-based front end. A web-based front end ensures that the status of \
your network and the health of your servers can be assessed from any \
location. Properly configured, ZABBIX can play an important role in \
monitoring IT infrastructure. This is equally true for small \
organisations with a few servers and for large companies with a \
multitude of servers."
HOMEPAGE = "http://www.zabbix.com/"
SECTION = "Applications/Internet"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=300e938ad303147fede2294ed78fe02e"
DEPENDS  = "openldap virtual/libiconv"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "http://jaist.dl.sourceforge.net/project/zabbix/ZABBIX%20Latest%20Stable/${PV}/${BPN}-${PV}.tar.gz \
           file://0001-Fix-configure.ac.patch \
           file://zabbix-agent.service"

SRC_URI[md5sum] = "9f8aeb11d8415585f41c3f2f22566b78"
SRC_URI[sha256sum] = "d2c47b8f5b9b91f18010d54c45de55845d979014a8b3fe4bef64e0b08f8b00da"

inherit autotools-brokensep linux-kernel-base pkgconfig systemd useradd

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "zabbix-agent.service"
SYSTEMD_AUTO_ENABLE = "enable"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "-r zabbix"
USERADD_PARAM_${PN} = "-r -g zabbix -d /var/lib/zabbix \
                       -s /sbin/nologin -c \"Zabbix Monitoring System\" zabbix"

KERNEL_VERSION = "${@get_kernelversion_headers('${STAGING_KERNEL_DIR}')}"

EXTRA_OECONF = '--enable-dependency-tracking \
	        --enable-agent \
	        --enable-ipv6 \
	        --with-net-snmp \
	        --with-ldap=${STAGING_EXECPREFIXDIR} \
	        --with-jabber \
	        --with-unixodbc \
	        --with-ssh2 \
	        --with-sqlite3 \
	        '
CFLAGS_append += "-lldap -llber"

do_configure_prepend() {
    export KERNEL_VERSION="${KERNEL_VERSION}"
}

do_install_append() {
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/zabbix-agent.service ${D}${systemd_unitdir}/system/
        sed -i -e 's#@SBINDIR@#${sbindir}#g' ${D}${systemd_unitdir}/system/zabbix-agent.service
    fi
}

FILES_${PN} += "${libdir}"

RDEPENDS_${PN} = "logrotate"
