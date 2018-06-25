EXTRA_OECMAKE_append_rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', ' -DUSE_GSTREAMER_GL=OFF ', '', d)}"
