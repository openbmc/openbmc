# if using bcm driver enable dispmanx not when using VC4 driver
PACKAGECONFIG:append:rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', ' dispmanx', d)}"
DEPENDS:append:rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', ' userland', d)}"

PACKAGECONFIG_GL_VC4GRAPHICS = "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'gles2 egl', '', d)}"
PACKAGECONFIG_GL:rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '${PACKAGECONFIG_GL_VC4GRAPHICS}', 'egl gles2', d)}"
