SUMMARY = "A read/write cpoy test program"
DESCRIPTION = "The copy test program."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
SRC_URI = "file://wr_perf_test file://rd_perf_test"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"

do_install () {
	install -d ${D}${bindir}/
	install ${WORKDIR}/rd_perf_test ${WORKDIR}/wr_perf_test ${D}${bindir}
}
