SUMMARY = "A simple daemon to allow session software to update firmware"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "\
    curl \
    gcab \
    glib-2.0 \
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
SRC_URI[sha256sum] = "472e9426f7a1b18fa9d199666c15482d4ee51ea35e916ca53bb3ca25919edb10"

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
    ${@bb.utils.contains_any('TARGET_ARCH', 'i386 i486 i586 i686 x86_64', 'hsi', '', d)} \
    gnutls \
    plugin_modem_manager \
"

PACKAGECONFIG[bluetooth] = "-Dbluez=enabled,-Dbluez=disabled"
PACKAGECONFIG[firmware-packager] = "-Dfirmware-packager=true,-Dfirmware-packager=false"
PACKAGECONFIG[fish-completion] = "-Dfish_completion=true,-Dfish_completion=false"
PACKAGECONFIG[gnutls] = "-Dgnutls=enabled,-Dgnutls=disabled,gnutls"
# HSI is only implemented for x86. Since 2.1.x meson.build gates it with
# .require() rather than .disable_auto_if(), so asking for it anywhere else is a
# hard error ("Feature hsi cannot be enabled") instead of being quietly ignored,
# hence the architecture check on the default above.
PACKAGECONFIG[hsi] = "-Dhsi=enabled,-Dhsi=disabled"
PACKAGECONFIG[libdrm] = "-Dlibdrm=enabled,-Dlibdrm=disabled,libdrm"
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false"
PACKAGECONFIG[metainfo] = "-Dmetainfo=true,-Dmetainfo=false"
PACKAGECONFIG[polkit] = "-Dpolkit=enabled,-Dpolkit=disabled,polkit"
PACKAGECONFIG[readline] = "-Dreadline=enabled,-Dreadline=disabled,readline"
PACKAGECONFIG[systemd] = "-Dsystemd=enabled,-Dsystemd=disabled,systemd"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false,gcab-native"

# TODO plugins-all meta-option that expands to all plugin_*?
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
RDEPENDS:${PN}-ptest += "gnome-desktop-testing python3 ${VIRTUAL-RUNTIME_dbus}"

# ESP mounting, not strictly necessary
RRECOMMENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'polkit', 'udisks2', '', d)}"

INSANE_SKIP:${PN}-ptest += "buildpaths"
