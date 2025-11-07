HOMEPAGE = "http://github.com/openbmc/sdbusplus"
PR = "r1"
PV ?= "1.0+git${SRCPV}"

SRC_URI += "git://github.com/openbmc/sdbusplus;branch=master;protocol=https"
SRCREV = "5e36f4ab9b19cebc8a1e9be82ae0c3363838b4ce"
