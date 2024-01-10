HOMEPAGE = "http://github.com/openbmc/sdbusplus"
PR = "r1"
PV ?= "1.0+git${SRCPV}"

SRC_URI += "git://github.com/openbmc/sdbusplus;branch=master;protocol=https"
SRCREV = "ac329c5a94c4847dfd927baa9804e315151e5f5b"
