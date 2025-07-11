HOMEPAGE = "https://github.com/openbmc/s2600wf-misc"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=7becf906c8f8d03c237bad13bc3dac53"
SRC_URI += "git://github.com/openbmc/s2600wf-misc.git;branch=master;protocol=https"
SRCREV = "2ac1af7085679fbb90c0ba26dab0df8286165c16"
PV = "0.1+git${SRCPV}"

inherit meson pkgconfig systemd
DEPENDS = "boost sdbusplus"
