SUMMARY = "Makes power profiles handling available over D-Bus"
HOMEPAGE = "https://gitlab.freedesktop.org/upower/power-profile-daemon"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = " \
    glib-2.0 \
    polkit \
    upower \
    libgudev \
"

inherit meson pkgconfig systemd features_check

REQUIRED_DISTRO_FEATURES = "polkit"

EXTRA_OEMESON = "-Dtests=false"

SRCREV = "5b4994c8a91290481bef87a5bae95391d0ec677f"
SRC_URI = "git://gitlab.freedesktop.org/upower/power-profiles-daemon;branch=main;protocol=https"

do_install:append() {
	install -d ${D}${sysconfdir}/tmpfiles.d
	echo "d ${localstatedir}/lib/power-profiles-daemon 700 root root - -" > ${D}${sysconfdir}/tmpfiles.d/power-profiles-daemon.conf
	sed -i -e 's|After=multi-user.target |After=|g' ${D}${systemd_system_unitdir}/power-profiles-daemon.service
}

FILES:${PN} += "${sysconfdir}/tmpfiles.d ${datadir}/dbus-1 ${datadir}/polkit-1 ${systemd_system_unitdir}"
