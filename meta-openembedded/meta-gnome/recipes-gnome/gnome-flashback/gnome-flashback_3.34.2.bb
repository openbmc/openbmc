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
    gnome-desktop3 \
    gnome-bluetooth \
"

SRC_URI[archive.md5sum] = "251b51bad322c41d34d06fdb8f1799d4"
SRC_URI[archive.sha256sum] = "3be65388cd2c8f39741bcc05da87ef40035183a9a39502d67696242c2aeb469c"

do_install_append() {
    # no oe-layer has compiz -> remove dead session
    rm -f ${D}${datadir}/xsessions/gnome-flashback-compiz.desktop
}

FILES_${PN} += " \
    ${datadir}/xsessions \
    ${datadir}/desktop-directories \
    ${datadir}/gnome-session \
    ${systemd_user_unitdir} \
"

RDEPENDS_${PN} += "metacity gnome-panel"
