DESCRIPTION = "Blueman is a GTK+ Bluetooth Manager"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "gtk+3 glib-2.0 bluez5 python3-pygobject python3-cython-native"

inherit meson gettext systemd gsettings pkgconfig python3native gtk-icon-cache features_check python3targetconfig

REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"

SRC_URI = "git://github.com/blueman-project/blueman.git;protocol=https;branch=2-4-stable \
           file://0001-meson-DO-not-emit-absolute-path-when-S-B.patch"
S = "${WORKDIR}/git"
SRCREV = "7bcf919ad6ac0ee9a8c66b18b0ca98af877d4c8f"

EXTRA_OEMESON = "-Druntime_deps_check=false \
    -Dsystemdsystemunitdir=${systemd_system_unitdir} \
    -Dsystemduserunitdir=${systemd_user_unitdir} \
"

SYSTEMD_SERVICE:${PN} = "${BPN}-mechanism.service"
SYSTEMD_AUTO_ENABLE:${PN} = "disable"

RRECOMMENDS:${PN} += "adwaita-icon-theme"
RDEPENDS:${PN} += " \
    python3-core \
    python3-ctypes \
    python3-dbus \
    python3-pygobject \
    python3-terminal \
    python3-fcntl \
    packagegroup-tools-bluetooth \
"

PACKAGECONFIG ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'polkit pulseaudio ', d)} \
    thunar \
"
PACKAGECONFIG[thunar] = "-Dthunar-sendto=true,-Dthunar-sendto=false"
PACKAGECONFIG[pulseaudio] = "-Dpulseaudio=true,-Dpulseaudio=false"
PACKAGECONFIG[polkit] = "-Dpolicykit=true,-Dpolicykit=false"

FILES:${PN} += " \
    ${datadir} \
    ${systemd_user_unitdir} \
    ${systemd_system_unitdir} \
    ${PYTHON_SITEPACKAGES_DIR} \
"

# In code, path to python is a variable that is replaced with path to native version of it
# during the configure stage, e.g ../recipe-sysroot-native/usr/bin/python3-native/python3.
# Replace it with #!/usr/bin/env python3
do_install:append() {
    sed -i "1s/.*/#!\/usr\/bin\/env python3/" ${D}${prefix}/libexec/blueman-rfcomm-watcher \
                                              ${D}${prefix}/libexec/blueman-mechanism \
                                              ${D}${bindir}/blueman-adapters \
                                              ${D}${bindir}/blueman-applet \
                                              ${D}${bindir}/blueman-manager \
                                              ${D}${bindir}/blueman-sendto \
                                              ${D}${bindir}/blueman-services \
                                              ${D}${bindir}/blueman-tray
}
