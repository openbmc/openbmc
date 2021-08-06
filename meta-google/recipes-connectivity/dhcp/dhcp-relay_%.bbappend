# LTO and static enabled to reduce image size, saves ~50%
CFLAGS:append:gbmc = " -flto"
EXTRA_OECONF:remove:gbmc = "--disable-static"
EXTRA_OECONF:append:gbmc = " --enable-relay-port --disable-shared"
