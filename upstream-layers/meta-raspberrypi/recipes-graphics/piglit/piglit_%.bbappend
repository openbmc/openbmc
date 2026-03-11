# mesa-demos need libgles1 and userland driver does not have it so remove it from piglit rdeps
RDEPENDS:${PN}:remove:rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', 'mesa-demos', d)}"
# it needs EGL >= 11 but userland says it provided version 10, remove it from build
# | --   Requested 'egl >= 11.0' but version of EGL is 10
COMPATIBLE_HOST:rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '(.*)', 'null', d)}"
