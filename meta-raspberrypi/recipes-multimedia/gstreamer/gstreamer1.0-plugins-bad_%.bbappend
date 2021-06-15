PACKAGECONFIG_append_rpi = " hls libmms \
                   ${@bb.utils.contains('LICENSE_FLAGS_WHITELIST', 'commercial', 'faad', '', d)}"
