OPENGL_PKGCONFIGS:rpi = "dri glx ${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', 'dri3 glamor', '', d)}"

# when using userland graphic KHR/khrplatform.h is provided by userland but virtual/libgl is provided by mesa-gl where
# we explicitly delete KHR/khrplatform.h since its already coming from userland package
DEPENDS:append:rpi = " ${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', 'userland', d)}"
