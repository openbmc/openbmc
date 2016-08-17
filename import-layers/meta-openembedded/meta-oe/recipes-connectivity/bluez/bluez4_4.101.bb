require bluez4.inc

PNBLACKLIST[bluez4] ?= "${@bb.utils.contains('DISTRO_FEATURES', 'bluez5', 'bluez5 conflicts with bluez4 and bluez5 is selected in DISTRO_FEATURES', '', d)}"

PR = "r11"

SRC_URI += "file://bluetooth.conf \
            file://sbc_mmx.patch \
            file://fix-udev-paths.patch \
            file://obsolete_automake_macros.patch \
            file://network-fix-network-Connect-method-parameters.patch \
            file://install-test-script.patch \
            file://fix_encrypt_collision.patch \
"

SRC_URI[md5sum] = "fb42cb7038c380eb0e2fa208987c96ad"
SRC_URI[sha256sum] = "59738410ade9f0e61a13c0f77d9aaffaafe49ba9418107e4ad75fe52846f7487"

RCONFLICTS_${PN} = "bluez5"

do_install_append() {
    install -m 0644 ${S}/audio/audio.conf ${D}/${sysconfdir}/bluetooth/
    install -m 0644 ${S}/network/network.conf ${D}/${sysconfdir}/bluetooth/
    install -m 0644 ${S}/input/input.conf ${D}/${sysconfdir}/bluetooth/
    # at_console doesn't really work with the current state of OE, so punch some more holes so people can actually use BT
    install -m 0644 ${WORKDIR}/bluetooth.conf ${D}/${sysconfdir}/dbus-1/system.d/
}

RDEPENDS_${PN}-dev = "bluez-hcidump"
RDEPENDS_${PN}-testtools += "python python-dbus python-pygobject"

ALLOW_EMPTY_libasound-module-bluez = "1"
PACKAGES =+ "libasound-module-bluez ${PN}-testtools"

FILES_libasound-module-bluez = "${libdir}/alsa-lib/lib*.so ${datadir}/alsa"
FILES_${PN} += "${libdir}/bluetooth/plugins ${libdir}/bluetooth/plugins/*.so ${base_libdir}/udev/ ${nonarch_base_libdir}/udev/ ${systemd_unitdir}/ ${datadir}/dbus-1"
FILES_${PN}-dev += "\
    ${libdir}/bluetooth/plugins/*.la \
    ${libdir}/alsa-lib/*.la \
"

FILES_${PN}-testtools = "${libdir}/bluez4/test/*"

FILES_${PN}-dbg += "\
    ${libdir}/bluetooth/plugins/.debug \
    ${libdir}/*/.debug \
    */udev/.debug \
"

SYSTEMD_SERVICE_${PN} = "bluetooth.service"
