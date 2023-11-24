SUMMARY = "EDK2 Test Parser"
DESCRIPTION = "EDK2 Test Parser for parsing the results of UEFI SCT tests"
HOMEPAGE = "https://gitlab.arm.com/systemready/edk2-test-parser"

inherit native

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c0550be4b3b9c0223efd0eaa70dc9085"

RDEPENDS:${PN} = "python3-packaging-native python3-pyyaml-native \
                  python3-jsonschema-native"

PV = "v2023.04"
S = "${WORKDIR}/git"
SRC_URI = "git://git.gitlab.arm.com/systemready/edk2-test-parser.git;protocol=https;nobranch=1"

# The SRCREV is at the v2023.04 tag
SRCREV  = "e8cdb692592d2a152cb87cf4d9fbd7ba2ae8b405"

do_install() {
    install -d ${D}/${libdir}/edk2_test_parser
    cp -r ${S}/* ${D}/${libdir}/edk2_test_parser
}

do_configure[noexec] = "1"
do_compile[noexec] = "1"
