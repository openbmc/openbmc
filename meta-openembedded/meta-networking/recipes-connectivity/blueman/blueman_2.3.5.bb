DESCRIPTION = "Blueman is a GTK+ Bluetooth Manager"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "gtk+3 glib-2.0 bluez5 python3-pygobject python3-cython-native"

inherit meson gettext systemd gsettings pkgconfig python3native gtk-icon-cache useradd features_check

REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"

SRC_URI = " \
    git://github.com/blueman-project/blueman.git;protocol=https;branch=2-3-stable \
    file://0001-Search-for-cython3.patch \
    file://0002-fix-fail-to-enable-bluetooth.patch \
    file://0001-meson-add-pythoninstalldir-option.patch \
"
S = "${WORKDIR}/git"
SRCREV = "c85e7afb8d6547d4c35b7b639124de8e999c3650"

EXTRA_OEMESON = "-Druntime_deps_check=false -Dpythoninstalldir=${@noprefix('PYTHON_SITEPACKAGES_DIR', d)}"

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

do_install:append() {
    install -d ${D}${datadir}/polkit-1/rules.d
    cat >${D}${datadir}/polkit-1/rules.d/51-blueman.rules <<EOF
/* Allow users in wheel group to use blueman feature requiring root without authentication */
polkit.addRule(function(action, subject) {
    if ((action.id == "org.blueman.network.setup" ||
         action.id == "org.blueman.dhcp.client" ||
         action.id == "org.blueman.rfkill.setstate" ||
         action.id == "org.blueman.pppd.pppconnect") &&
        subject.isInGroup("wheel")) {

        return polkit.Result.YES;
    }
});
EOF
}

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --user-group --home-dir ${sysconfdir}/polkit-1 --shell /bin/nologin polkitd"

do_install:append() {
        # Fix up permissions on polkit rules.d to work with rpm4 constraints
        chmod 700 ${D}/${datadir}/polkit-1/rules.d
        chown polkitd:root ${D}/${datadir}/polkit-1/rules.d
}
