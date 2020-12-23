FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# when using userland graphic KHR/khrplatform.h is provided by userland but virtual/libgl is provided by mesa-gl where
# we explicitly delete KHR/khrplatform.h since its already coming from userland package
DEPENDS_append_rpi = " ${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', 'userland', d)}"
