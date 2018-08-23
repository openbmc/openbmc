# Should RDEPENDS on python at the very least.
FILESEXTRAPATHS_append := "${THISDIR}/files:"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://spimaster.py;beginline=1;endline=13;md5=b86c59990b35609be34a0ec322a3cfff"

S = "${WORKDIR}/"

SRC_URI = "file://spimaster.py"

DEPENDS = "python"

FILES_${PN} += "*"

do_install() {
    install -d ${D}/home
    install -d ${D}/home/root
    install -m 0755 ${S}/spimaster.py ${D}/home/root/.
}
