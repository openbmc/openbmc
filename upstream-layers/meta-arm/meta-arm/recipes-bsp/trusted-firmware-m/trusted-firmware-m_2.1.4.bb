require recipes-bsp/trusted-firmware-m/trusted-firmware-m-${PV}-src.inc
require recipes-bsp/trusted-firmware-m/trusted-firmware-m.inc

# FIXME - arm-none-eabi/bin/ld: error: unsupported option: -z relro
# Working around the issue by removing the loader flags, which aren't relevant for us here
# Long term fix, create a baremetal firmware bbclass that doesn't add this stuff
SECURITY_LDFLAGS = ""
