SUMMARY = "Error Test case that fails on do_compile"
DESCRIPTION = "This generates a compile time error to be used to for testing."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

INHIBIT_DEFAULT_DEPS = "1"                                                                                                                                                                  
EXCLUDE_FROM_WORLD = "1"

do_compile() {
        bbfatal "Failing as expected.";
}
