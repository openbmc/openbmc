HOMEPAGE = "https://github.com/openbmc/s2600wf-misc"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=7becf906c8f8d03c237bad13bc3dac53"
SRC_URI += "git://github.com/openbmc/s2600wf-misc.git;branch=master;protocol=https"
SRCREV = "a057bee1b4b547d4c193bb6c8bb7bec9090f490e"
PV = "0.1+git${SRCPV}"

inherit meson pkgconfig systemd
DEPENDS = "boost sdbusplus"
