SUMMARY = "System performance tools"
DESCRIPTION = "The sysstat utilities are a collection of performance monitoring tools for Linux."
HOMEPAGE = "https://sysstat.github.io/"
LICENSE = "GPL-2.0-or-later"
SECTION = "console/utils"

SRC_URI = "git://github.com/sysstat/sysstat.git;protocol=https;branch=master \
           file://99_sysstat \
           file://sysstat.service \
           file://0001-configure.in-remove-check-for-chkconfig.patch \
          "

LIC_FILES_CHKSUM = "file://COPYING;md5=a23a74b3f4caf9616230789d94217acb"

SRCREV = "2d7682f26f42cef9127b123e319349b330c4ab8f"
S = "${WORKDIR}/git"

DEPENDS += "base-passwd"

# autotools-brokensep as this package doesn't use automake
inherit autotools-brokensep gettext systemd

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[lm-sensors] = "--enable-sensors,--disable-sensors,lmsensors,lmsensors-libsensors"
PACKAGECONFIG[cron] = "--enable-install-cron --enable-copy-only,--disable-install-cron --disable-copy-only"
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_system_unitdir}"

EXTRA_OECONF += "--disable-stripping"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "sysstat.service"
SYSTEMD_AUTO_ENABLE = "enable"

do_configure:prepend() {
    export sa_lib_dir=${libexecdir}/sa
}

do_install() {
	autotools_do_install

	# Don't version the documentation
	mv ${D}${docdir}/${BP} ${D}${docdir}/${BPN}

	# don't install /var/log/sa when populating rootfs. Do it through volatile
	rm -rf ${D}/var
	if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
	        install -d ${D}/etc/default/volatiles
		install -m 0644 ${WORKDIR}/99_sysstat ${D}/etc/default/volatiles
	fi
	if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
	        install -d ${D}${nonarch_libdir}/tmpfiles.d
	        echo "d ${localstatedir}/log/sa - - - -" \
		     > ${D}${nonarch_libdir}/tmpfiles.d/sysstat.conf

		# Unless both cron and systemd are enabled, install our own
		# systemd unit file. Otherwise the package will install one.
	        if ${@bb.utils.contains('PACKAGECONFIG', 'cron systemd', 'false', 'true', d)}; then
			install -d ${D}${systemd_system_unitdir}
			install -m 0644 ${WORKDIR}/sysstat.service ${D}${systemd_system_unitdir}
			sed -i -e 's#@LIBEXECDIR@#${libexecdir}#g' ${D}${systemd_system_unitdir}/sysstat.service
	        fi
	fi
}

pkg_postinst:${PN} () {
	if [  ! -n "$D" ]; then
		if [ -e /etc/init.d/populate-volatile.sh ]; then
			/etc/init.d/populate-volatile.sh update
		fi
	fi
}

FILES:${PN} += " \
	${systemd_system_unitdir} \
	${nonarch_base_libdir}/systemd  \
	${nonarch_libdir}/tmpfiles.d \
"

TARGET_CC_ARCH += "${LDFLAGS}"

