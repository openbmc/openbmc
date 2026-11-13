SUMMARY = "A set of tools using pywbem"
DESCRIPTION = "A set of tools using pywbem to communicate with WBEM servers"
HOMEPAGE = "https://pywbemtools.readthedocs.io/en/stable/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e23fadd6ceef8c618fc1c65191d846fa"

SRC_URI[sha256sum] = "537f848a90b3f40531984ec296387eaa1892905b5ed8d3fe7d6d4c737f25fce9"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    python3-setuptools-scm-native \
    python3-wheel-native \
"

RDEPENDS:${PN}:append:class-target = " \
    python3-ply \
    python3-pyyaml \
    python3-pywbem \
    python3-click \
    python3-requests \
    python3-prompt-toolkit \
    python3-mock \
    python3-packaging \
    python3-nocasedict \
    python3-yamlloader \
    python3-click-repl \
    python3-click-spinner \
    python3-asciitree \
    python3-tabulate \
    python3-nocaselist \
    python3-toposort \
    python3-psutil \
    python3-pyparsing \
    python3-urllib3 \
"

BBCLASSEXTEND = "native"
