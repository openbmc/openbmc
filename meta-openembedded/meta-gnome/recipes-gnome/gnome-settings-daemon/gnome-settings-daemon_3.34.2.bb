SUMMARY = "Window navigation construction toolkit"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gobject-introspection gettext features_check upstream-version-is-even

SRC_URI[archive.md5sum] = "528b0b7cc2dd22c6026a9c8739c71fa7"
SRC_URI[archive.sha256sum] = "7ce4979817866911a94ecb75b36db56797e038c0c524c5c1a81aefccafc17337"

DEPENDS = " \
    colord \
    geocode-glib \
    gcr \
    gnome-desktop3 \
    libgweather \
    lcms \
    libcanberra \
    geoclue \
    libnotify \
    upower \
    libwacom \
    virtual/libx11 \
"

# all these are mandatory
REQUIRED_DISTRO_FEATURES = "x11 polkit pulseaudio systemd gobject-introspection-data"
UNKNOWN_CONFIGURE_WHITELIST_append = " introspection"

SRC_URI[archive.md5sum] = "493332fa0f36645188468fed41c0060b"
SRC_URI[archive.sha256sum] = "9fbae67e217e53b99e4f9e7d392c91ffbe31253941c9b136ef09c2d9db7ad7ed"

# allow cross build mixed with build of native tools
do_write_config_append() {
    cat >${WORKDIR}/meson.native <<EOF
[binaries]
pkgconfig = 'pkg-config-native'
EOF
}
EXTRA_OEMESON = "--native-file ${WORKDIR}/meson.native"

PACKAGECONFIG ??= " \
    cups nm \
    alsa gudev \
    ${@bb.utils.filter('DISTRO_FEATURES', 'wayland', d)} \
"
PACKAGECONFIG[alsa] = "-Dalsa=true,-Dalsa=false,alsa-lib"
PACKAGECONFIG[cups] = "-Dcups=true,-Dcups=false,cups"
PACKAGECONFIG[gudev] = "-Dgudev=true,-Dgudev=false,libgudev"
PACKAGECONFIG[nm] = "-Dnetwork_manager=true,-Dnetwork_manager=false,networkmanager"
PACKAGECONFIG[wayland] = "-Dwayland=true,-Dwayland=false,wayland"

FILES_${PN} += " \
    ${systemd_user_unitdir} \
    ${libdir}/gnome-settings-daemon-3.0/libgsd.so \
"

RDEPEND_${PN} += "gdbus"
