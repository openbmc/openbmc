SECTION = "console/network"
SUMMARY = "Internet Software Consortium DHCP package"
DESCRIPTION = "DHCP (Dynamic Host Configuration Protocol) is a protocol \
which allows individual devices on an IP network to get their own \
network configuration information from a server.  DHCP helps make it \
easier to administer devices."

HOMEPAGE = "http://www.isc.org/"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;beginline=4;md5=c5c64d696107f84b56fe337d14da1753"

DEPENDS = "openssl bind"

SRC_URI = "ftp://ftp.isc.org/isc/dhcp/${PV}/dhcp-${PV}.tar.gz \
           file://define-macro-_PATH_DHCPD_CONF-and-_PATH_DHCLIENT_CON.patch \
           file://init-relay file://default-relay \
           file://init-server file://default-server \
           file://dhclient.conf file://dhcpd.conf \
           file://dhcpd.service file://dhcrelay.service \
           file://dhcpd6.service \
           file://search-for-libxml2.patch "

UPSTREAM_CHECK_URI = "ftp://ftp.isc.org/isc/dhcp/"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+\.\d+\.(\d+?))/"

inherit autotools systemd useradd update-rc.d

USERADD_PACKAGES = "${PN}-server"
USERADD_PARAM_${PN}-server = "--system --no-create-home --home-dir /var/run/${PN} --shell /bin/false --user-group ${PN}"

SYSTEMD_PACKAGES = "${PN}-server ${PN}-relay"
SYSTEMD_SERVICE_${PN}-server = "dhcpd.service dhcpd6.service"
SYSTEMD_AUTO_ENABLE_${PN}-server = "disable"

SYSTEMD_SERVICE_${PN}-relay = "dhcrelay.service"
SYSTEMD_AUTO_ENABLE_${PN}-relay = "disable"

INITSCRIPT_PACKAGES = "dhcp-server"
INITSCRIPT_NAME_dhcp-server = "dhcp-server"
INITSCRIPT_PARAMS_dhcp-server = "defaults"

TARGET_CFLAGS += "-D_GNU_SOURCE"
EXTRA_OECONF = "--with-srv-lease-file=${localstatedir}/lib/dhcp/dhcpd.leases \
                --with-srv6-lease-file=${localstatedir}/lib/dhcp/dhcpd6.leases \
                --with-cli-lease-file=${localstatedir}/lib/dhcp/dhclient.leases \
                --with-cli6-lease-file=${localstatedir}/lib/dhcp/dhclient6.leases \
                --with-libbind=${STAGING_LIBDIR}/ \
		--enable-paranoia \
               "

do_install_append () {
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/dhcp
	install -m 0755 ${WORKDIR}/init-relay ${D}${sysconfdir}/init.d/dhcp-relay
	install -m 0644 ${WORKDIR}/default-relay ${D}${sysconfdir}/default/dhcp-relay
	install -m 0755 ${WORKDIR}/init-server ${D}${sysconfdir}/init.d/dhcp-server
	install -m 0644 ${WORKDIR}/default-server ${D}${sysconfdir}/default/dhcp-server

	rm -f ${D}${sysconfdir}/dhclient.conf*
	rm -f ${D}${sysconfdir}/dhcpd.conf*
	install -m 0644 ${WORKDIR}/dhclient.conf ${D}${sysconfdir}/dhcp/dhclient.conf
	install -m 0644 ${WORKDIR}/dhcpd.conf ${D}${sysconfdir}/dhcp/dhcpd.conf

	install -d ${D}${base_sbindir}/
	if [ "${sbindir}" != "${base_sbindir}" ]; then
		mv ${D}${sbindir}/dhclient ${D}${base_sbindir}/
	fi
	install -m 0755 ${S}/client/scripts/linux ${D}${base_sbindir}/dhclient-script

	# Install systemd unit files
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/dhcpd.service ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/dhcpd6.service ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/dhcrelay.service ${D}${systemd_unitdir}/system
	sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_unitdir}/system/dhcpd*.service ${D}${systemd_unitdir}/system/dhcrelay.service
	sed -i -e 's,@SYSCONFDIR@,${sysconfdir},g' ${D}${systemd_unitdir}/system/dhcpd*.service
	sed -i -e 's,@base_bindir@,${base_bindir},g' ${D}${systemd_unitdir}/system/dhcpd*.service
	sed -i -e 's,@localstatedir@,${localstatedir},g' ${D}${systemd_unitdir}/system/dhcpd*.service
}

PACKAGES += "dhcp-server dhcp-server-config dhcp-client dhcp-relay dhcp-omshell"

FILES_${PN} = ""
RDEPENDS_${PN}-dev = ""
RDEPENDS_${PN}-staticdev = ""

FILES_${PN}-server = "${sbindir}/dhcpd ${sysconfdir}/init.d/dhcp-server"
RRECOMMENDS_${PN}-server = "dhcp-server-config"

FILES_${PN}-server-config = "${sysconfdir}/default/dhcp-server ${sysconfdir}/dhcp/dhcpd.conf"

FILES_${PN}-relay = "${sbindir}/dhcrelay ${sysconfdir}/init.d/dhcp-relay ${sysconfdir}/default/dhcp-relay"

FILES_${PN}-client = "${base_sbindir}/dhclient ${base_sbindir}/dhclient-script ${sysconfdir}/dhcp/dhclient.conf"
RDEPENDS_${PN}-client = "bash"

FILES_${PN}-omshell = "${bindir}/omshell"

pkg_postinst_dhcp-server() {
    mkdir -p $D/${localstatedir}/lib/dhcp
    touch $D/${localstatedir}/lib/dhcp/dhcpd.leases
    touch $D/${localstatedir}/lib/dhcp/dhcpd6.leases
}

pkg_postinst_dhcp-client() {
    mkdir -p $D/${localstatedir}/lib/dhcp
}

pkg_postrm_dhcp-server() {
    rm -f $D/${localstatedir}/lib/dhcp/dhcpd.leases
    rm -f $D/${localstatedir}/lib/dhcp/dhcpd6.leases

    if ! rmdir $D/${localstatedir}/lib/dhcp 2>/dev/null; then
        echo "Not removing ${localstatedir}/lib/dhcp as it is non-empty."
    fi
}

pkg_postrm_dhcp-client() {
    rm -f $D/${localstatedir}/lib/dhcp/dhclient.leases
    rm -f $D/${localstatedir}/lib/dhcp/dhclient6.leases

    if ! rmdir $D/${localstatedir}/lib/dhcp 2>/dev/null; then
        echo "Not removing ${localstatedir}/lib/dhcp as it is non-empty."
    fi
}
