OPENGL_PKGCONFIGS_rpi = "dri glx ${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', 'dri3 xshmfence glamor', '', d)}"
