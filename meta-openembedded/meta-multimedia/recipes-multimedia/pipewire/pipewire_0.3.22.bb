SUMMARY = "Multimedia processing server for Linux"
DESCRIPTION = "Linux server for handling and routing audio and video streams between applications and multimedia I/O devices"
HOMEPAGE = "https://pipewire.org/"
BUGTRACKER  = "https://gitlab.freedesktop.org/pipewire/pipewire/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=e2c0b7d86d04e716a3c4c9ab34260e69 \
    file://COPYING;md5=97be96ca4fab23e9657ffa590b931c1a \
"
SECTION = "multimedia"

DEPENDS = "dbus"

SRCREV = "22d563720a7f6ba7bdf59950f8c14488d80dfa95"
SRC_URI = "git://gitlab.freedesktop.org/pipewire/pipewire.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig systemd manpages gettext useradd

USERADD_PACKAGES = "${PN}"

GROUPADD_PARAM_${PN} = "--system pipewire"

USERADD_PARAM_${PN} = "--system --home / --no-create-home \
                       --comment 'PipeWire multimedia daemon' \
                       --gid pipewire --groups audio,video \
                       pipewire"

# For "EVL", look up https://evlproject.org/ . It involves
# a specially prepared kernel, and is currently unavailable
# in Yocto.
# FFmpeg and Vulkan aren't really supported - at the current
# stage (version 0.3.22), these are just experiments, not
# actual features.
# libcamera support currently does not build successfully.
# systemd user service files are disabled because per-user
# PipeWire instances aren't really something that makes
# much sense in an embedded environment. A system-wide
# instance does.
EXTRA_OEMESON += " \
    -Daudiotestsrc=true \
    -Devl=false \
    -Dsystemd-user-service=false \
    -Dtests=false \
    -Dudevrulesdir=${nonarch_base_libdir}/udev/rules.d/ \
    -Dvideotestsrc=true \
    -Dffmpeg=false \
    -Dvulkan=false \
    -Dlibcamera=false \
"

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez', '', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'alsa systemd', d)} \
    gstreamer jack v4l2 \
"

# "jack" and "pipewire-jack" packageconfigs cannot be both enabled,
# since "jack" imports libjack, and "pipewire-jack" generates
# libjack.so* files, thus colliding with the libpack package. This
# is why these two are marked in their respective packageconfigs
# as being in conflict.

PACKAGECONFIG[alsa] = "-Dalsa=true,-Dalsa=false,alsa-lib udev"
PACKAGECONFIG[bluez] = "-Dbluez5=true,-Dbluez5=false,bluez5 sbc"
PACKAGECONFIG[docs] = "-Ddocs=true,-Ddocs=false,doxygen"
PACKAGECONFIG[gstreamer] = "-Dgstreamer=true,-Dgstreamer=false,glib-2.0 gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[jack] = "-Djack=true,-Djack=false,jack,,,pipewire-jack"
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false,libxml-parser-perl-native"
PACKAGECONFIG[sdl2] = "-Dsdl2=enabled,-Dsdl2=disabled,virtual/libsdl2"
PACKAGECONFIG[sndfile] = "-Dsndfile=enabled,-Dsndfile=disabled,libsndfile1"
PACKAGECONFIG[systemd] = "-Dsystemd=true -Dsystemd-system-service=true ,-Dsystemd=false -Dsystemd-system-service=false,systemd"
PACKAGECONFIG[v4l2] = "-Dv4l2=true,-Dv4l2=false,udev"
PACKAGECONFIG[pipewire-alsa] = "-Dpipewire-alsa=true,-Dpipewire-alsa=false,alsa-lib"
PACKAGECONFIG[pipewire-jack] = "-Dpipewire-jack=true -Dlibjack-path=${libdir}/${PW_MODULE_SUBDIR}/jack,-Dpipewire-jack=false,jack,,,jack"

