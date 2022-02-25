PACKAGECONFIG:append:rpi = " hls \
                   ${@bb.utils.contains('LICENSE_FLAGS_ACCEPTED', 'commercial', 'gpl faad', '', d)}"
