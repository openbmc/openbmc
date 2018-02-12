FILESEXTRAPATHS_prepend_centriq2400-rep := "${THISDIR}/${PN}:"
inherit obmc-phosphor-license

SRC_URI += "file://centriq-2400rep-console-client.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/centriq-2400rep-console-client.sh ${D}${bindir}/centriq-2400rep-console-client.sh
}

