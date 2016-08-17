#
# Need to make this conditional to gstreamer1
#
SRC_URI_append_rpi = " \
             file://0001-config-files-path.patch \
             file://0001-Don-t-try-to-acquire-buffer-when-src-pad-isn-t-activ.patch \
             file://0002-fix-decoder-flushing.patch \
             file://0003-no-timeout-on-get-state.patch \
             file://0004-Properly-handle-drain-requests-while-flushing.patch \
             file://0005-Don-t-abort-gst_omx_video_dec_set_format-if-there-s-.patch \
             file://0006-omxvideodec-unref-allocator-after-getting-it-from-al.patch \
             file://0007-omxvideodec-Use-gstglmemoryegl-for-the-RPi.patch \
"

FILESEXTRAPATHS_prepend := "${THISDIR}/gstreamer1.0-omx-1.2.0:"

GSTREAMER_1_0_OMX_TARGET_rpi = "rpi"
GSTREAMER_1_0_OMX_CORE_NAME_rpi = "${libdir}/libopenmaxil.so"


# How to make this RPI specific?
EXTRA_OECONF_append_rpi  = " CFLAGS="$CFLAGS -I${STAGING_DIR_TARGET}/usr/include/IL -I${STAGING_DIR_TARGET}/usr/include/interface/vcos/pthreads -I${STAGING_DIR_TARGET}/usr/include/interface/vmcs_host/linux""
#examples only build with GL but not GLES, so disable it for RPI
EXTRA_OECONF_append_rpi = " --disable-examples"
