SUMMARY = "YAML parser/emitter that supports roundtrip preservation of comments, seq/map flow style, and map key order."
HOMEPAGE = "https://pypi.org/project/ruamel.yaml/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5cc5d45e8a30c81dade6ca1928caa515"

PYPI_PACKAGE = "ruamel.yaml"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "20c86ab29ac2153f80a428e1254a8adf686d3383df04490514ca3b79a362db58"

RDEPENDS:${PN} += "\
    python3-shell \
    python3-datetime \
    python3-netclient \
"

BBCLASSEXTEND = "native nativesdk"
