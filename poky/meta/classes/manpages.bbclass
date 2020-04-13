# Inherit this class to enable or disable building and installation of manpages
# depending on whether 'api-documentation' is in DISTRO_FEATURES. Such building
# tends to pull in the entire XML stack and other tools, so it's not enabled
# by default.
PACKAGECONFIG_append_class-target = " ${@bb.utils.contains('DISTRO_FEATURES', 'api-documentation', 'manpages', '', d)}"

inherit qemu

# usually manual files are packaged to ${PN}-doc except man-pages
MAN_PKG ?= "${PN}-doc"

# only add man-db to RDEPENDS when manual files are built and installed
RDEPENDS_${MAN_PKG} += "${@bb.utils.contains('PACKAGECONFIG', 'manpages', 'man-db', '', d)}"

pkg_postinst_append_${MAN_PKG} () {
	# only update manual page index caches when manual files are built and installed
	if ${@bb.utils.contains('PACKAGECONFIG', 'manpages', 'true', 'false', d)}; then
		if test -n "$D"; then
			if ${@bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', 'true','false', d)}; then
				sed "s:\(\s\)/:\1$D/:g" $D${sysconfdir}/man_db.conf | ${@qemu_run_binary(d, '$D', '${bindir}/mandb')} -C - -u -q $D${mandir}
				chown -R root:root $D${mandir}
				mkdir -p $D${localstatedir}/cache/man
				cd $D${mandir}
				find . -name index.db | while read index; do
					mkdir -p $D${localstatedir}/cache/man/$(dirname ${index})
					mv ${index} $D${localstatedir}/cache/man/${index}
					chown man:man $D${localstatedir}/cache/man/${index}
				done
				cd -
			else
				$INTERCEPT_DIR/postinst_intercept delay_to_first_boot ${PKG} mlprefix=${MLPREFIX}
			fi
		else
			mandb -q
		fi
	fi
}

pkg_postrm_append_${MAN_PKG} () {
	# only update manual page index caches when manual files are built and installed
	if ${@bb.utils.contains('PACKAGECONFIG', 'manpages', 'true', 'false', d)}; then
		mandb -q
	fi
}
