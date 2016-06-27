SUMMARY = "Framework for defining and tracking users, login sessions, and seats"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/ConsoleKit"
BUGTRACKER = "https://bugs.freedesktop.org/buglist.cgi?query_format=specific&product=ConsoleKit"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
                    file://src/main.c;endline=21;md5=0a994e09769780220163255d8f9071c3"

DEPENDS = "glib-2.0 glib-2.0-native dbus dbus-glib virtual/libx11"
RDEPENDS_${PN} += "base-files"

inherit autotools pkgconfig distro_features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "http://www.freedesktop.org/software/ConsoleKit/dist/ConsoleKit-${PV}.tar.xz \
           file://sepbuildfix.patch \
           file://add-polkit-configure-argument.patch \
"

SRC_URI[md5sum] = "611792b4d616253a5bdec9175f8b7678"
SRC_URI[sha256sum] = "b41d17e06f80059589fbeefe96ad07bcc564c49e65516da1caf975146475565c"

S = "${WORKDIR}/ConsoleKit-${PV}"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

PACKAGECONFIG[pam] = "--enable-pam-module --with-pam-module-dir=${base_libdir}/security,--disable-pam-module,libpam"
PACKAGECONFIG[policykit] = "--with-polkit,--without-polkit,polkit"
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_unitdir}/system/,--with-systemdsystemunitdir="

FILES_${PN} += "${localstatedir}/log/ConsoleKit ${exec_prefix}/lib/ConsoleKit \
                ${libdir}/ConsoleKit  ${systemd_unitdir} ${base_libdir} \
                ${datadir}/dbus-1 ${datadir}/PolicyKit ${datadir}/polkit*"

PACKAGES =+ "pam-plugin-ck-connector"
FILES_pam-plugin-ck-connector += "${base_libdir}/security/*.so"
RDEPENDS_pam-plugin-ck-connector += "${PN}"

do_install_append() {
	if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		install -d ${D}${sysconfdir}/tmpfiles.d
		echo "d ${localstatedir}/log/ConsoleKit - - - -" \
			> ${D}${sysconfdir}/tmpfiles.d/consolekit.conf
	fi

	# Remove /var/run from package as console-kit-daemon will populate it on startup
	rm -fr "${D}${localstatedir}/run"
}
