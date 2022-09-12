HOMEPAGE = "http://github.com/openbmc/sdbusplus"
PR = "r1"
PV ?= "1.0+git${SRCPV}"

SRC_URI += "git://github.com/openbmc/sdbusplus;branch=master;protocol=https"
SRCREV = "293c8a26901f39fa3c9a34be9b4ad530c5592f90"