PACKAGESPLITFUNCS_prepend = " split_dynamic_packages "
PACKAGESPLITFUNCS_append = " set_dynamic_metapkg_rdepends "

SPA_SUBDIR = "spa-0.2"
PW_MODULE_SUBDIR = "pipewire-0.3"

remove_unused_installed_files() {
    # jack.conf is used by pipewire-jack (not the JACK SPA plugin).
    # Remove it if pipewire-jack is not built to avoid creating the
    # pipewire-jack package.
    if ${@bb.utils.contains('PACKAGECONFIG', 'pipewire-jack', 'false', 'true', d)}; then
        rm -f "${D}${sysconfdir}/pipewire/jack.conf"
    fi
}

do_install[postfuncs] += "remove_unused_installed_files"

python split_dynamic_packages () {
    # Create packages for each SPA plugin. These plugins are located
    # in individual subdirectories, so a recursive search is needed.
    spa_libdir = d.expand('${libdir}/${SPA_SUBDIR}')
    do_split_packages(d, spa_libdir, r'^libspa-(.*)\.so$', d.expand('${PN}-spa-plugins-%s'), 'PipeWire SPA plugin for %s', extra_depends='', recursive=True)

    # Create packages for each PipeWire module.
    pw_module_libdir = d.expand('${libdir}/${PW_MODULE_SUBDIR}')
    do_split_packages(d, pw_module_libdir, r'^libpipewire-module-(.*)\.so$', d.expand('${PN}-modules-%s'), 'PipeWire %s module', extra_depends='', recursive=False)
}

python set_dynamic_metapkg_rdepends () {
    import os
    import oe.utils

    # Go through all generated SPA plugin and PipeWire module packages
    # (excluding the main package and the -meta package itself) and
    # add them to the -meta package as RDEPENDS.

    base_pn = d.getVar('PN')

    spa_pn = base_pn + '-spa-plugins'
    spa_metapkg =  spa_pn + '-meta'

    pw_module_pn = base_pn + '-modules'
    pw_module_metapkg =  pw_module_pn + '-meta'

    d.setVar('ALLOW_EMPTY_' + spa_metapkg, "1")
    d.setVar('FILES_' + spa_metapkg, "")

    d.setVar('ALLOW_EMPTY_' + pw_module_metapkg, "1")
    d.setVar('FILES_' + pw_module_metapkg, "")

    blacklist = [ spa_pn, spa_metapkg, pw_module_pn, pw_module_metapkg ]
    spa_metapkg_rdepends = []
    pw_module_metapkg_rdepends = []
    pkgdest = d.getVar('PKGDEST')

    for pkg in oe.utils.packages_filter_out_system(d):
        if pkg in blacklist:
            continue

        is_spa_pkg = pkg.startswith(spa_pn)
        is_pw_module_pkg = pkg.startswith(pw_module_pn)
        if not is_spa_pkg and not is_pw_module_pkg:
            continue

        if pkg in spa_metapkg_rdepends or pkg in pw_module_metapkg_rdepends:
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
            if is_spa_pkg:
                spa_metapkg_rdepends.append(pkg)
            if is_pw_module_pkg:
                pw_module_metapkg_rdepends.append(pkg)

    d.setVar('RDEPENDS_' + spa_metapkg, ' '.join(spa_metapkg_rdepends))
    d.setVar('DESCRIPTION_' + spa_metapkg, spa_pn + ' meta package')

    d.setVar('RDEPENDS_' + pw_module_metapkg, ' '.join(pw_module_metapkg_rdepends))
    d.setVar('DESCRIPTION_' + pw_module_metapkg, pw_module_pn + ' meta package')
}

PACKAGES =+ "\
    libpipewire \
    ${PN}-tools \
    ${PN}-pulse \
    ${PN}-alsa \
    ${PN}-jack \
    ${PN}-media-session \
    ${PN}-spa-plugins \
    ${PN}-spa-plugins-meta \
    ${PN}-spa-tools \
    ${PN}-modules \
    ${PN}-modules-meta \
    ${PN}-alsa-card-profile \
    gstreamer1.0-pipewire \
"

