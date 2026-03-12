SUMMARY = "A drop-in replacement for argparse that allows options to also be set via config files and/or environment variables."
HOMEPAGE = "https://github.com/bw2/ConfigArgParse"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=da746463714cc35999ed9a42339f2943"

SRC_URI[sha256sum] = "79c2ddae836a1e5914b71d58e4b9adbd9f7779d4e6351a637b7d2d9b6c46d3d9"

PYPI_PACKAGE = "configargparse"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi setuptools3

PACKAGECONFIG ?= "yaml"
PACKAGECONFIG[yaml] = ",,,python3-pyyaml"

RDEPENDS:${PN} += "\
    python3-core \
    python3-shell \
    python3-json \
"

BBCLASSEXTEND = "native nativesdk"
