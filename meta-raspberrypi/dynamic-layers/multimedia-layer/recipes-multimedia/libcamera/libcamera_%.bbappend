PACKAGECONFIG[raspberrypi] = "-Dpipelines=rpi/vc4 -Dipas=rpi/vc4"
PACKAGECONFIG:append:rpi = " raspberrypi"
CXXFLAGS:append:rpi = " -Wno-unaligned-access "
