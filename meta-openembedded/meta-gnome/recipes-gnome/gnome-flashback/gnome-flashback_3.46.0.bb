SUMMARY = "GNOME Flashback (GNOME 2) session"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

GNOMEBASEBUILDCLASS = "autotools"
inherit gnomebase gsettings gtk-icon-cache gettext upstream-version-is-even features_check

REQUIRED_DISTRO_FEATURES = "x11 polkit systemd pam"

DEPENDS += " \
    upower \
    ibus \
    libxkbfile \
    polkit \
    metacity \
    gdm \
    gnome-desktop \
    gnome-bluetooth \
    gnome-panel \
"

SRC_URI[archive.sha256sum] = "7a8d5c03310e4dfadd18a65e00a37741032afeea5418dd6804a975c4b0980045"

do_install:append() {
    # no oe-layer has compiz -> remove dead session
    rm -f ${D}${datadir}/xsessions/gnome-flashback-compiz.desktop
}

FILES:${PN} += " \
    ${datadir}/desktop-directories \
    ${datadir}/gnome-control-center \
    ${datadir}/gnome-panel \
    ${datadir}/gnome-session \
    ${datadir}/xsessions \
    ${libdir}/gnome-panel \
    ${systemd_user_unitdir} \
"

RDEPENDS:${PN} += "metacity"
