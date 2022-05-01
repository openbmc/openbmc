SUMMARY = "GNOME Flashback (GNOME 2) session"
LICENSE = "GPL-3.0-only"
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
    gnome-desktop \
    gnome-bluetooth3 \
    gnome-panel \
"

SRC_URI[archive.sha256sum] = "1df0838127c6246eecd89d1c50ff88fc82abf6de3b3068e52dde495a42bd550a"

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
