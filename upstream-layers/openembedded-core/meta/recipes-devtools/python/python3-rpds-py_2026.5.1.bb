SUMMARY = "Python bindings to the Rust rpds crate for persistent data structures."
HOMEPAGE = "https://pypi.org/project/rpds-py/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7767fa537c4596c54141f32882c4a984"

SRC_URI[sha256sum] = "07b24fea40541e28570e5b795a4a38fbdcd12550c06bd0748005ecc8116ca256"

require ${BPN}-crates.inc

inherit pypi cargo-update-recipe-crates python_maturin ptest-python-pytest

PYPI_PACKAGE = "rpds_py"

RDEPENDS:${PN}-ptest += " \
    python3-iniconfig \
    python3-packaging \
    python3-pluggy \
    "

BBCLASSEXTEND = "native nativesdk"
