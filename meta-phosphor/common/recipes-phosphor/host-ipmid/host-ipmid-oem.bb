SUMMARY = "Phosphor OpenBMC OEM Commands for OpenPOWER systems"
DESCRIPTION = "Phosphor OpenBMC IPMI OEM commands for OpenPOWER based systems"
HOMEPAGE = "https://github.com/openbmc/openpower-host-ipmi-oem"
PR = "r1"

RRECOMMENDS_${PN} = "virtual/obmc-phosphor-host-ipmi-hw"

inherit obmc-phosphor-license

DEPENDS += "systemd    \
		 	host-ipmid \
		 	"


RDEPENDS_${PN} += "libsystemd"



SRC_URI += "git://github.com/openbmc/openpower-host-ipmi-oem"

SRCREV = "29a8e0fd82d70af5626df5346f4c3d2568752af9"

FILES_${PN} += "${libdir}/host-ipmid/*.so"
FILES_${PN}-dbg += "${libdir}/host-ipmid/.debug"

S = "${WORKDIR}/git"

do_install() {  
        install -m 0755 -d ${D}${libdir}/host-ipmid
        install -m 0755 ${S}/*.so ${D}${libdir}/host-ipmid/
}
