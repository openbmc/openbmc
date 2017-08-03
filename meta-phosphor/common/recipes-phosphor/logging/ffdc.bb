SUMMARY = "FFDC collector script"
DESCRIPTION = "Command line tool to collect and tar up debug data"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

RDEPENDS_${PN} += " \
        systemd \
        ${VIRTUAL-RUNTIME_base-utils} \
        "

S = "${WORKDIR}/git"
SRC_URI += "git://github.com/openbmc/phosphor-debug-collector"

SRCREV = "ff0699dd834984e806e00dcc586ffb01bcd85a70"

do_install() {
       install -d ${D}${bindir}
       install -m 0755 ffdc \
                       ${D}${bindir}/ffdc
}
