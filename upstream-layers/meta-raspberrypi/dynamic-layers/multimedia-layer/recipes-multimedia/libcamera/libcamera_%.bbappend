EXTRA_OEMESON:append:rpi = " -Dipas=rpi/vc4,rpi/pisp"
PACKAGECONFIG:append:rpi = " raspberrypi"
CXXFLAGS:append:rpi = " -Wno-unaligned-access "
