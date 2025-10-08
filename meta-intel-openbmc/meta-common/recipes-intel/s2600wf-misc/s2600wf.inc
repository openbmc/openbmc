HOMEPAGE = "https://github.com/openbmc/s2600wf-misc"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=7becf906c8f8d03c237bad13bc3dac53"
SRC_URI += "git://github.com/openbmc/s2600wf-misc.git;branch=master;protocol=https"
SRCREV = "ed4c97df285cf38def98919ff9ede05586468685"
PV = "0.1+git${SRCPV}"

inherit meson pkgconfig systemd
DEPENDS = "boost sdbusplus"
