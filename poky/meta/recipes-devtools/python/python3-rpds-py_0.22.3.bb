SUMMARY = "Python bindings to the Rust rpds crate for persistent data structures."
HOMEPAGE = "https://pypi.org/project/rpds-py/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7767fa537c4596c54141f32882c4a984"

SRC_URI[sha256sum] = "e32fee8ab45d3c2db6da19a5323bc3362237c8b653c70194414b892fd06a080d"

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
