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
    file://0001-Drop-libdl-references.patch \
    file://0001-Avoid-linking-to-plymouth_logo_file.patch \
    file://0001-Make-themes-build-optional.patch \
"

SRC_URI[sha256sum] = "f3f7841358c98f5e7b06a9eedbdd5e6882fd9f38bbd14a767fb083e3b55b1c34"

PLYMOUTH_RUNSTATEDIR ??= "${base_prefix}/run"
PLYMOUTH_RELEASE_FILE ??= "${sysconfdir}/system-release"

PLYMOUTH_BACKGROUND_COLOR ??= "0x5d5950"
PLYMOUTH_BACKGROUND_START_COLOR_STOP ??= "0x807c71"
PLYMOUTH_BACKGROUND_END_COLOR_STOP ??= "0x3a362f"

PLYMOUTH_BOOT_TTY ??= "/dev/tty1"
PLYMOUTH_SHUTDOWN_TTY ??= "/dev/tty63"

PLYMOUTH_THEMES ??= "spinfinity fade-in text details solar glow script spinner tribar bgrt"

EXTRA_OEMESON += " \
    -Drunstatedir=${PLYMOUTH_RUNSTATEDIR} \
    -Drelease-file=${PLYMOUTH_RELEASE_FILE} \
    -Dbackground-color=${PLYMOUTH_BACKGROUND_COLOR} \
    -Dbackground-start-color-stop=${PLYMOUTH_BACKGROUND_START_COLOR_STOP} \
    -Dbackground-end-color-stop=${PLYMOUTH_BACKGROUND_END_COLOR_STOP} \
    -Dboot-tty=${PLYMOUTH_BOOT_TTY} \
    -Dshutdown-tty=${PLYMOUTH_SHUTDOWN_TTY} \
"

PACKAGECONFIG ??= "initrd freetype pango udev ${PLYMOUTH_THEMES} ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG:append:x86 = " drm"
PACKAGECONFIG:append:x86-64 = " drm"

PACKAGECONFIG[drm] = "-Ddrm=true,-Ddrm=false,libdrm"
PACKAGECONFIG[docs] = "-Ddocs=true,-Ddocs=false"
PACKAGECONFIG[freetype] = "-Dfreetype=enabled,-Dfreetype=disabled,freetype"
PACKAGECONFIG[initrd] = ",,"
PACKAGECONFIG[gtk] = "-Dgtk=enabled,-Dgtk=disabled,gtk+3"
PACKAGECONFIG[pango] = "-Dpango=enabled,-Dpango=disabled,pango cairo"
PACKAGECONFIG[systemd] = "-Dsystemd-integration=true ,-Dsystemd-integration=false,systemd"
PACKAGECONFIG[tracing] = "-Dtracing=true,-Dtracing=false"
PACKAGECONFIG[udev] = "-Dudev=enabled,-Dudev=disabled,udev"
PACKAGECONFIG[upstart-monitoring] = "-Dupstart-monitoring=true,-Dupstart-monitoring=false,ncurses dbus"

# theme configs
PACKAGECONFIG[spinfinity] = "-Dspinfinity-theme=true,-Dspinfinity-theme=false"
PACKAGECONFIG[fade-in] = "-Dfade-in-theme=true,-Dfade-in-theme=false"
PACKAGECONFIG[text] = "-Dtext-theme=true,-Dtext-theme=false"
PACKAGECONFIG[details] = "-Ddetails-theme=true,-Ddetails-theme=false"
PACKAGECONFIG[solar] = "-Dsolar-theme=true,-Dsolar-theme=false"
PACKAGECONFIG[glow] = "-Dglow-theme=true,-Dglow-theme=false"
PACKAGECONFIG[script] = "-Dscript-theme=true,-Dscript-theme=false"
PACKAGECONFIG[spinner] = "-Dspinner-theme=true,-Dspinner-theme=false"
PACKAGECONFIG[tribar] = "-Dtribar-theme=true,-Dtribar-theme=false"
PACKAGECONFIG[bgrt] = "-Dbgrt-theme=true,-Dbgrt-theme=false"

inherit meson pkgconfig systemd gettext

do_install:append() {
    # Remove ${PLYMOUTH_RUNSTATEDIR} from package as plymouth will populate it on startup
    rm -fr ${D}${PLYMOUTH_RUNSTATEDIR}

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

DEPENDS = "libcap libpng libxkbcommon xkeyboard-config libevdev"
DEPENDS:append:libc-musl = " musl-rpmatch"

LDFLAGS:append:libc-musl = " -lrpmatch"

RDEPENDS:${PN}-initrd = "bash dracut"
RDEPENDS:${PN}-set-default-theme = "bash"

SYSTEMD_SERVICE:${PN} = "plymouth-start.service"
