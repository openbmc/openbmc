SUMMARY    = "Session / policy manager implementation for PipeWire"
HOMEPAGE   = "https://gitlab.freedesktop.org/pipewire/wireplumber"
BUGTRACKER = "https://gitlab.freedesktop.org/pipewire/wireplumber/issues"
SECTION    = "multimedia"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=17d1fe479cdec331eecbc65d26bc7e77"

DEPENDS = "glib-2.0 glib-2.0-native lua pipewire \
    ${@bb.utils.contains("DISTRO_FEATURES", "gobject-introspection-data", "python3-native python3-lxml-native doxygen-native", "", d)} \
"

SRCREV = "84429b47943d789389fbde17c06b82efb197d04e"
SRC_URI = " \
    git://gitlab.freedesktop.org/pipewire/wireplumber.git;branch=master;protocol=https;tag=${PV} \
    file://run-ptest \
    file://90-OE-disable-session-dbus-dependent-features.lua \
"


inherit meson pkgconfig gobject-introspection systemd bash-completion ptest

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
"

PACKAGECONFIG ??= " dbus \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd systemd-system-service systemd-user-service', '', d)} \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'test', '', d)} \
"

PACKAGECONFIG[systemd] = "-Dsystemd=enabled,-Dsystemd=disabled,systemd"
PACKAGECONFIG[systemd-system-service] = "-Dsystemd-system-service=true,-Dsystemd-system-service=false,systemd"
# "systemd-user-service" packageconfig will only install service
# files to rootfs but not enable them as systemd.bbclass
# currently lacks the feature of enabling user services.
PACKAGECONFIG[systemd-user-service] = "-Dsystemd-user-service=true,-Dsystemd-user-service=false,systemd"
PACKAGECONFIG[dbus] = ""
PACKAGECONFIG[test] = "-Dtests=true,-Dtests=false"

PACKAGESPLITFUNCS:prepend = " split_dynamic_packages "
PACKAGESPLITFUNCS:append = " set_dynamic_metapkg_rdepends "

WP_MODULE_SUBDIR = "wireplumber-0.5"

do_install:append() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'dbus', 'false', 'true', d)}; then
        install -m 0644 ${UNPACKDIR}/90-OE-disable-session-dbus-dependent-features.lua ${D}${datadir}/wireplumber/main.lua.d
    fi
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/data/config
    cd ${B}/tests
    find . -maxdepth 2 -type f -executable -exec install -D {} ${D}${PTEST_PATH}/{} \;

    install -m 644 ${S}/tests/wp/component-loader.conf ${D}${PTEST_PATH}/data/
    cp -r ${S}/tests/scripts/scripts ${D}${PTEST_PATH}/scripts/
    cp -r ${S}/tests/wplua/scripts ${D}${PTEST_PATH}/wplua/
    cp -r ${S}/tests/wp/conf ${D}${PTEST_PATH}/data
    cp -r ${S}/tests/wp/settings ${D}${PTEST_PATH}/data
    install -m 644 ${S}/src/config/wireplumber.conf ${D}${PTEST_PATH}/data/config/
    install -Dm 644 ${S}/tests/wplua/scripts/lib/testlib.lua ${D}${datadir}/wireplumber/scripts/lib/testlib.lua
    install -Dm 644 ${S}/tests/scripts/scripts/lib/test-utils.lua ${D}${datadir}/wireplumber/scripts/lib/test-utils.lua

    # this is not a test
    rm -rf ${D}${PTEST_PATH}/examples

    # Beside regular bianry executables, this package comes with lua tests also
    # which need to be executed with specific parameters. Take the parameters
    # from the meson.build files, and read them from run-ptest script.
    grep args ${S}/tests/wplua/meson.build   | cut -d[ -f2 | cut -d] -f1 | tr -d ",'" > ${D}${PTEST_PATH}/wplua/ptest-list
    grep args ${S}/tests/scripts/meson.build | cut -d[ -f2 | cut -d] -f1 | tr -d ",'" > ${D}${PTEST_PATH}/scripts/ptest-list
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

FILES:${PN}-ptest += "${datadir}/wireplumber/scripts/lib/test-utils.lua ${datadir}/wireplumber/scripts/testlib.lua"
RDEPENDS:${PN}-ptest += "pipewire-modules-protocol-native ${PN}-scripts"
