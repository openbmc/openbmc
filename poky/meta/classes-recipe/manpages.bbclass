#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Inherit this class to enable or disable building and installation of manpages
# depending on whether 'api-documentation' is in DISTRO_FEATURES. Such building
# tends to pull in the entire XML stack and other tools, so it's not enabled
# by default.
PACKAGECONFIG:append:class-target = " ${@bb.utils.contains('DISTRO_FEATURES', 'api-documentation', 'manpages', '', d)}"

inherit qemu

# usually manual files are packaged to ${PN}-doc except man-pages
MAN_PKG ?= "${PN}-doc"

# only add man-db to RDEPENDS when manual files are built and installed
RDEPENDS:${MAN_PKG} += "${@bb.utils.contains('PACKAGECONFIG', 'manpages', 'man-db', '', d)}"

pkg_postinst:${MAN_PKG}:append () {
	# only update manual page index caches when manual files are built and installed
	if ${@bb.utils.contains('PACKAGECONFIG', 'manpages', 'true', 'false', d)}; then
		if test -n "$D"; then
			if ${@bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', 'true', 'false', d)}; then
				$INTERCEPT_DIR/postinst_intercept update_mandb ${PKG} mlprefix=${MLPREFIX} binprefix=${MLPREFIX} bindir=${bindir} sysconfdir=${sysconfdir} mandir=${mandir}
			else
				$INTERCEPT_DIR/postinst_intercept delay_to_first_boot ${PKG} mlprefix=${MLPREFIX}
			fi
		else
			mandb -q
		fi
	fi
}

pkg_postrm:${MAN_PKG}:append () {
	# only update manual page index caches when manual files are built and installed
	if ${@bb.utils.contains('PACKAGECONFIG', 'manpages', 'true', 'false', d)}; then
		mandb -q
	fi
}
