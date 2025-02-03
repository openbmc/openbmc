SUMMARY = "Polkit Authorization Framework"
DESCRIPTION = "The polkit package is an application-level toolkit for defining and handling the policy that allows unprivileged processes to speak to privileged processes."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/polkit"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=155db86cdbafa7532b41f390409283eb"
BUGTRACKER = "https://github.com/polkit-org/polkit/issues"

SRC_URI = "git://github.com/polkit-org/polkit.git;protocol=https;branch=main"

S = "${WORKDIR}/git"
SRCREV = "d627b0d1e1108563658dabe3fb8d2a065e64df10"

DEPENDS = "expat glib-2.0 duktape"

inherit meson pkgconfig useradd systemd gettext gobject-introspection features_check

REQUIRED_DISTRO_FEATURES = "polkit"

# Prevent meson.build to try to autodetect host OS (which could lead to
# non-reproducibility)
EXTRA_OEMESON = "-Dos_type=openembedded"

PACKAGECONFIG = " \
	${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', 'consolekit', d)} \
	dbus \
"
PACKAGECONFIG[dbus] = ",,dbus"
PACKAGECONFIG[gtk-doc] = "-Dgtk_doc=true,-Dgtk_doc=false,gtk-doc-native"
PACKAGECONFIG[pam] = "-Dauthfw=pam,-Dauthfw=shadow,libpam,libpam"
PACKAGECONFIG[systemd] = "-Dsession_tracking=logind,,systemd,,,consolekit elogind"
PACKAGECONFIG[consolekit] = "-Dsession_tracking=ConsoleKit,,,consolekit,,systemd elogind"
PACKAGECONFIG[elogind] = "-Dsession_tracking=libelogin,,elogind,,,systemd consolekit"
PACKAGECONFIG[libs-only] = "-Dlibs-only=true,-Dlibs-only=false"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --user-group --home-dir ${sysconfdir}/${BPN}-1 --shell /bin/nologin polkitd"

SYSTEMD_SERVICE:${PN} = "${BPN}.service"
SYSTEMD_AUTO_ENABLE = "disable"

do_install:append() {
	#Fix up permissions on polkit rules.d to work with rpm4 constraints
	if ${@bb.utils.contains('PACKAGECONFIG', 'libs-only', 'false', 'true', d)}; then
		chmod 700 ${D}/${sysconfdir}/polkit-1/rules.d
		chown polkitd:root ${D}/${sysconfdir}/polkit-1/rules.d
	fi

	# Polkit unconditionally installs a systemd service, remove it on SysVinit
	# systems to avoid "installed but not packaged file" error.
	if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
		rm -r ${D}${libdir}/systemd
	fi
}

FILES:${PN} += " \
	${libdir}/pam.d/polkit-1 \
	${libdir}/sysusers.d \
	${libdir}/tmpfiles.d \
	${libdir}/polkit-1 \
	${nonarch_libdir}/pam.d/polkit-1 \
	${nonarch_libdir}/sysusers.d \
	${nonarch_libdir}/tmpfiles.d \
	${nonarch_libdir}/polkit-1 \
	${datadir} \
"
