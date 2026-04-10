SUMMARY = "eth-hash: The Ethereum hashing function, keccak256, sometimes (erroneously) called sha3"
HOMEPAGE = "https://github.com/ethereum/eth-hash"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3ef06eb4d4373222e59a338e2eb9a795"

SRC_URI[sha256sum] = "b009752b620da2e9c7668014849d1f5fadbe4f138603f1871cc5d4ca706896b1"

inherit pypi python_setuptools_build_meta

PACKAGECONFIG ?= ""
PACKAGECONFIG[dev] = ",,,python3-build python3-bumpversion python3-ipython python3-pre-commit python3-pytest python3-pytest-xdist python3-sphinx python3-sphinx_rtd_theme python3-towncrier python3-tox python3-twine python3-wheel"
PACKAGECONFIG[docs] = ",,,python3-sphinx python3-sphinx_rtd_theme python3-towncrier"
PACKAGECONFIG[pycryptodome] = ",,,python3-pycryptodome"
PACKAGECONFIG[pysha3python-version-smaller-3-dot-9] = ",,,python3-pysha3"
PACKAGECONFIG[pysha3python-version-bigger--equals-3-dot-9] = ",,,python3-safe-pysha3"
PACKAGECONFIG[test] = ",,,python3-pytest python3-pytest-xdist"

RDEPENDS:${PN} += "python3-core python3-logging python3-pycryptodome"

PYPI_PACKAGE = "eth_hash"
