SUMMARY = "Test Multiconfig Parsing"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

INHIBIT_DEFAULT_DEPS = "1"

do_showvar() {
    bbplain "MCTESTVAR=${MCTESTVAR}"
}
addtask do_showvar

