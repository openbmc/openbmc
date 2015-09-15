include gstreamer1.0-plugins-base.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=c54ce9345727175ff66d17b67ff51f58 \
                    file://common/coverage/coverage-report.pl;beginline=2;endline=17;md5=a4e1830fce078028c8f0974161272607 \
                    file://COPYING.LIB;md5=6762ed442b3822387a51c92d928ead0d \
                   "

SRC_URI += "file://do-not-change-eos-event-to-gap-event-if.patch \
            file://get-caps-from-src-pad-when-query-caps.patch \
            file://taglist-not-send-to-down-stream-if-all-the-frame-cor.patch \
            file://fix-id3demux-utf16-to-utf8-issue.patch \
            file://handle-audio-video-decoder-error.patch \
            file://videobuffer_updata_alignment_update.patch \
            file://0002-video-frame-Add-GST_VIDEO_FRAME_MAP_FLAG_NO_REF.patch \
            file://0001-video-frame-Don-t-ref-buffers-twice-when-mapping.patch \
            file://0003-videofilter-Use-new-GST_VIDEO_FRAME_MAP_FLAG_NO_REF.patch \
            file://videoencoder-Keep-sticky-events-around-when-doing-a-soft-.patch \
            file://do-not-change-eos-event-to-gap-event2.patch \
            file://do-not-change-eos-event-to-gap-event3.patch \
            file://0001-basetextoverlay-make-memory-copy-when-video-buffer-s.patch \
            file://0002-gstplaysink-don-t-set-async-of-custom-text-sink-to-f.patch \
            file://0003-ssaparse-enhance-SSA-text-lines-parsing.patch \
            file://0004-subparse-set-need_segment-after-sink-pad-received-GS.patch \
            file://encodebin-Need-more-buffers-in-output-queue-for-bett.patch \
"

SRC_URI[md5sum] = "357165af625c0ca353ab47c5d843920e"
SRC_URI[sha256sum] = "77bd8199e7a312d3d71de9b7ddf761a3b78560a2c2a80829d0815ca39cbd551d"

S = "${WORKDIR}/gst-plugins-base-${PV}"
