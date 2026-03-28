SUMMARY = "License management tooling for Coherent System and skeleton projects"
DESCRIPTION = "This library was built for coherent.build and skeleton projects \
to inject a license file at build time to reflect the license declared in the \
License Expression."
HOMEPAGE = "https://github.com/coherent-oss/coherent.licensed"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1aeae65f25a15b1e46d4381f2f094e0a"

SRC_URI[sha256sum] = "d8071403ce742d3ac3592ddc4fb7057a46caffb415b928b4d52802e5f208416d"

inherit pypi python_flit_core

PYPI_PACKAGE = "coherent_licensed"

BBCLASSEXTEND = "native nativesdk"
