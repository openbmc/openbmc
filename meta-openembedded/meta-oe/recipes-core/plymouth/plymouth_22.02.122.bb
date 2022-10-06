SUMMARY = "Plymouth is a project from Fedora providing a flicker-free graphical boot process."
DESCRIPTION = "Plymouth is an application that runs very early in the boot process \
(even before the root filesystem is mounted!) that provides a \
graphical boot animation while the boot process happens in the background."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/Plymouth"
SECTION = "base"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = " \
    http://www.freedesktop.org/software/plymouth/releases/${BPN}-${PV}.tar.xz \
    file://0001-Make-full-path-to-systemd-tty-ask-password-agent-con.patch \
    file://0001-plymouth-start-service-in-add-related-kernel-paramet.patch \
    file://0001-plymouth-Add-the-retain-splash-option.patch \
    file://0001-Use-standard-runstatedir-vs-custom-flag.patch \
    file://0001-Fix-daemon-install-ignoring-configured-runstatedir.patch \
"

SRC_URI[sha256sum] = "100551442221033ce868c447ad6c74d831d209c18ae232b98ae0207e34eadaeb"

LOGO ??= "${datadir}/plymouth/bizcom.png"
RUNSTATEDIR ??= "${localstatedir}/run"

EXTRA_OECONF = "--runstatedir=${RUNSTATEDIR}"

PACKAGECONFIG ??= "initrd logo pango udev ${@bb.utils.filter('DISTRO_FEATURES', 'systemd usrmerge', d)}"
PACKAGECONFIG:append:x86 = " drm"
PACKAGECONFIG:append:x86-64 = " drm"

PACKAGECONFIG[drm] = "--enable-drm,--disable-drm,libdrm"
PACKAGECONFIG[documentation] = "--enable-documentation,--disable-documentation"
PACKAGECONFIG[initrd] = ",,"
PACKAGECONFIG[gtk] = "--enable-gtk,--disable-gtk,gtk+3"
PACKAGECONFIG[logo] = "--with-logo=${LOGO},--without-logo"
PACKAGECONFIG[pango] = "--enable-pango,--disable-pango,pango"
PACKAGECONFIG[systemd] = "--enable-systemd-integration --with-systemd-tty-ask-password-agent=${base_bindir}/systemd-tty-ask-password-agent,--disable-systemd-integration,systemd"
PACKAGECONFIG[udev] = "--with-udev,--without-udev,udev"
PACKAGECONFIG[upstart-monitoring] = "--enable-upstart-monitoring,--disable-upstart-monitoring,ncurses dbus"
PACKAGECONFIG[usrmerge] = "--without-system-root-install,--with-system-root-install"

inherit autotools pkgconfig systemd gettext

do_install:append() {
    # Remove /var/run from package as plymouth will populate it on startup
    rm -fr ${D}${RUNSTATEDIR}

    if ! ${@bb.utils.contains('PACKAGECONFIG', 'initrd', 'true', 'false', d)}; then
        rm -rf "${D}${libexecdir}"
    fi
}

PROVIDES = "virtual/psplash"
RPROVIDES:${PN} = "virtual-psplash virtual-psplash-support"

PACKAGES =. "${@bb.utils.contains('PACKAGECONFIG', 'initrd', '${PN}-initrd ', '', d)}"
PACKAGES =+ "${PN}-set-default-theme"

FILES:${PN}-initrd = "${libexecdir}/plymouth/*"
FILES:${PN}-set-default-theme = "${sbindir}/plymouth-set-default-theme"

FILES:${PN} += "${systemd_unitdir}/system/*"
FILES:${PN}-dbg += "${libdir}/plymouth/renderers/.debug"

DEPENDS = "libcap libpng"
DEPENDS:append:libc-musl = " musl-rpmatch"

LDFLAGS:append:libc-musl = " -lrpmatch"

RDEPENDS:${PN}-initrd = "bash dracut"
RDEPENDS:${PN}-set-default-theme = "bash"

SYSTEMD_SERVICE:${PN} = "plymouth-start.service"
