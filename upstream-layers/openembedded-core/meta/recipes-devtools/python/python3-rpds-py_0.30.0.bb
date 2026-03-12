SUMMARY = "Python bindings to the Rust rpds crate for persistent data structures."
HOMEPAGE = "https://pypi.org/project/rpds-py/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7767fa537c4596c54141f32882c4a984"

SRC_URI[sha256sum] = "dd8ff7cf90014af0c0f787eea34794ebf6415242ee1d6fa91eaba725cc441e84"

require ${BPN}-crates.inc

inherit pypi cargo-update-recipe-crates python_maturin ptest-python-pytest

PYPI_PACKAGE = "rpds_py"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN}-ptest += " \
    python3-iniconfig \
    python3-packaging \
    python3-pluggy \
    "

BBCLASSEXTEND = "native nativesdk"
