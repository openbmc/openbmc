DESCRIPTION = "This is Mellanox BMC tools"
SECTION = "console/network"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "git://github.com/mellanoxbmc/mellanox-bmc-tools.git;protocol=git"
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"

inherit module

do_compile() {
    cd ${B}/mlnx_cpldprog
    oe_runmake CC="${CC}"
}

do_install() {
    install -d ${D}${sbindir}
    install -d ${D}/usr/share
    install -m 0755 ${B}/mlnx_cpldprog/mlnx_cpldprog ${D}${sbindir}
    install -m 0755 ${B}/mlnx_cpldprog/obmc-update_cpld.sh ${D}${sbindir}/update_cpld
    cp ${B}/mlnx_cpldprog/CPLD_firmware/*.svf ${D}/usr/share 2>/dev/null || :
}

FILES_${PN} += "${sbindir} ./usr/share"

INSANE_SKIP_${PN} = "ldflags"