SUMMARY = "generate-squashfs script"
DESCRIPTION = "Command line tool to generate a SquashFS image from the PNOR image"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=<checksum>"

RDEPENDS_${PN} += " \
        ${VIRTUAL-RUNTIME_base-utils} \
        pflash \
        mksquashfs \
        "

S = "${WORKDIR}"
SRC_URI += "git://github.com/gtmills/openpower-pnor-code-mgmt/"

SRCREV = "92db9551be41d7beac9e8101bf9effde0e523dd1"

do_install() {
       install -d ${D}${bindir}
       install -m 0755 generate-squashfs \
                       ${D}${bindir}/generate-squashfs
}
