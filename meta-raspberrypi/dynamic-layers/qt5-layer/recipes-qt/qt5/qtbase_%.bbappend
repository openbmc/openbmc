PACKAGECONFIG_GL_rpi = "${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'gl', \
                        bb.utils.contains('DISTRO_FEATURES',     'opengl', 'eglfs gles2', \
                                                                       '', d), d)}"
PACKAGECONFIG_GL_append_rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', ' kms', '', d)}"
PACKAGECONFIG_GL_append_rpi = " gbm"
PACKAGECONFIG_FONTS_rpi = "fontconfig"
PACKAGECONFIG_append_rpi = " libinput examples tslib xkb xkbcommon-evdev"
PACKAGECONFIG_remove_rpi = "tests"

OE_QTBASE_EGLFS_DEVICE_INTEGRATION_rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', 'eglfs_brcm', d)}"

do_configure_prepend_rpi() {
    # Add the appropriate EGLFS_DEVICE_INTEGRATION
    if [ "${@d.getVar('OE_QTBASE_EGLFS_DEVICE_INTEGRATION')}" != "" ]; then
        echo "EGLFS_DEVICE_INTEGRATION = ${OE_QTBASE_EGLFS_DEVICE_INTEGRATION}" >> ${S}/mkspecs/oe-device-extra.pri
    fi
}
RDEPENDS_${PN}_append_rpi = " userland"
DEPENDS_append_rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', ' userland', d)}"
