# Should RDEPENDS on python at the very least.
FILESEXTRAPATHS_append := "${THISDIR}/files:"

inherit obmc-phosphor-license

S = "${WORKDIR}/"

SRC_URI = "file://spimaster.py"

DEPENDS = "python"

FILES_${PN} += "*"

do_install() {
    install -d ${D}/home
    install -d ${D}/home/root
    install -m 0755 ${S}/spimaster.py ${D}/home/root/.
}
