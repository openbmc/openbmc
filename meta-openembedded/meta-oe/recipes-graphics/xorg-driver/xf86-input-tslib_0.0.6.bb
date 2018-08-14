require recipes-graphics/xorg-driver/xorg-driver-input.inc
SUMMARY = "X.Org X server -- tslib input driver"
DEPENDS += "tslib"
RRECOMMENDS_${PN} += "tslib-calibrate"
RSUGGESTS_${PN} += "hal"

# derived from xf86-input-void, that's why I kept MIT-X, but it's not clear, see COPYING
LIC_FILES_CHKSUM = "file://src/tslib.c;endline=28;md5=bd62eaef222dcf5cd59e490a12bd795e \
                    file://COPYING;md5=4641deddaa80fe7ca88e944e1fd94a94"

PR = "${INC_PR}.1"

SRC_URI = "http://www.pengutronix.de/software/xf86-input-tslib/download/xf86-input-tslib-${PV}.tar.bz2 \
           file://double-free-crash.patch \
           file://10-x11-input-tslib.fdi \
           file://xserver-174-XGetPointerControl.patch \
           file://99-xf86-input-tslib.rules \
           file://xf86-input-tslib-port-ABI-12-r48.patch \
           file://xf86-input-tslib-0.0.6-xf86XInputSetScreen.patch \
"

SRC_URI[md5sum] = "b7a4d2f11637ee3fcf432e044b1d017f"
SRC_URI[sha256sum] = "5f46fdef095a6e44a69e0f0b57c7d665224b26d990d006611236d8332e85b105"

do_configure_prepend() {
    rm -rf ${S}/m4/ || true
}
do_install_append() {
    install -d ${D}/${datadir}/hal/fdi/policy/20thirdparty
    install -m 0644 ${WORKDIR}/10-x11-input-tslib.fdi ${D}/${datadir}/hal/fdi/policy/20thirdparty
    install -d ${D}${nonarch_base_libdir}/udev/rules.d
    install -m 0644 ${WORKDIR}/99-xf86-input-tslib.rules ${D}${nonarch_base_libdir}/udev/rules.d/
}

FILES_${PN} += "${datadir}/hal ${nonarch_base_libdir}/udev"
