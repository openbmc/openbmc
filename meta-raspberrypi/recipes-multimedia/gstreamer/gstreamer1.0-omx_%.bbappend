GSTREAMER_1_0_OMX_TARGET_rpi = "rpi"
GSTREAMER_1_0_OMX_CORE_NAME_rpi = "${libdir}/libopenmaxil.so"
EXTRA_OEMESON_append_rpi = " -Dheader_path=${STAGING_DIR_TARGET}/usr/include"
CFLAGS_append_rpi = " \
    -I${STAGING_DIR_TARGET}/usr/include/IL \
    -I${STAGING_DIR_TARGET}/usr/include/interface/vcos/pthreads \
    -I${STAGING_DIR_TARGET}/usr/include/interface/vmcs_host/linux \
"
