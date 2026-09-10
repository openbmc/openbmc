SUMMARY = "Produce a broken pc file"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
INHIBIT_DEFAULT_DEPS = "1"

EXCLUDE_FROM_WORLD = "1"

do_install() {
    install -d ${D}${libdir}/test/
    echo '${WORKDIR}' > ${D}${libdir}/test/test.pc
}

BBCLASSEXTEND += "native"
