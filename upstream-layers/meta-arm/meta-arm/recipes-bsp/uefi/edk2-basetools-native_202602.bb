# Install EDK2 Base Tools in native sysroot. The Python BaseTools are copied
# wholesale, and the C host tools (GenFfs, GenFv) are built so that downstream
# recipes can generate UEFI FV images and capsules.
#
# SRCREV_brotli must be kept in sync with the brotli submodule commit that
# the pinned edk2 SRCREV declares in its .gitmodules. Update it whenever
# the edk2 SRCREV is bumped.
#   edk2-stable202602 (b7a715f7): brotli e230f474

SUMMARY = "EDK2 Base Tools"
LICENSE = "BSD-2-Clause-Patent"

# EDK2
SRC_URI = " \
    git://github.com/tianocore/edk2.git;branch=master;protocol=https \
    git://github.com/google/brotli.git;protocol=https;nobranch=1;name=brotli;destsuffix=${BPN}-${PV}/BaseTools/Source/C/BrotliCompress/brotli \
"
LIC_FILES_CHKSUM = "file://License.txt;md5=2b415520383f7964e96700ae12b4570a"

SRCREV = "b7a715f7c03c45c6b4575bf88596bfd79658b8ce"
SRCREV_brotli = "e230f474b87134e8c6c85b630084c612057f253e"
SRCREV_FORMAT = "default_brotli"

UPSTREAM_CHECK_GITTAGREGEX = "^edk2-stable(?P<pver>\d+)$"

inherit native

RDEPENDS:${PN} += "python3-core"

do_compile() {
    BASE_C="${S}/BaseTools/Source/C"

    oe_runmake -C "${BASE_C}/Common"
    oe_runmake -C "${BASE_C}/BrotliCompress"
    oe_runmake -C "${BASE_C}/GenFfs"
    oe_runmake -C "${BASE_C}/GenFv"
}

do_install () {
    mkdir -p ${D}${bindir}/edk2-BaseTools
    cp -r ${S}/BaseTools/* ${D}${bindir}/edk2-BaseTools/

    # Install compiled binaries to bindir so downstream recipes can reference
    # them via ${STAGING_BINDIR_NATIVE}/GenFfs and GenFv.
    install -d "${D}${bindir}"
    install -m 0755 "${S}/BaseTools/Source/C/bin/GenFfs" "${D}${bindir}/"
    install -m 0755 "${S}/BaseTools/Source/C/bin/GenFv"  "${D}${bindir}/"

    # Install GenerateCapsule.py and Common/ Python library under a
    # well-known datadir path.
    install -d "${D}${datadir}/edk2-basetools"
    install -m 0644 \
        "${S}/BaseTools/Source/Python/Capsule/GenerateCapsule.py" \
        "${D}${datadir}/edk2-basetools/"
    cp -r "${S}/BaseTools/Source/Python/Common" \
        "${D}${datadir}/edk2-basetools/"
}
