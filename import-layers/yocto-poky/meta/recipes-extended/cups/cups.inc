SUMMARY = "An Internet printing system for Unix"
SECTION = "console/utils"
LICENSE = "GPLv2 & LGPLv2"
DEPENDS = "gnutls libpng jpeg dbus dbus-glib zlib libusb"

SRC_URI = "https://www.cups.org/software/${PV}/${BP}-source.tar.bz2 \
           file://use_echo_only_in_init.patch \
           file://0001-don-t-try-to-run-generated-binaries.patch \
           file://cups_serverbin.patch \
	  "

UPSTREAM_CHECK_URI = "http://www.cups.org/software.php"
UPSTREAM_CHECK_REGEX = "cups-(?P<pver>\d+\.\d+(\.\d+)?)-source.tar"

LEAD_SONAME = "libcupsdriver.so"

CLEANBROKEN = "1"

inherit autotools-brokensep binconfig useradd systemd

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system lpadmin"

SYSTEMD_SERVICE_${PN} = "org.cups.cupsd.socket org.cups.cupsd.path org.cups.cupsd.service org.cups.cups-lpd.socket org.cups.cups-lpd@.service"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'zeroconf', 'avahi', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}"
PACKAGECONFIG[avahi] = "--enable-avahi,--disable-avahi,avahi"
PACKAGECONFIG[acl] = "--enable-acl,--disable-acl,acl"
PACKAGECONFIG[pam] = "--enable-pam, --disable-pam, libpam"
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
               --without-php \
               --without-perl \
               --without-python \
               --without-java \
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
	rmdir ${D}/${libdir}/${BPN}/driver

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

python do_package_append() {
    import subprocess
    # Change permissions back the way they were, they probably had a reason...
    workdir = d.getVar('WORKDIR', True)
    subprocess.call('chmod 0511 %s/install/cups/var/run/cups/certs' % workdir, shell=True)
}

PACKAGES =+ "${PN}-lib ${PN}-libimage"

RDEPENDS_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'procps', '', d)}"
FILES_${PN} += "${libdir}/cups/backend \
		${libdir}/cups/cgi-bin \
		${libdir}/cups/filter \
		${libdir}/cups/monitor \
		${libdir}/cups/notifier \
		${libdir}/cups/daemon \
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

SYSROOT_PREPROCESS_FUNCS += "cups_sysroot_preprocess"
cups_sysroot_preprocess () {
	sed -i ${SYSROOT_DESTDIR}${bindir_crossscripts}/cups-config -e 's:cups_datadir=.*:cups_datadir=${datadir}/cups:' -e 's:cups_serverbin=.*:cups_serverbin=${libdir}/cups:'
}
