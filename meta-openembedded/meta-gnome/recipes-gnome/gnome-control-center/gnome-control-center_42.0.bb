SUMMARY = "GNOME Settings"
DESCRIPTION = "GNOME Settings is GNOME's main interface for configuration of various aspects of your desktop"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=75859989545e37968a99b631ef42722e"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gettext vala upstream-version-is-even bash-completion features_check

DEPENDS = " \
    gdk-pixbuf-native \
    colord-gtk \
    udisks2 \
    upower \
    polkit \
    pulseaudio \
    accountsservice \
    samba \
    gsettings-desktop-schemas \
    gnome-settings-daemon \
    gnome-desktop \
    gnome-online-accounts \
    libnma \
    gnome-bluetooth \
    grilo \
    libgtop \
    gsound \
    libpwquality \
    libadwaita \
"

REQUIRED_DISTRO_FEATURES += "polkit pulseaudio systemd x11"

SRC_URI[archive.sha256sum] = "0732efa7c4171ce0a0ed4106580b4ce35a57b10152c0ea36d239134913777eda"
SRC_URI += "file://0001-Add-meson-option-to-pass-sysroot.patch"

PACKAGECONFIG ??= "ibus ${@bb.utils.filter('DISTRO_FEATURES', 'wayland', d)}"
PACKAGECONFIG[ibus] = "-Dibus=true, -Dibus=false, ibus"
PACKAGECONFIG[wayland] = "-Dwayland=true, -Dwayland=false, wayland"

EXTRA_OEMESON = " \
    -Doe_sysroot=${STAGING_DIR_HOST} \
"

do_install:append() {
	# If polkit is setup fixup permissions and ownership
    if [ -d ${D}${datadir}/polkit-1/rules.d ]; then
        chmod 700 ${D}${datadir}/polkit-1/rules.d
        chown polkitd:root ${D}${datadir}/polkit-1/rules.d
    fi
}

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/gnome-shell \
    ${datadir}/metainfo \
"

FILES:${PN}-dev += "${datadir}/gettext"

RDEPENDS:${PN} += "gsettings-desktop-schemas"
