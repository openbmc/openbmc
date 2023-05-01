SUMMARY = "GNOME Display Manager"
LICENSE="GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = " \
    accountsservice \
    audit \
    dconf-native \
    gtk+3 \
    keyutils \
    libcanberra \
    libgudev \
    libpam \
    xserver-xorg \
"

REQUIRED_DISTRO_FEATURES = "x11 systemd pam polkit gobject-introspection-data"
GIR_MESON_OPTION = ""

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings pkgconfig gobject-introspection gettext systemd useradd itstool gnome-help features_check

SRC_URI[archive.sha256sum] = "ce20b0a221dbf8cde0064b501fd8f38f73839152857c4535337eb09cc52f7f6c"

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
