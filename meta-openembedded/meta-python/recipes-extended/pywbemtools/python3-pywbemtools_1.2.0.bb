SUMMARY = "A set of tools using pywbem"
DESCRIPTION = "A set of tools using pywbem to communicate with WBEM servers"
HOMEPAGE = "https://pywbemtools.readthedocs.io/en/stable/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e23fadd6ceef8c618fc1c65191d846fa"

SRC_URI[sha256sum] = "921fb57ea2ae3ae2806e18895bb0457697f27221b345c8b05afa4cb304a7f939"

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
