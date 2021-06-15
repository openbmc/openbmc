require recipes-graphics/xorg-driver/xorg-driver-input.inc
SUMMARY = "X.Org X server -- tslib input driver"
DEPENDS += "tslib"
RRECOMMENDS_${PN} += "tslib-calibrate"

LIC_FILES_CHKSUM = "file://COPYING;md5=f1524518264f7776a9707c19c8affbbf"

SRC_URI = "https://github.com/merge/xf86-input-tslib/releases/download/${PV}/xf86-input-tslib-${PV}.tar.xz \
           file://99-xf86-input-tslib.rules \
"

SRC_URI[md5sum] = "c5ffb03bccccfa1c4ba11079fef0036e"
SRC_URI[sha256sum] = "1439a9efa50eb481e6a0ab5319ab0765d457732e7da64e3c15f3c0cd13b44297"

do_install_append() {
    install -d ${D}${nonarch_base_libdir}/udev/rules.d
    install -m 0644 ${WORKDIR}/99-xf86-input-tslib.rules ${D}${nonarch_base_libdir}/udev/rules.d/
}

FILES_${PN} += "${nonarch_base_libdir}/udev"
