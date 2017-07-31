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

SRCREV = "5cca05a8e3ea732e1f57bedf698de3bb7820116c"

do_install() {
       install -d ${D}${bindir}
       install -m 0755 ffdc \
                       ${D}${bindir}/ffdc
}
