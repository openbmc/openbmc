SUMMARY = "A set of tools using pywbem"
DESCRIPTION = "A set of tools using pywbem to communicate with WBEM servers"
HOMEPAGE = "https://pywbemtools.readthedocs.io/en/stable/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e23fadd6ceef8c618fc1c65191d846fa"

SRC_URI[sha256sum] = "3fd2fec6a11992afb8a34c0181b76c8d36c6fae1b79d83fb4542ce08e0fb4127"

inherit pypi setuptools3

DEPENDS += " \
    ${PYTHON_PN}-pyyaml-native \
    ${PYTHON_PN}-pywbem-native \
    ${PYTHON_PN}-six-native \
    ${PYTHON_PN}-click-native \
"

RDEPENDS:${PN}:class-target += "\
    ${PYTHON_PN}-ply \
    ${PYTHON_PN}-pyyaml \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-pywbem \
    ${PYTHON_PN}-click \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-prompt-toolkit \
    ${PYTHON_PN}-mock \
    ${PYTHON_PN}-packaging \
    ${PYTHON_PN}-nocasedict \
    ${PYTHON_PN}-yamlloader \
    ${PYTHON_PN}-click-repl \
    ${PYTHON_PN}-click-spinner \
    ${PYTHON_PN}-asciitree \
    ${PYTHON_PN}-tabulate \
    ${PYTHON_PN}-pydicti \
    ${PYTHON_PN}-nocaselist \
    ${PYTHON_PN}-custom-inherit \
"

BBCLASSEXTEND = "native"
