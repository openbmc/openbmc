SUMMARY = "Window navigation construction toolkit"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gobject-introspection gettext features_check upstream-version-is-even

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

SRC_URI[archive.md5sum] = "102dc488a6a726e4050cf5ab7e967e8d"
SRC_URI[archive.sha256sum] = "3e33dbd319b562a5ab602dcab6de3ca81b85f8346672e90ec632b36bbf15ee4b"

UNKNOWN_CONFIGURE_WHITELIST = "introspection"

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
    smartcard \
    ${@bb.utils.filter('DISTRO_FEATURES', 'wayland', d)} \
"
PACKAGECONFIG[alsa] = "-Dalsa=true,-Dalsa=false,alsa-lib"
PACKAGECONFIG[cups] = "-Dcups=true,-Dcups=false,cups"
PACKAGECONFIG[gudev] = "-Dgudev=true,-Dgudev=false,libgudev"
PACKAGECONFIG[nm] = "-Dnetwork_manager=true,-Dnetwork_manager=false,networkmanager"
PACKAGECONFIG[smartcard] = "-Dsmartcard=true,-Dsmartcard=false,nss"
PACKAGECONFIG[wayland] = "-Dwayland=true,-Dwayland=false,wayland"

FILES_${PN} += " \
    ${systemd_user_unitdir} \
    ${libdir}/gnome-settings-daemon-3.0/libgsd.so \
"
