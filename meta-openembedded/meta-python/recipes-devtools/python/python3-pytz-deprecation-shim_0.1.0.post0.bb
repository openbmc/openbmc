SUMMARY = "Shims to help you safely remove pytz"
HOMEPAGE = "https://github.com/pganssle/pytz-deprecation-shim"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fca9fd5c15a28eb874ba38577a585d48"

SRC_URI[sha256sum] = "af097bae1b616dde5c5744441e2ddc69e74dfdcb0c263129610d85b87445a59d"

PYPI_PACKAGE = "pytz_deprecation_shim"
PYPI_SRC_URI = "https://files.pythonhosted.org/packages/94/f0/909f94fea74759654390a3e1a9e4e185b6cd9aa810e533e3586f39da3097/${PYPI_PACKAGE}-${PV}.tar.gz"

inherit pypi python_setuptools_build_meta
