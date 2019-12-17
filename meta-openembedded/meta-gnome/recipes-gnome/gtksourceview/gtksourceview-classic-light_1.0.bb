DESCRIPTION = "Gtksourceview Classic-Light theme"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://classic-light.xml;beginline=6;endline=23;md5=2b4f75364fad00a4d752214dcbd7d7c3"

inherit allarch

SRC_URI = "file://classic-light.xml"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${datadir}/gtksourceview-3.0/styles
    install -m 0644 ${WORKDIR}/classic-light.xml ${D}${datadir}/gtksourceview-3.0/styles/
    install -d ${D}${datadir}/gtksourceview-4/styles
    install -m 0644 ${WORKDIR}/classic-light.xml ${D}${datadir}/gtksourceview-4/styles/
}

FILES_${PN} = " \
    ${datadir}/gtksourceview-3.0/styles \
    ${datadir}/gtksourceview-4/styles \
"
