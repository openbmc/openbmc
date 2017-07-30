SUMMARY = "dreport(dump report) script"
DESCRIPTION = "dreport provides mechanisms to collect various log files \
and system parameters in compressed tar file format. \
This data can be  used to troubleshoot problems in OpenBMC based systems."
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

RDEPENDS_${PN} += " \
        systemd \
        ${VIRTUAL-RUNTIME_base-utils} \
        "

S = "${WORKDIR}/git"
SRC_URI += "git:///esw/san5/othayoth/obmc_1506/phosphor-debug-collector/"

SRCREV = "c767d147cc4c0b7b8a24c53ceed29770bfb0cc9d"

do_install() {
       install -d ${D}${bindir}
       install -m 0755 tools/dreport \
                       ${D}${bindir}/dreport
}
