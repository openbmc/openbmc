SUMMARY = "GNOME Shell is the graphical shell of the GNOME desktop environment"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = " \
    libxml2-native \
    at-spi2-core \
    evolution-data-server \
    gcr \
    gdk-pixbuf \
    gjs \
    gnome-autoar \
    gnome-desktop \
    gsettings-desktop-schemas \
    gtk4 \
    libsoup-3.0 \
    libxml2 \
    mutter \
    pango \
    polkit \
"

inherit gnomebase gsettings gettext gobject-introspection gtk-icon-cache features_check bash-completion

REQUIRED_DISTRO_FEATURES = "polkit systemd pam wayland"

GTKIC_VERSION = "4"
GIR_MESON_OPTION = ""

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES += "gobject-introspection-data"

SRC_URI += "file://0001-shell-app-usage.c-only-include-x11-headers-if-HAVE_X.patch"
SRC_URI += "file://0002-calendar-server-Fix-build-failure-with-libical-4.patch"
SRC_URI[archive.sha256sum] = "c531939539db316a41aef23670370abd1330d3254f84bcb0f9f4dae5d6e362cf"

PACKAGECONFIG ??= " \
    camera-monitor \
    extensions-tool \
    nm \
    portal-helper \
    bluetooth \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
"
PACKAGECONFIG[bluetooth] = ",,gnome-bluetooth"
PACKAGECONFIG[camera-monitor] = "-Dcamera_monitor=true, -Dcamera_monitor=false, pipewire"
PACKAGECONFIG[extensions-app] = "-Dextensions_app=true, -Dextensions_app=false"
PACKAGECONFIG[extensions-tool] = "-Dextensions_tool=true, -Dextensions_tool=false, gnome-autoar json-glib libsoup-3.0"
PACKAGECONFIG[nm] = "-Dnetworkmanager=true, -Dnetworkmanager=false, networkmanager libsecret, networkmanager"
PACKAGECONFIG[portal-helper] = "-Dportal_helper=true, -Dportal_helper=false"
PACKAGECONFIG[systemd] = "-Dsystemd=true, -Dsystemd=false, systemd"

EXTRA_OEMESON += " \
    -Dtests=false \
    -Dman=false \
    -Dgtk_doc=false \
    --cross-file=${WORKDIR}/meson-${PN}.cross \
"

do_write_config:append() {
    cat >${WORKDIR}/meson-${PN}.cross <<EOF
[binaries]
gjs = '${bindir}/gjs'
EOF
}

do_install:append() {
    # fix shebangs
    for tool in `find ${D}${bindir} -name '*-tool'`; do
        sed -i 's:#!${PYTHON}:#!${bindir}/python3:' $tool
    done
}

GSETTINGS_PACKAGE = "${PN}-gsettings"

FILES:${PN} += " \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
    ${datadir}/gnome-control-center \
    ${datadir}/xdg-desktop-portal \
    ${datadir}/desktop-directories \
    ${systemd_user_unitdir} \
"

RDEPENDS:${PN} += " \
	accountsservice \
	adwaita-icon-theme \
	adwaita-icon-theme-cursors \
	gdm \
	gnome-control-center \
	gnome-backgrounds \
	gnome-bluetooth \
	gnome-desktop \
	gnome-session \
	gnome-settings-daemon \
	gnome-shell-gsettings \
	gsettings-desktop-schemas \
	librsvg-gtk \
"

# The extensions app is a GJS application and pulls libadwaita at runtime only.
RDEPENDS:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'extensions-app', 'libadwaita', '', d)}"

PACKAGES =+ "${PN}-tools ${PN}-gsettings"
FILES:${PN}-tools = "${bindir}/*-tool"
RDEPENDS:${PN}-tools = "python3-core"

CVE_STATUS[CVE-2021-3982] = "not-applicable-config: OE doesn't set CAP_SYS_NICE capability"
