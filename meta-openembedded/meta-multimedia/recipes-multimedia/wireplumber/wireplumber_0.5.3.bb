SUMMARY    = "Session / policy manager implementation for PipeWire"
HOMEPAGE   = "https://gitlab.freedesktop.org/pipewire/wireplumber"
BUGTRACKER = "https://gitlab.freedesktop.org/pipewire/wireplumber/issues"
SECTION    = "multimedia"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=17d1fe479cdec331eecbc65d26bc7e77"

DEPENDS = "glib-2.0 glib-2.0-native lua pipewire \
    ${@bb.utils.contains("DISTRO_FEATURES", "gobject-introspection-data", "python3-native python3-lxml-native doxygen-native", "", d)} \
"

SRCREV = "65e4ae83b994616401fc5859e00d5051b72518ba"
SRC_URI = " \
    git://gitlab.freedesktop.org/pipewire/wireplumber.git;branch=master;protocol=https \
    file://90-OE-disable-session-dbus-dependent-features.lua \
"

S = "${WORKDIR}/git"

inherit meson pkgconfig gobject-introspection systemd

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'

# Enable system-lua to let wireplumber use OE's lua.
# Documentation needs python-sphinx, which is not in oe-core or meta-python2 for now.
# elogind is not (yet) available in OE, so disable support.
EXTRA_OEMESON += " \
    -Ddoc=disabled \
    -Dsystem-lua=true \
    -Delogind=disabled \
    -Dsystemd-system-unit-dir=${systemd_system_unitdir} \
    -Dsystemd-user-unit-dir=${systemd_user_unitdir} \
    -Dtests=false \
"

PACKAGECONFIG ??= " dbus \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd systemd-system-service systemd-user-service', '', d)} \
"

PACKAGECONFIG[systemd] = "-Dsystemd=enabled,-Dsystemd=disabled,systemd"
PACKAGECONFIG[systemd-system-service] = "-Dsystemd-system-service=true,-Dsystemd-system-service=false,systemd"
# "systemd-user-service" packageconfig will only install service
# files to rootfs but not enable them as systemd.bbclass
# currently lacks the feature of enabling user services.
PACKAGECONFIG[systemd-user-service] = "-Dsystemd-user-service=true,-Dsystemd-user-service=false,systemd"
PACKAGECONFIG[dbus] = ""

PACKAGESPLITFUNCS:prepend = " split_dynamic_packages "
PACKAGESPLITFUNCS:append = " set_dynamic_metapkg_rdepends "

WP_MODULE_SUBDIR = "wireplumber-0.5"

do_install:append() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'dbus', 'false', 'true', d)}; then
        install -m 0644 ${UNPACKDIR}/90-OE-disable-session-dbus-dependent-features.lua ${D}${datadir}/wireplumber/main.lua.d
    fi
}

python split_dynamic_packages () {
    # Create packages for each WirePlumber module.
    wp_module_libdir = d.expand('${libdir}/${WP_MODULE_SUBDIR}')
    do_split_packages(d, wp_module_libdir, r'^libwireplumber-module-(.*)\.so$', d.expand('${PN}-modules-%s'), 'WirePlumber %s module', extra_depends='', recursive=False)
}

python set_dynamic_metapkg_rdepends () {
    import os
    import oe.utils

    # Go through all generated WirePlumber module packages
    # (excluding the main package and the -meta package itself)
    # and add them to the -meta package as RDEPENDS.

    base_pn = d.getVar('PN')

    wp_module_pn = base_pn + '-modules'
    wp_module_metapkg =  wp_module_pn + '-meta'

    d.setVar('ALLOW_EMPTY:' + wp_module_metapkg, "1")
    d.setVar('FILES:' + wp_module_metapkg, "")

    blacklist = [ wp_module_pn, wp_module_metapkg ]
    wp_module_metapkg_rdepends = []
    pkgdest = d.getVar('PKGDEST')

    for pkg in oe.utils.packages_filter_out_system(d):
        if pkg in blacklist:
            continue

        is_wp_module_pkg = pkg.startswith(wp_module_pn)
        if not is_wp_module_pkg:
            continue

        if pkg in wp_module_metapkg_rdepends:
            continue

        # See if the package is empty by looking at the contents of its
        # PKGDEST subdirectory. If this subdirectory is empty, then then
        # package is empty as well. Empty packages do not get added to
        # the meta package's RDEPENDS.
        pkgdir = os.path.join(pkgdest, pkg)
        if os.path.exists(pkgdir):
            dir_contents = os.listdir(pkgdir) or []
        else:
            dir_contents = []
        is_empty = len(dir_contents) == 0
        if not is_empty:
            if is_wp_module_pkg:
                wp_module_metapkg_rdepends.append(pkg)

    d.setVar('RDEPENDS:' + wp_module_metapkg, ' '.join(wp_module_metapkg_rdepends))
    d.setVar('DESCRIPTION:' + wp_module_metapkg, wp_module_pn + ' meta package')
}

PACKAGES =+ "\
    libwireplumber \
    ${PN}-default-config \
    ${PN}-scripts \
    ${PN}-modules \
    ${PN}-modules-meta \
"

PACKAGES_DYNAMIC = "^${PN}-modules.*"

CONFFILES:${PN} += " \
    ${datadir}/wireplumber/wireplumber.conf \
    ${datadir}/wireplumber/*.lua.d/* \
"
# Add pipewire to RRECOMMENDS, since WirePlumber expects a PipeWire daemon to
# be present. While in theory any application that uses libpipewire can configure
# itself to become a daemon, in practice, the PipeWire daemon is used.
RRECOMMENDS:${PN} += "pipewire ${PN}-scripts ${PN}-modules-meta"

FILES:${PN} += "${systemd_user_unitdir} ${systemd_system_unitdir} ${datadir}/zsh"

FILES:libwireplumber = " \
    ${libdir}/libwireplumber-*.so.* \
"

FILES:${PN}-scripts += "${datadir}/wireplumber/scripts/*"

# Dynamic packages (see set_dynamic_metapkg_rdepends).
FILES:${PN}-modules = ""
RRECOMMENDS:${PN}-modules += "${PN}-modules-meta"
