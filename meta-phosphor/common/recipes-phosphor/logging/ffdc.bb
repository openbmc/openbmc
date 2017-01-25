SUMMARY = "FFDC collector script"
DESCRIPTION = "Command line tool to collect and tar up debug data"
PR = "r1"

inherit obmc-phosphor-license

RDEPENDS_${PN} += "systemd \
                    bash"

S = "${WORKDIR}"
SRC_URI += "git://github.com/openbmc/phosphor-debug-collector/"

do_install() {
       install -d ${D}${bindir}
       install -m 0755 ffdc \
                       ${D}${bindir}/ffdc
}
