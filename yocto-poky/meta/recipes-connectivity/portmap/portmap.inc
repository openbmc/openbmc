SUMMARY = "RPC program number mapper"
HOMEPAGE = "http://neil.brown.name/portmap/"
SECTION = "console/network"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://portmap.c;beginline=2;endline=31;md5=51ff67e66ec84b2009b017b1f94afbf4 \
                    file://from_local.c;beginline=9;endline=35;md5=1bec938a2268b8b423c58801ace3adc1"

INITSCRIPT_NAME = "portmap"
INITSCRIPT_PARAMS = "start 10 2 3 4 5 . stop 32 0 1 6 ."

inherit update-rc.d systemd

SYSTEMD_SERVICE_${PN} = "portmap.service"

PACKAGES =+ "portmap-utils"
FILES_portmap-utils = "${base_sbindir}/pmap_set ${base_sbindir}/pmap_dump"
FILES_${PN}-doc += "${docdir}"
