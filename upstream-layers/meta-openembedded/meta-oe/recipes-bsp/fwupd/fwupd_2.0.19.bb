SUMMARY = "A simple daemon to allow session software to update firmware"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "\
    curl \
    gcab \
    glib-2.0 \
    json-glib \
    hwdata \
    libjcat \
    libusb \
    libxmlb \
    python3-jinja2-native \
    vala-native \
"

SRC_URI = "\
    https://github.com/${BPN}/${BPN}/releases/download/${PV}/${BP}.tar.xz \
    file://run-ptest \
"
SRC_URI[sha256sum] = "3bb7a4a1e2d00f0ab513e4c667d7bf5a3ff34a9802757849d3fedf07dd40ddbb"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

# Machine-specific as we examine MACHINE_FEATURES to decide whether to build the UEFI plugins
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit meson vala gobject-introspection systemd bash-completion pkgconfig gi-docgen ptest manpages

GIDOCGEN_MESON_OPTION = 'docs'
GIDOCGEN_MESON_ENABLE_FLAG = 'enabled'
GIDOCGEN_MESON_DISABLE_FLAG = 'disabled'
GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'

EXTRA_OEMESON = "-Dvendor_ids_dir=${datadir}/hwdata"

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)} \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'bluetooth polkit', d)} \
    gnutls \
    hsi \
    plugin_flashrom \
    plugin_modem_manager \
    protobuf \
"

PACKAGECONFIG[bluetooth] = "-Dbluez=enabled,-Dbluez=disabled"
PACKAGECONFIG[firmware-packager] = "-Dfirmware-packager=true,-Dfirmware-packager=false"
PACKAGECONFIG[fish-completion] = "-Dfish_completion=true,-Dfish_completion=false"
PACKAGECONFIG[gnutls] = "-Dgnutls=enabled,-Dgnutls=disabled,gnutls"
PACKAGECONFIG[hsi] = "-Dhsi=enabled,-Dhsi=disabled"
PACKAGECONFIG[libarchive] = "-Dlibarchive=enabled,-Dlibarchive=disabled,libarchive"
PACKAGECONFIG[libdrm] = "-Dlibdrm=enabled,-Dlibdrm=disabled,libdrm"
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false"
PACKAGECONFIG[metainfo] = "-Dmetainfo=true,-Dmetainfo=false"
PACKAGECONFIG[polkit] = "-Dpolkit=enabled,-Dpolkit=disabled,polkit"
PACKAGECONFIG[readline] = "-Dreadline=enabled,-Dreadline=disabled,readline"
PACKAGECONFIG[systemd] = "-Dsystemd=enabled,-Dsystemd=disabled,systemd"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false,gcab-native"

# TODO plugins-all meta-option that expands to all plugin_*?
PACKAGECONFIG[plugin_flashrom] = "-Dplugin_flashrom=enabled,-Dplugin_flashrom=disabled,flashrom"
PACKAGECONFIG[protobuf] = "-Dprotobuf=enabled,-Dprotobuf=disabled,protobuf-c-native protobuf-c"
PACKAGECONFIG[plugin_modem_manager] = "-Dplugin_modem_manager=enabled,-Dplugin_modem_manager=disabled,libqmi modemmanager"
PACKAGECONFIG[plugin_uefi_capsule_splash] = "-Dplugin_uefi_capsule_splash=true,-Dplugin_uefi_capsule_splash=false,python3-pygobject"

FILES:${PN} += "\
    ${libdir}/fwupd-plugins-* \
    ${libdir}/fwupd-${PV} \
    ${systemd_unitdir} \
    ${nonarch_libdir}/sysusers.d/fwupd.conf \
    ${datadir}/fish \
    ${datadir}/metainfo \
    ${datadir}/icons \
    ${datadir}/dbus-1 \
    ${datadir}/polkit-1 \
    ${nonarch_libdir}/modules-load.d \
"

FILES:${PN}-ptest += "${libexecdir}/installed-tests/ \
                      ${datadir}/installed-tests/"
RDEPENDS:${PN}-ptest += "gnome-desktop-testing python3 dbus"

# ESP mounting, not strictly necessary
RRECOMMENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'polkit', 'udisks2', '', d)}"

INSANE_SKIP:${PN}-ptest += "buildpaths"
