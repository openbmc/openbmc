SUMMARY = "Phosphor OpenBMC OEM Commands for OpenPOWER systems"
DESCRIPTION = "Phosphor OpenBMC IPMI OEM commands for OpenPOWER based systems"
HOMEPAGE = "https://github.com/openbmc/openpower-host-ipmi-oem"
PR = "r1"

RRECOMMENDS_${PN} = "virtual/obmc-phosphor-host-ipmi-hw"

inherit obmc-phosphor-license

DEPENDS += "systemd    \
		 	host-ipmid \
		 	"
TARGET_CFLAGS += "-fpic"


RDEPENDS_${PN} += "libsystemd"



SRC_URI += "git://github.com/openbmc/openpower-host-ipmi-oem"

SRCREV = "ca99efb722ff757707e27f91ba6af293d7e09753"

FILES_${PN} += "${libdir}/host-ipmid/*.so"
FILES_${PN}-dbg += "${libdir}/host-ipmid/.debug"

S = "${WORKDIR}/git"

do_install() {  
        install -m 0755 -d ${D}${libdir}/host-ipmid
        install -m 0755 ${S}/*.so ${D}${libdir}/host-ipmid/
}
