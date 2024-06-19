SUMMARY = "Generic Linux command-line automation tool (no X!)"
DESCRIPTION = "ydotool is not limited to Wayland. You can use it on anything as long as it accepts keyboard/mouse/whatever input."
LICENSE = "AGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=eb1e647870add0502f8f010b19de32af"

PV = "1.0.4+git"

SRC_URI = "git://github.com/ReimuNotMoe/ydotool;protocol=https;branch=master"
SRCREV = "0c295346d55afcc6aebaaee564333b3e1efabcbd"

S = "${WORKDIR}/git"

inherit cmake systemd

EXTRA_OECMAKE = "\
    -DBUILD_DOCS=OFF \
"

do_install:append() {
    if ! ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        rm -rf ${D}${systemd_user_unitdir}
        rmdir ${D}${nonarch_libdir}/systemd ${D}${nonarch_libdir}
    fi
}
SYSTEMD_SERVICE:${PN} = "ydotoold.service"
SYSTEMD_AUTO_ENABLE = "disable"
