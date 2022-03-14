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

REQUIRED_DISTRO_FEATURES = "x11 systemd pam polkit gobject-introspection-data"
GIR_MESON_OPTION = ""

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gobject-introspection gettext systemd useradd itstool gnome-help features_check

SRC_URI[archive.sha256sum] = "5738c4293a9f5a80d4a6e9e06f4d0df3e9f313ca7b61bfb4d8afaba983e200dc"

EXTRA_OEMESON = " \
    -Dplymouth=disabled \
    -Ddefault-pam-config=openembedded \
    -Dpam-mod-dir=${base_libdir}/security \
"

do_install:append() {
    rm -rf ${D}/run ${D}${localstatedir}/run
}

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --home ${localstatedir}/lib/gdm --user-group gdm"

SYSTEMD_SERVICE:${PN} = "${BPN}.service"

# Some gnome components - as gnome-panel and gnome-shell (!!) - require gdm
# components. To allow gnome-images using different display-manager, split them
# out into a seperate package.
PACKAGE_BEFORE_PN = "${PN}-base"
FILES:${PN}-base = " \
    ${datadir}/glib-2.0 \
    ${datadir}/gnome-session \
    ${libdir}/lib*${SOLIBS} \
    ${libdir}/girepository-1.0 \
"

CONFFILES:${PN} += "${sysconfdir}/gdm/custom.conf"
FILES:${PN} += " \
    ${datadir}/dconf \
    ${base_libdir}/security/pam_gdm.so \
    ${localstatedir} \
    ${systemd_unitdir} ${systemd_user_unitdir} \
"

RDEPENDS:${PN} += "${PN}-base"

