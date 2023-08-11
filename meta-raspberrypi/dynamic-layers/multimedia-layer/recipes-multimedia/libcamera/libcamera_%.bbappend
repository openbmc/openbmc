PACKAGECONFIG[raspberrypi] = "-Dpipelines=rpi/vc4 -Dipas=rpi/vc4 -Dcpp_args=-Wno-unaligned-access"
PACKAGECONFIG:append:rpi = " raspberrypi"
