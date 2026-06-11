PACKAGECONFIG:append:rpi = " hls v4l2codecs \
                   ${@bb.utils.contains('LICENSE_FLAGS_ACCEPTED', 'commercial', 'faad', '', d)}"
