SUMMARY = "An Internet printing system for Unix"
DESCRIPTION = "The Common UNIX Printing System is a printing system and \
general replacement for lpd and the like. It supports the Internet Printing \
Protocol (IPP), and has its own filtering driver model for handling various \
document types."
HOMEPAGE = "https://www.cups.org/"
SECTION = "console/utils"
LICENSE = "Apache-2.0"
DEPENDS = "gnutls libpng jpeg dbus zlib libusb1"

SRC_URI = "https://github.com/apple/cups/releases/download/v${PV}/${BP}-source.tar.gz \
           file://0001-use-echo-only-in-init.patch \
           file://0002-don-t-try-to-run-generated-binaries.patch \
           file://0003-cups_1.4.6.bb-Fix-build-on-ppc64.patch \
           file://0004-cups-fix-multilib-install-file-conflicts.patch\
           "

UPSTREAM_CHECK_URI = "https://github.com/apple/cups/releases"
UPSTREAM_CHECK_REGEX = "cups-(?P<pver>\d+\.\d+(\.\d+)?)-source.tar"

# Issue only applies to MacOS
CVE_CHECK_WHITELIST += "CVE-2008-1033"
# Issue affects pdfdistiller plugin used with but not part of cups
CVE_CHECK_WHITELIST += "CVE-2009-0032"
# This is an Ubuntu only issue.
CVE_CHECK_WHITELIST += "CVE-2018-6553"

LEAD_SONAME = "libcupsdriver.so"

CLEANBROKEN = "1"

inherit autotools-brokensep binconfig useradd systemd pkgconfig multilib_script

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system lpadmin"

SYSTEMD_SERVICE_${PN} = "org.cups.cupsd.socket org.cups.cupsd.path org.cups.cupsd.service org.cups.cups-lpd.socket org.cups.cups-lpd@.service"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'zeroconf', 'avahi', '', d)} \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'pam systemd', d)}"
PACKAGECONFIG[avahi] = "--enable-avahi,--disable-avahi,avahi"
PACKAGECONFIG[acl] = "--enable-acl,--disable-acl,acl"
PACKAGECONFIG[pam] = "--enable-pam --with-pam-module=unix, --disable-pam, libpam"
PACKAGECONFIG[systemd] = "--with-systemd=${systemd_system_unitdir},--without-systemd,systemd"
PACKAGECONFIG[xinetd] = "--with-xinetd=${sysconfdir}/xinetd.d,--without-xinetd,xinetd"

EXTRA_OECONF = " \
               --enable-gnutls \
               --enable-dbus \
               --enable-browsing \
               --disable-gssapi \
               --enable-debug \
               --disable-relro \
               --enable-libusb \
               --with-system-groups=lpadmin \
               --with-cups-group=lp \
               DSOFLAGS='${LDFLAGS}' \
               "

EXTRA_AUTORECONF += "--exclude=autoheader"

do_compile () {
	echo "all:"    >  man/Makefile
	echo "libs:" >> man/Makefile
	echo "install:" >> man/Makefile
	echo "install-data:" >> man/Makefile
	echo "install-exec:" >> man/Makefile
	echo "install-headers:" >> man/Makefile
	echo "install-libs:" >> man/Makefile

	oe_runmake
}

do_install () {
	oe_runmake "DSTROOT=${D}" install

	# Remove /var/run from package as cupsd will populate it on startup
	rm -fr ${D}/${localstatedir}/run
	rmdir ${D}/${libexecdir}/${BPN}/driver

	# Fix the pam configuration file permissions
	if ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'true', 'false', d)}; then
	    chmod 0644 ${D}${sysconfdir}/pam.d/cups
	fi

	# Remove sysinit script and symlinks if sysvinit is not in DISTRO_FEATURES
	if ${@bb.utils.contains('DISTRO_FEATURES','sysvinit','false','true',d)}; then
	    rm -rf ${D}${sysconfdir}/init.d/
	    rm -rf ${D}${sysconfdir}/rc*
	fi
}

PACKAGES =+ "${PN}-lib ${PN}-libimage"

RDEPENDS_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'procps', '', d)}"
FILES_${PN} += "${libexecdir}/cups/ \
	       "

FILES_${PN}-lib = "${libdir}/libcups.so.*"

FILES_${PN}-libimage = "${libdir}/libcupsimage.so.*"

#package the html for the webgui inside the main packages (~1MB uncompressed)

FILES_${PN} += "${datadir}/doc/cups/images \
                ${datadir}/doc/cups/*html \
                ${datadir}/doc/cups/*.css \
                ${datadir}/icons/ \
               "
CONFFILES_${PN} += "${sysconfdir}/cups/cupsd.conf"

MULTILIB_SCRIPTS = "${PN}-dev:${bindir}/cups-config"

SYSROOT_PREPROCESS_FUNCS += "cups_sysroot_preprocess"
cups_sysroot_preprocess () {
	sed -i ${SYSROOT_DESTDIR}${bindir_crossscripts}/cups-config -e 's:cups_datadir=.*:cups_datadir=${datadir}/cups:' -e 's:cups_serverbin=.*:cups_serverbin=${libexecdir}/cups:'
}
