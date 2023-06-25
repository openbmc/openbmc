SUMMARY = "A set of tools using pywbem"
DESCRIPTION = "A set of tools using pywbem to communicate with WBEM servers"
HOMEPAGE = "https://pywbemtools.readthedocs.io/en/stable/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e23fadd6ceef8c618fc1c65191d846fa"

SRC_URI[sha256sum] = "921fb57ea2ae3ae2806e18895bb0457697f27221b345c8b05afa4cb304a7f939"

inherit pypi setuptools3

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
