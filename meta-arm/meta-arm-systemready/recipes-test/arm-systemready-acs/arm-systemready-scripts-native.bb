SUMMARY = "System Ready Scripts"
DESCRIPTION = "A collection of scripts to help with SystemReady compliance."
HOMEPAGE = "https://gitlab.arm.com/systemready/systemready-scripts"

inherit native

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=85b7d439a311c22626c2e3f05daf628e"

RDEPENDS:${PN} = "python3-packaging-native python3-pyyaml-native \
python3-chardet-native python3-requests-native python3-construct-native \
dtc-native python3-dtschema-wrapper-native"

PV = "v2023.04"
S = "${WORKDIR}/git"
SRC_URI = "\
        git://git.gitlab.arm.com/systemready/systemready-scripts.git;protocol=https;nobranch=1 \
        file://0001-check-sr-results-Return-non-zero-exit-code-on-failur.patch \
        file://0002-check-sr-results-Device-tree-improvements.patch \
"

# The SRCREV is at the v2023.04 tag
SRCREV  = "f8244ab8da09f9e6005ceff81ebb234f35a2a698"

do_install() {
    install -d ${D}/${libdir}/systemready_scripts
    cp -r ${S}/* ${D}/${libdir}/systemready_scripts
}

do_configure[noexec] = "1"
do_compile[noexec] = "1"
