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
LICENSE = "AGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb1e647870add0502f8f010b19de32af"
DEPENDS  = "libevent libpcre openldap virtual/libiconv zlib"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "https://cdn.zabbix.com/zabbix/sources/stable/7.0/${BPN}-${PV}.tar.gz \
    file://0001-Fix-configure.ac.patch \
    file://zabbix-agent.service \
"
SRC_URI[sha256sum] = "8d7755576f30b5d6099ff5e3574549fc248229c017f9753828565174125069f6"

inherit autotools-brokensep linux-kernel-base pkgconfig systemd useradd

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "zabbix-agent.service"
SYSTEMD_AUTO_ENABLE = "enable"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "-r zabbix"
USERADD_PARAM:${PN} = "-r -g zabbix -d /var/lib/zabbix \
    -s /sbin/nologin -c \"Zabbix Monitoring System\" zabbix \
"

KERNEL_VERSION = "${@get_kernelversion_headers('${STAGING_KERNEL_DIR}')}"

EXTRA_OECONF = " \
    --enable-dependency-tracking \
    --enable-agent \
    --enable-ipv6 \
    --with-net-snmp \
    --with-ldap=${STAGING_EXECPREFIXDIR} \
    --with-unixodbc \
    --with-ssh2 \
    --with-sqlite3 \
    --with-zlib \
    --with-libpthread \
    --with-libevent \
    --with-libpcre=${STAGING_EXECPREFIXDIR} \
    --with-iconv=${STAGING_EXECPREFIXDIR} \
"
CFLAGS:append = " -lldap -llber -pthread"

do_configure:prepend() {
    export KERNEL_VERSION="${KERNEL_VERSION}"
}

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${UNPACKDIR}/zabbix-agent.service ${D}${systemd_unitdir}/system/
        sed -i -e 's#@SBINDIR@#${sbindir}#g' ${D}${systemd_unitdir}/system/zabbix-agent.service
    fi
}

FILES:${PN} += "${libdir}"

RDEPENDS:${PN} = "logrotate"