PACKAGES_DYNAMIC = "^${PN}-spa-plugins.* ^${PN}-modules.*"

SYSTEMD_SERVICE_${PN} = "pipewire.service"
CONFFILES_${PN} += "${sysconfdir}/pipewire/pipewire.conf"
FILES_${PN} = " \
    ${sysconfdir}/pipewire/pipewire.conf \
    ${systemd_user_unitdir}/pipewire.* \
    ${bindir}/pipewire \
"

FILES_${PN}-dev += " \
    ${libdir}/${PW_MODULE_SUBDIR}/jack/libjack*.so \
"

CONFFILES_libpipewire += "${sysconfdir}/pipewire/client.conf"
FILES_libpipewire = " \
    ${sysconfdir}/pipewire/client.conf \
    ${libdir}/libpipewire-*.so.* \
"
# Add the bare minimum modules and plugins required to be able
# to use libpipewire. Without these, it is essentially unusable.
RDEPENDS_libpipewire += " \
    ${PN}-modules-client-node \
    ${PN}-modules-protocol-native \
    ${PN}-spa-plugins-support \
"

FILES_${PN}-tools = " \
    ${bindir}/pw-* \
"

# This is a shim daemon that is intended to be used as a
# drop-in PulseAudio replacement, providing a pulseaudio-compatible
# socket that can be used by applications that use libpulse.
CONFFILES_${PN}-pulse += "${sysconfdir}/pipewire/pipewire-pulse.conf"
FILES_${PN}-pulse = " \
    ${sysconfdir}/pipewire/pipewire-pulse.conf \
    ${systemd_user_unitdir}/pipewire-pulse.* \
    ${bindir}/pipewire-pulse \
"
RDEPENDS_${PN}-pulse += " \
    ${PN}-modules-protocol-pulse \
"

# alsa plugin to redirect audio to pipewire
FILES_${PN}-alsa = "\
    ${libdir}/alsa-lib/* \
    ${datadir}/alsa/alsa.conf.d/* \
"

# jack drop-in libraries to redirect audio to pipewire
CONFFILES_${PN}-jack = "${sysconfdir}/pipewire/jack.conf"
FILES_${PN}-jack = "\
    ${sysconfdir}/pipewire/jack.conf \
    ${libdir}/${PW_MODULE_SUBDIR}/jack/libjack*.so.* \
"

# Example session manager. Not intended for use in production.
CONFFILES_${PN}-media-session = "${sysconfdir}/pipewire/media-session.d/*"
FILES_${PN}-media-session = " \
    ${bindir}/pipewire-media-session \
    ${sysconfdir}/pipewire/media-session.d/* \
"
RPROVIDES_${PN}-media-session = "virtual/pipewire-sessionmanager"

# Dynamic packages (see set_dynamic_metapkg_rdepends).
FILES_${PN}-spa-plugins = ""
RRECOMMENDS_${PN}-spa-plugins += "${PN}-spa-plugins-meta"

FILES_${PN}-spa-tools = " \
    ${bindir}/spa-* \
"

# Dynamic packages (see set_dynamic_metapkg_rdepends).
FILES_${PN}-modules = ""
RRECOMMENDS_${PN}-modules += "${PN}-modules-meta"

CONFFILES_${PN}-modules-rtkit = "${sysconfdir}/pipewire/client-rt.conf"
FILES_${PN}-modules-rtkit += " \
    ${sysconfdir}/pipewire/client-rt.conf \
    "

FILES_${PN}-alsa-card-profile = " \
    ${datadir}/alsa-card-profile/* \
    ${nonarch_base_libdir}/udev/rules.d/90-pipewire-alsa.rules \
"

FILES_gstreamer1.0-pipewire = " \
    ${libdir}/gstreamer-1.0/* \
"
