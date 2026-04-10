SUMMARY = "A drop-in replacement for argparse that allows options to also be set via config files and/or environment variables."
HOMEPAGE = "https://github.com/bw2/ConfigArgParse"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=da746463714cc35999ed9a42339f2943"

SRC_URI[sha256sum] = "e3f9a7bb6be34d66b2e3c4a2f58e3045f8dfae47b0dc039f87bcfaa0f193fb0f"

PYPI_PACKAGE = "configargparse"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"

PACKAGECONFIG ?= "yaml"
PACKAGECONFIG[yaml] = ",,,python3-pyyaml"

RDEPENDS:${PN} += "\
    python3-core \
    python3-shell \
    python3-json \
"

BBCLASSEXTEND = "native nativesdk"
