PACKAGECONFIG[raspberrypi] = "-Dpipelines=raspberrypi -Dipas=raspberrypi -Dcpp_args=-Wno-unaligned-access"
PACKAGECONFIG:append:rpi = " raspberrypi"
