SUMMARY = "Plymouth is a project from Fedora providing a flicker-free graphical boot process."

DESCRIPTION = "Plymouth is an application that runs very early in the boot process \
    (even before the root filesystem is mounted!) that provides a \
    graphical boot animation while the boot process happens in the background. \
"

HOMEPAGE = "http://www.freedesktop.org/wiki/Software/Plymouth"
SECTION = "base"

LICENSE = "GPL-2.0-or-later"

LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "libcap libpng cairo dbus udev"
DEPENDS:append:libc-musl = " musl-rpmatch"
PROVIDES = "virtual/psplash"
RPROVIDES:${PN} = "virtual-psplash virtual-psplash-support"

SRC_URI = " \
    http://www.freedesktop.org/software/plymouth/releases/${BPN}-${PV}.tar.xz \
    file://0001-Make-full-path-to-systemd-tty-ask-password-agent-con.patch \
    file://0001-systemd-switch-to-KillMode-mixed.patch \
    file://0001-plymouth-start-service-in-add-related-kernel-paramet.patch \
    file://0001-plymouth-Add-the-retain-splash-option.patch \
        "

SRC_URI[md5sum] = "8a25d23f3ae732af300a56fa33cacff2"

EXTRA_OECONF += " --enable-shared --disable-static --disable-gtk --disable-documentation \
    --with-logo=${LOGO} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--enable-systemd-integration --with-systemd-tty-ask-password-agent=${base_bindir}/systemd-tty-ask-password-agent', '--disable-systemd-integration', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge','--without-system-root-install','--with-system-root-install',d)} \
"

PACKAGECONFIG ??= "pango initrd"
PACKAGECONFIG:append:x86 = " drm"
PACKAGECONFIG:append:x86-64 = " drm"

PACKAGECONFIG[drm] = "--enable-drm,--disable-drm,libdrm"
PACKAGECONFIG[pango] = "--enable-pango,--disable-pango,pango"
PACKAGECONFIG[gtk] = "--enable-gtk,--disable-gtk,gtk+3"
PACKAGECONFIG[initrd] = ",,,"

LOGO ??= "${datadir}/plymouth/bizcom.png"

inherit autotools pkgconfig systemd gettext

LDFLAGS:append:libc-musl = " -lrpmatch"

do_install:append() {
    # Remove /var/run from package as plymouth will populate it on startup
    rm -fr "${D}${localstatedir}/run"

    if ! ${@bb.utils.contains('PACKAGECONFIG', 'initrd', 'true', 'false', d)}; then
        rm -rf "${D}${libexecdir}"
    fi
}

PACKAGES =. "${@bb.utils.contains('PACKAGECONFIG', 'initrd', '${PN}-initrd ', '', d)}"
PACKAGES =+ "${PN}-set-default-theme"

FILES:${PN}-initrd = "${libexecdir}/plymouth/*"
FILES:${PN}-set-default-theme = "${sbindir}/plymouth-set-default-theme"

FILES:${PN} += "${systemd_unitdir}/system/*"
FILES:${PN}-dbg += "${libdir}/plymouth/renderers/.debug"


RDEPENDS:${PN}-initrd = "bash dracut"
RDEPENDS:${PN}-set-default-theme = "bash"

SYSTEMD_SERVICE:${PN} = "plymouth-start.service"
