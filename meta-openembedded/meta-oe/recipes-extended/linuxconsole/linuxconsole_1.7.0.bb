SUMMARY = "Linux Console Project"
DESCRIPTION = "This project maintains the Linux Console tools, which include \
utilities to test and configure joysticks, connect legacy devices to the kernel's \
input subsystem (providing support for serial mice, touchscreens etc.), and test \
the input event layer."
HOMEPAGE = "https://sourceforge.net/projects/linuxconsole"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

SRC_URI = "\
    ${SOURCEFORGE_MIRROR}/linuxconsole/linuxconsoletools-${PV}.tar.bz2 \
    file://51-these-are-not-joysticks-rm.rules \
    file://60-joystick.rules \
    file://inputattachctl \
    file://inputattach.service \
"

SRC_URI[sha256sum] = "95d112f06393806116341d593bda002c8bc44119c1538407623268fed90d8c34"

S = "${WORKDIR}/linuxconsoletools-${PV}"

inherit systemd pkgconfig

EXTRA_OEMAKE = "DESTDIR=${D} PREFIX=${prefix} -C utils"
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'SYSTEMD_SUPPORT=1', '', d)}"

SYSTEMD_PACKAGES += "inputattach"
SYSTEMD_SERVICE_inputattach = "inputattach.service"
SYSTEMD_AUTO_ENABLE_inputattach = "enable"

PROVIDES += "joystick"

PACKAGECONFIG ??= "sdl"
PACKAGECONFIG[sdl] = ",,libsdl2"

do_compile() {
    if ! ${@bb.utils.contains('PACKAGECONFIG', 'sdl', 'true', 'false', d)}; then
        # drop ffmvforce so that we don't need libsdl2
        sed '/^PROGRAMS/s/ffmvforce *//g' -i ${S}/utils/Makefile
    fi
    # respect nonarch_base_libdir path to keep QA check happy
    sed 's#DESTDIR)/lib/udev#DESTDIR)/${nonarch_base_libdir}/udev#g' -i ${S}/utils/Makefile
    oe_runmake
}

do_install() {
    oe_runmake install

    install -Dm 0644 ${WORKDIR}/51-these-are-not-joysticks-rm.rules ${D}${nonarch_base_libdir}/udev/rules.d/51-these-are-not-joysticks-rm.rules
    install -Dm 0644 ${WORKDIR}/60-joystick.rules ${D}${nonarch_base_libdir}/udev/rules.d/60-joystick.rules

    install -Dm 0644 ${WORKDIR}/inputattach.service ${D}${systemd_system_unitdir}/inputattach.service
    install -Dm 0755 ${WORKDIR}/inputattachctl ${D}${bindir}/inputattachctl
}

PACKAGES += "inputattach joystick-jscal joystick"

# We won't package any file here as we are following the same packaging schema
# Debian does and we are splitting it in 'inputattach' and 'joystick' packages.
FILES_${PN} = ""

FILES_inputattach += "\
    ${bindir}/inputattach \
    ${bindir}/inputattachctl \
    ${systemd_system_unitdir}/inputattach.service \
"

FILES_joystick += "\
    ${bindir}/evdev-joystick \
    ${bindir}/ffcfstress \
    ${bindir}/ffmvforce \
    ${bindir}/ffset \
    ${bindir}/fftest \
    ${bindir}/jstest \
    ${nonarch_base_libdir}/udev/rules.d/51-these-are-not-joysticks-rm.rules \
    ${nonarch_base_libdir}/udev/js-set-enum-leds \
    ${nonarch_base_libdir}/udev/rules.d/60-joystick.rules \
    ${nonarch_base_libdir}/udev/rules.d/80-stelladaptor-joystick.rules \
"

FILES_joystick-jscal = " \
    ${datadir}/joystick \
    ${bindir}/jscal \
    ${bindir}/jscal-restore \
    ${bindir}/jscal-store \
"

RDEPENDS_inputattach += "inputattach-config"

RDEPENDS_joystick-jscal += "\
    bash \
    gawk \
"
