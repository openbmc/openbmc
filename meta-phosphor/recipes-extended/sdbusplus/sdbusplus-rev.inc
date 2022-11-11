HOMEPAGE = "http://github.com/openbmc/sdbusplus"
PR = "r1"
PV ?= "1.0+git${SRCPV}"

SRC_URI += "git://github.com/openbmc/sdbusplus;branch=master;protocol=https"
SRCREV = "44c6013c1494be851a2f26f39940e2be428466dc"
