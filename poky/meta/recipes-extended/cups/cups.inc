SUMMARY = "An Internet printing system for Unix"
DESCRIPTION = "The Common UNIX Printing System is a printing system and \
general replacement for lpd and the like. It supports the Internet Printing \
Protocol (IPP), and has its own filtering driver model for handling various \
document types."
HOMEPAGE = "https://www.cups.org/"
SECTION = "console/utils"
LICENSE = "Apache-2.0"
DEPENDS = "libpng jpeg dbus zlib libusb1"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/cups-${PV}-source.tar.gz \
           file://0001-use-echo-only-in-init.patch \
           file://0002-don-t-try-to-run-generated-binaries.patch \
           file://libexecdir.patch \
           file://0004-cups-fix-multilib-install-file-conflicts.patch \
           file://volatiles.99_cups \
           file://cups-volatiles.conf \
           "

GITHUB_BASE_URI = "https://github.com/OpenPrinting/cups/releases"

CVE_STATUS[CVE-2008-1033] = "not-applicable-platform: Issue only applies to MacOS"
CVE_STATUS[CVE-2009-0032] = "cpe-incorrect: Issue affects pdfdistiller plugin used with but not part of cups"
CVE_STATUS[CVE-2018-6553] = "not-applicable-platform: This is an Ubuntu only issue"
CVE_STATUS[CVE-2022-26691] = "fixed-version: This is fixed in 2.4.2 but the cve-check class still reports it"
CVE_STATUS[CVE-2021-25317] = "not-applicable-config: This concerns /var/log/cups having lp ownership, our /var/log/cups is root:root, so this doesn't apply."

LEAD_SONAME = "libcupsdriver.so"

CLEANBROKEN = "1"

inherit autotools-brokensep binconfig useradd systemd pkgconfig multilib_script github-releases

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system lpadmin"

SYSTEMD_SERVICE:${PN} = "cups.socket cups.path cups.service cups-lpd.socket"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'zeroconf', 'avahi', '', d)} \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'pam systemd', d)} \
                   openssl \
"
PACKAGECONFIG[avahi] = "--with-dnssd=avahi,--with-dnssd=no,avahi"
PACKAGECONFIG[acl] = "--enable-acl,--disable-acl,acl"
PACKAGECONFIG[gnutls] = "--with-tls=gnutls,,gnutls,,,openssl"
PACKAGECONFIG[openssl] = "--with-tls=openssl,,openssl,,,gnutls"
PACKAGECONFIG[pam] = "--enable-pam --with-pam-module=unix, --disable-pam, libpam"
PACKAGECONFIG[systemd] = "--with-systemd=${systemd_system_unitdir},--without-systemd,systemd"
PACKAGECONFIG[xinetd] = "--with-xinetd=${sysconfdir}/xinetd.d,--without-xinetd,xinetd"
PACKAGECONFIG[webif] = "--enable-webif,--disable-webif"

EXTRA_OECONF = " \
               --enable-dbus \
               --with-dbusdir=${sysconfdir}/dbus-1 \
               --enable-browsing \
               --disable-gssapi \
               --enable-debug \
               --disable-relro \
               --enable-libusb \
               --with-system-groups=lpadmin,root,sys,wheel \
               --with-cups-group=lp \
               --with-domainsocket=/run/cups/cups.sock \
               --with-pkgconfpath=${libdir}/pkgconfig \
               DSOFLAGS='${LDFLAGS}' \
               "

EXTRA_AUTORECONF += "--exclude=autoheader"

do_install () {
	oe_runmake "BUILDROOT=${D}" install

	# Remove /var/run from package as cupsd will populate it on startup
	rm -fr ${D}/${localstatedir}/run
	rm -fr ${D}/${localstatedir}/log
	rmdir ${D}/${libexecdir}/${BPN}/driver

	# Fix the pam configuration file permissions
	if ${@bb.utils.contains('PACKAGECONFIG', 'pam', 'true', 'false', d)}; then
	    chmod 0644 ${D}${sysconfdir}/pam.d/cups
	fi

	# Remove sysinit script and symlinks if sysvinit is not in DISTRO_FEATURES
	if ${@bb.utils.contains('DISTRO_FEATURES','sysvinit','false','true',d)}; then
	    rm -rf ${D}${sysconfdir}/init.d/
	    rm -rf ${D}${sysconfdir}/rc*
	    install -d ${D}${sysconfdir}/tmpfiles.d
	    install -m 0644 ${UNPACKDIR}/cups-volatiles.conf \
		    ${D}${sysconfdir}/tmpfiles.d/cups.conf
	else
	    install -d ${D}${sysconfdir}/default/volatiles
	    install -m 0644 ${UNPACKDIR}/volatiles.99_cups \
		    ${D}${sysconfdir}/default/volatiles/99_cups
	fi
}

PACKAGES =+ "${PN}-lib ${PN}-libimage ${PN}-webif"

RDEPENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'procps', '', d)}"
FILES:${PN} += "${libexecdir}/cups/"

FILES:${PN}-lib = "${libdir}/libcups.so.*"

FILES:${PN}-libimage = "${libdir}/libcupsimage.so.*"

# put the html for the web interface into its own PACKAGE
FILES:${PN}-webif += "${datadir}/doc/cups/ ${datadir}/icons/"
RRECOMMENDS:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'webif', '${PN}-webif', '', d)}"

CONFFILES:${PN} += "${sysconfdir}/cups/cupsd.conf"

MULTILIB_SCRIPTS = "${PN}-dev:${bindir}/cups-config"

LOCALE_PATHS += "${datadir}/cups/templates"

SYSROOT_PREPROCESS_FUNCS += "cups_sysroot_preprocess"
cups_sysroot_preprocess () {
	sed -i ${SYSROOT_DESTDIR}${bindir_crossscripts}/cups-config -e 's:cups_datadir=.*:cups_datadir=${datadir}/cups:' -e 's:cups_serverbin=.*:cups_serverbin=${libexecdir}/cups:'
}
