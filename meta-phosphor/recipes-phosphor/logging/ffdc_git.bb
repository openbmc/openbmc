SUMMARY = "FFDC collector script"
DESCRIPTION = "Command line tool to collect and tar up debug data"
PR = "r1"
PV = "1.0+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

DEPENDS += "systemd"

RDEPENDS_${PN} += " \
        ${VIRTUAL-RUNTIME_base-utils} \
        "

S = "${WORKDIR}/git"
SRC_URI += "git://github.com/openbmc/phosphor-debug-collector"

SRCREV = "984a98f79022c60c51a465d4bbde1247d0f69b3e"

do_install() {
       install -d ${D}${bindir}
       install -m 0755 ffdc \
                       ${D}${bindir}/ffdc
}
