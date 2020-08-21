SUMMARY = "GNOME Display Manager"
LICENSE="GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = " \
    dconf-native \
    gtk+3 \
    accountsservice \
    libcanberra \
    libpam \
"

REQUIRED_DISTRO_FEATURES = "x11 systemd pam"

inherit gnomebase gsettings gobject-introspection gettext systemd useradd upstream-version-is-even features_check

SRC_URI[archive.sha256sum] = "3bfbb620cbc0d1cbd70b4c4376cf4b705db4dc36a37124e5be386ccc25fa7e81"
SRC_URI += "file://0001-Ensure-pam-file-installation.patch"

EXTRA_OECONF = " \
    --without-plymouth \
    --with-default-pam-config=openembedded \
    --with-pam-mod-dir=${base_libdir}/security \
"

do_install_append() {
    rm -rf ${D}/run ${D}${localstatedir}/run
}

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --no-create-home --home ${localstatedir}/lib/gdm --user-group gdm"

SYSTEMD_SERVICE_${PN} = "${BPN}.service"

# Some gnome components - as gnome-panel and gnome-shell (!!) - require gdm
# components. To allow gnome-images using different display-manager, split them
# out into a seperate package.
PACKAGE_BEFORE_PN = "${PN}-base"
FILES_${PN}-base = " \
    ${datadir}/glib-2.0 \
    ${datadir}/gnome-session \
    ${libdir}/lib*${SOLIBS} \
    ${libdir}/girepository-1.0 \
"

CONFFILES_${PN} += "${sysconfdir}/gdm/custom.conf"
FILES_${PN} += " \
    ${datadir}/dconf \
    ${base_libdir}/security/pam_gdm.so \
    ${localstatedir} \
    ${systemd_unitdir} \
"

RDEPENDS_${PN} += "${PN}-base"

