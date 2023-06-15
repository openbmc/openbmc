inherit update-rc.d

SUMMARY = "Enables reading any script at simulation launch time"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

SRC_URI = "file://m5-readfile.sh"

INITSCRIPT_NAME = "m5-readfile.sh"
INITSCRIPT_PARAMS = "defaults 99"

do_install() {
    install -d ${D}/${INIT_D_DIR}
    install -m 755 ${WORKDIR}/m5-readfile.sh ${D}/${INIT_D_DIR}
}

RDEPENDS:${PN} = "gem5-m5ops"
