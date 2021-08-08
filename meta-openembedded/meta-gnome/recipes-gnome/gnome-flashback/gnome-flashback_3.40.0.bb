SUMMARY = "GNOME Flashback (GNOME 2) session"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit gnomebase gsettings gtk-icon-cache gettext upstream-version-is-even features_check

REQUIRED_DISTRO_FEATURES = "x11 polkit systemd pam"

DEPENDS += " \
    upower \
    ibus \
    libxkbfile \
    polkit \
    metacity \
    gdm \
    gnome-desktop3 \
    gnome-bluetooth \
    gnome-panel \
"

SRC_URI[archive.sha256sum] = "e03f33100f1982019c2e59bbdcd664549ec5caa0ef2d99e2c0e1272cea08bb3b"

do_install:append() {
    # no oe-layer has compiz -> remove dead session
    rm -f ${D}${datadir}/xsessions/gnome-flashback-compiz.desktop
}

FILES:${PN} += " \
    ${datadir}/desktop-directories \
    ${datadir}/gnome-panel \
    ${datadir}/gnome-session \
    ${datadir}/xsessions \
    ${libdir}/gnome-panel \
    ${systemd_user_unitdir} \
"

RDEPENDS:${PN} += "metacity"
