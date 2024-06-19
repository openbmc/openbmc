SUMMARY = "A set of tools using pywbem"
DESCRIPTION = "A set of tools using pywbem to communicate with WBEM servers"
HOMEPAGE = "https://pywbemtools.readthedocs.io/en/stable/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e23fadd6ceef8c618fc1c65191d846fa"

SRC_URI[sha256sum] = "9d0162b74c0b34d4500c099dddfe518cadc295a1a7bfb0abefa740a134d80fea"

inherit pypi setuptools3

RDEPENDS:${PN}:class-target += "\
    python3-ply \
    python3-pyyaml \
    python3-six \
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
    python3-pydicti \
    python3-nocaselist \
    python3-custom-inherit \
"

BBCLASSEXTEND = "native"
