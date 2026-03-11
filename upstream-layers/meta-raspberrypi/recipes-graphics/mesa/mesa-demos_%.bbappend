# mesa-demos userland driver doesn't provide libgles1 and the EGL headers it provides break the mesa-demos build.
# And enabling the `wayland` option without enabling `egl` is useless.
PACKAGECONFIG:remove:rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', 'egl gles1 wayland', d)}"
