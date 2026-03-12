SUMMARY = "GNOME Display Manager"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = " \
    accountsservice \
    audit \
    dconf \
    dconf-native \
    gtk4 \
    json-glib \
    keyutils \
    libcanberra \
    libgudev \
    libpam \
"

REQUIRED_DISTRO_FEATURES = "systemd pam polkit gobject-introspection-data"
GIR_MESON_OPTION = ""


inherit gnomebase gsettings pkgconfig gobject-introspection gettext systemd useradd itstool gnome-help features_check

SRC_URI[archive.sha256sum] = "9813631f9f5f0f860ea14a437866e60efc7bed3023b7c3b765cc5de1de597a06"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[plymouth] = "-Dplymouth=enabled,-Dplymouth=disabled,plymouth"
PACKAGECONFIG[x11] = "-Dx11-support=true,-Dx11-support=false,xau"

EXTRA_OEMESON = " \
    -Ddefault-pam-config=openembedded \
    -Dpam-mod-dir=${base_libdir}/security \
    --cross-file=${WORKDIR}/meson-${PN}.cross \
"

do_write_config:append() {
    cat >${WORKDIR}/meson-${PN}.cross <<EOF
[binaries]
nologin = '${sbindir}/nologin'
EOF
}

do_install:prepend() {
    sed -i -e 's|${B}/||g' ${B}/daemon/gdm-session-worker-enum-types.c
    sed -i -e 's|${B}/||g' ${B}/daemon/gdm-session-worker-enum-types.h
    sed -i -e 's|${B}/||g' ${B}/daemon/gdm-session-enum-types.c
    sed -i -e 's|${B}/||g' ${B}/daemon/gdm-session-enum-types.h
}

do_install:append() {
    rm -rf ${D}/run ${D}${localstatedir}/run
    echo "auth       optional     pam_gnome_keyring.so" >> ${D}${sysconfdir}/pam.d/gdm-password
    echo "session    optional     pam_gnome_keyring.so auto_start" >> ${D}${sysconfdir}/pam.d/gdm-password
    install -d ${D}${sysconfdir}/tmpfiles.d
    echo "d ${localstatedir}/lib/gdm 700 gdm gdm - -" > ${D}${sysconfdir}/tmpfiles.d/gdm.conf
}

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --groups video --home ${localstatedir}/lib/gdm  gdm"

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
    ${systemd_unitdir} ${systemd_user_unitdir} \
"

RDEPENDS:${PN} += "${PN}-base"
