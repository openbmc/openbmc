require recipes-graphics/xorg-driver/xorg-driver-input.inc

SUMMARY = "X.Org X server -- tslib input driver"
LIC_FILES_CHKSUM = "file://COPYING;md5=f1524518264f7776a9707c19c8affbbf"

DEPENDS += "tslib"
RRECOMMENDS:${PN} += "tslib-calibrate"

SRC_URI = "https://github.com/merge/xf86-input-tslib/releases/download/${PV}/xf86-input-tslib-${PV}.tar.xz \
           file://99-xf86-input-tslib.rules \
"
UPSTREAM_CHECK_URI = "https://github.com/merge/xf86-input-tslib/tags"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)$"

SRC_URI[sha256sum] = "b596168c4ed2f1023212dc828ce49cbe82a0bbd1aac3c5e2958154d78870ca88"

do_install:append() {
    install -d ${D}${nonarch_base_libdir}/udev/rules.d
    install -m 0644 ${UNPACKDIR}/99-xf86-input-tslib.rules ${D}${nonarch_base_libdir}/udev/rules.d/
}

FILES:${PN} += "${nonarch_base_libdir}/udev"
