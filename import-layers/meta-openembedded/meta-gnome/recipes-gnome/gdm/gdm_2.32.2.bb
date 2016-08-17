SUMMARY = "Graphical login manager"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "xinput gnome-panel tcp-wrappers libcanberra libxklavier grep consolekit libpam gnome-doc-utils gtk+ xrdb"

PR = "r18"

inherit gnome update-rc.d systemd useradd

SRC_URI += " \
    file://cross-xdetection.diff \
    file://0001-Remove-user-switch-applet.patch \
    file://0002-gdm-user-manager.c-avoid-displaying-system-users-in-.patch \
    file://sysrooted-pkg-config.patch \
    file://%gconf-tree.xml \
    file://gdm \
    file://gdm.conf \
    file://gdm-pam \
    file://Default \
    file://gdm.service.in \
"

SRC_URI[archive.md5sum] = "dbe5187a2e17881cc454e313e0ae8d1e"
SRC_URI[archive.sha256sum] = "034d23af0ea18d86e5543e707212d9297ec7d83f221808968af266dbebc0e703"
GNOME_COMPRESS_TYPE="bz2"

EXTRA_OECONF = " \
    --enable-authentication-scheme=shadow \
    --enable-debug=yes \
    --with-console-kit \
    --disable-scrollkeeper \
"

do_configure_prepend() {
    sed -i -e "s:\bdocs::g" ${S}/Makefile.am
}

do_install_prepend() {
    install -d ${D}/${localstatedir}/lib/gdm/.gconf.mandatory
    install ${WORKDIR}/%gconf-tree.xml ${D}/${localstatedir}/lib/gdm/.gconf.mandatory/
}

do_install_append() {
    install -d ${D}/${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/gdm ${D}/${sysconfdir}/init.d/

    install -d ${D}/${sysconfdir}/gdm
    install -m 0644 ${WORKDIR}/gdm.conf ${D}/${sysconfdir}/gdm/

    install -d ${D}/${sysconfdir}/pam.d
    install -m 0755 ${WORKDIR}/gdm-pam       ${D}/${sysconfdir}/pam.d/gdm
    rm -f ${D}/${sysconfdir}/pam.d/gdm-autologin

    install -d ${D}/${sysconfdir}/gdm/Init
    install -m 0755 ${WORKDIR}/Default ${D}/${sysconfdir}/gdm/Init

    install -d ${D}${systemd_unitdir}/system
    sed -e 's,%sbindir%,${sbindir},g' \
        < ${WORKDIR}/gdm.service.in \
        > ${D}${systemd_unitdir}/system/gdm.service

    chown -R gdm:gdm ${D}${localstatedir}/lib/gdm
    chmod 0750 ${D}${localstatedir}/lib/gdm

    rm -rf "${D}${localstatedir}/run"
    rmdir --ignore-fail-on-non-empty "${D}${localstatedir}"

    rm -f ${D}${datadir}/gdm/autostart/LoginWindow/at-spi-registryd-wrapper.desktop
    rm -f ${D}${datadir}/gdm/autostart/LoginWindow/orca-screen-reader.desktop
    rm -f ${D}${datadir}/gdm/autostart/LoginWindow/gnome-mag.desktop
    rm -f ${D}${datadir}/gdm/autostart/LoginWindow/gok.desktop
    rm -f ${D}${datadir}/gdm/autostart/LoginWindow/metacity.desktop
}

FILES_${PN} += "${datadir}/icon* \
    ${datadir}/xsession* \
"

RDEPENDS_${PN} += "grep dbus-x11 shadow"
# "libpam-base-files"
CONFFILES_${PN} += "${sysconfdir}/gdm/gdm.conf ${sysconfdir}/init.d/gdm"
RRECOMMENDS_${PN} += "openssh-misc desktop-file-utils glib-2.0-utils metacity gnome-session polkit-gnome consolekit"

RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "gdm.service"

INITSCRIPT_NAME = "gdm"
INITSCRIPT_PARAMS = "start 99 5 . stop 20 0 1 2 3 6 ."

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --no-create-home --home ${localstatedir}/lib/gdm --user-group gdm"

pkg_postinst_${PN} () {
# Register up as default dm
mkdir -p $D${sysconfdir}/X11/
echo "${bindir}/gdm" > $D${sysconfdir}/X11/default-display-manager
}

pkg_postrm_${PN} () {
    deluser gdm || true
    delgroup gdm || true
    sed -i /gdm/d ${sysconfdir}/X11/default-display-manager || true
}
