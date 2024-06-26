FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SUMMARY = "AMD FPGA Register Dump Utility"
DESCRIPTION = "AMD FPGA Register Dump Utility"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5e24678b8d0883d9dfa9e9473069ddd2"

RDEPENDS:${PN} = "bash"
SRCREV = "${AUTOREV}"
SRC_URI =  "file://fpgardu.sh"
SRC_URI += "file://LICENSE"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install () {
        install -d ${D}${bindir}
        install -m 0755 ${S}/fpgardu.sh ${D}${bindir}/
}
