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
"

FILESEXTRAPATHS_prepend := "${THISDIR}/gstreamer1.0-omx-1.12:"
