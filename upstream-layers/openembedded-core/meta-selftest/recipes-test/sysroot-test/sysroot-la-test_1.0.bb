SUMMARY = "Produce a broken la file"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
INHIBIT_DEFAULT_DEPS = "1"

EXCLUDE_FROM_WORLD = "1"

# remove-libtool.bbclass is inherited by default and removes all
# .la files which for this test we specifically do not want.
REMOVE_LIBTOOL_LA = "0"

do_install() {
    install -d ${D}${libdir}/test/
    echo '${WORKDIR}' > ${D}${libdir}/test/la-test.la
}

BBCLASSEXTEND += "native"
