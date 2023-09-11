# Install EDK2 Base Tools in native sysroot. Currently the BaseTools are not
# built, they are just copied to native sysroot. This is sufficient for
# generating UEFI capsules as it only depends on some python scripts. Other
# tools need to be built first before adding to sysroot.

SUMMARY = "EDK2 Base Tools"
LICENSE = "BSD-2-Clause-Patent"

# EDK2
SRC_URI = "git://github.com/tianocore/edk2.git;branch=master;protocol=https"
LIC_FILES_CHKSUM = "file://License.txt;md5=2b415520383f7964e96700ae12b4570a"

SRCREV = "f80f052277c88a67c55e107b550f504eeea947d3"

S = "${WORKDIR}/git"

inherit native

RDEPENDS:${PN} += "python3-core"

do_install () {
    mkdir -p ${D}${bindir}/edk2-BaseTools
    cp -r ${WORKDIR}/git/BaseTools/* ${D}${bindir}/edk2-BaseTools/
}
