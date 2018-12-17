SECTION = "console/network"
SUMMARY = "Internet Software Consortium DHCP package"
DESCRIPTION = "DHCP (Dynamic Host Configuration Protocol) is a protocol \
which allows individual devices on an IP network to get their own \
network configuration information from a server.  DHCP helps make it \
easier to administer devices."

HOMEPAGE = "http://www.isc.org/"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;beginline=4;md5=004a4db50a1e20972e924a8618747c01"

DEPENDS = "openssl bind"

SRC_URI = "http://ftp.isc.org/isc/dhcp/${PV}/dhcp-${PV}.tar.gz \
           file://init-relay file://default-relay \
           file://init-server file://default-server \
           file://dhclient.conf file://dhcpd.conf \
           file://dhclient-systemd-wrapper \
           file://dhclient.service \
           file://dhcpd.service file://dhcrelay.service \
           file://dhcpd6.service \
           "
UPSTREAM_CHECK_URI = "ftp://ftp.isc.org/isc/dhcp/"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+\.\d+\.(\d+?))/"

inherit autotools-brokensep systemd useradd update-rc.d

USERADD_PACKAGES = "${PN}-server"
USERADD_PARAM_${PN}-server = "--system --no-create-home --home-dir /var/run/${BPN} --shell /bin/false --user-group ${BPN}"

SYSTEMD_PACKAGES = "${PN}-server ${PN}-relay ${PN}-client"
SYSTEMD_SERVICE_${PN}-server = "dhcpd.service dhcpd6.service"
SYSTEMD_AUTO_ENABLE_${PN}-server = "disable"

SYSTEMD_SERVICE_${PN}-relay = "dhcrelay.service"
SYSTEMD_AUTO_ENABLE_${PN}-relay = "disable"

SYSTEMD_SERVICE_${PN}-client = "dhclient.service"
SYSTEMD_AUTO_ENABLE_${PN}-client = "disable"

INITSCRIPT_PACKAGES = "dhcp-server"
INITSCRIPT_NAME_dhcp-server = "dhcp-server"
INITSCRIPT_PARAMS_dhcp-server = "defaults"

TARGET_CFLAGS += "-D_GNU_SOURCE"
EXTRA_OECONF = "--with-srv-lease-file=${localstatedir}/lib/dhcp/dhcpd.leases \
                --with-srv6-lease-file=${localstatedir}/lib/dhcp/dhcpd6.leases \
                --with-cli-lease-file=${localstatedir}/lib/dhcp/dhclient.leases \
                --with-cli6-lease-file=${localstatedir}/lib/dhcp/dhclient6.leases \
                --enable-paranoia --disable-static \
                --with-randomdev=/dev/random \
                --with-libbind=${STAGING_DIR_HOST} \
		--enable-libtool \
               "

#Enable shared libs per dhcp README
do_configure_prepend () {
	cp configure.ac+lt configure.ac
}

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
	sed -i -e 's,@SYSCONFDIR@,${sysconfdir},g' ${D}${systemd_unitdir}/system/dhcrelay.service

	install -d ${D}${base_sbindir}
	install -m 0755 ${WORKDIR}/dhclient-systemd-wrapper ${D}${base_sbindir}/dhclient-systemd-wrapper
	install -m 0644 ${WORKDIR}/dhclient.service ${D}${systemd_unitdir}/system
	sed -i -e 's,@SYSCONFDIR@,${sysconfdir},g' ${D}${systemd_unitdir}/system/dhclient.service
	sed -i -e 's,@BASE_SBINDIR@,${base_sbindir},g' ${D}${systemd_unitdir}/system/dhclient.service
}

PACKAGES += "dhcp-libs dhcp-server dhcp-server-config dhcp-client dhcp-relay dhcp-omshell"

PACKAGES_remove = "${PN}"
RDEPENDS_${PN}-dev = ""
RDEPENDS_${PN}-staticdev = ""
FILES_${PN}-libs = "${libdir}/libdhcpctl.so.0* ${libdir}/libomapi.so.0* ${libdir}/libdhcp.so.0*"

FILES_${PN}-server = "${sbindir}/dhcpd ${sysconfdir}/init.d/dhcp-server"
RRECOMMENDS_${PN}-server = "dhcp-server-config"

FILES_${PN}-server-config = "${sysconfdir}/default/dhcp-server ${sysconfdir}/dhcp/dhcpd.conf"

FILES_${PN}-relay = "${sbindir}/dhcrelay ${sysconfdir}/init.d/dhcp-relay ${sysconfdir}/default/dhcp-relay"

FILES_${PN}-client = "${base_sbindir}/dhclient \
                      ${base_sbindir}/dhclient-script \
                      ${sysconfdir}/dhcp/dhclient.conf \
                      ${base_sbindir}/dhclient-systemd-wrapper \
                     "

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
