HOMEPAGE = "http://github.com/openbmc/sdbusplus"
PR = "r1"
PV ?= "1.0+git${SRCPV}"

SRC_URI += "git://github.com/openbmc/sdbusplus;branch=master;protocol=https"
SRCREV = "56dd6dd45837670e8cce56a0602e6dfe5dc75816"
