FILESEXTRAPATHS:prepend:rpi := "${THISDIR}/${PN}:"

SRC_URI:append:rpi = " \
    file://0001-Don-t-try-to-acquire-buffer-when-src-pad-isn-t-activ.patch \
    file://0003-no-timeout-on-get-state.patch \
    file://0004-Properly-handle-drain-requests-while-flushing.patch \
    file://0005-Don-t-abort-gst_omx_video_dec_set_format-if-there-s-.patch \
"

GSTREAMER_1_0_OMX_TARGET:rpi = "rpi"
GSTREAMER_1_0_OMX_CORE_NAME:rpi = "${libdir}/libopenmaxil.so"
EXTRA_OEMESON:append:rpi = " -Dheader_path=${STAGING_DIR_TARGET}/usr/include/IL"
