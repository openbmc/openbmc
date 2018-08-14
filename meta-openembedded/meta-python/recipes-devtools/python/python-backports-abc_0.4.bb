SUMMARY = "collections.abc from Python 3.4"
DESCRIPTION = "A backport of recent additions to the 'collections.abc' module"

LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=dd98d01d471fac8d8dbdd975229dba03"

# pypi package doesn't have valid entry for license
SRC_URI += "https://raw.githubusercontent.com/cython/backports_abc/master/LICENSE;name=license"

SRC_URI[md5sum] = "0b65a216ce9dc9c1a7e20a729dd7c05b"
SRC_URI[sha256sum] = "8b3e4092ba3d541c7a2f9b7d0d9c0275b21c6a01c53a61c731eba6686939d0a5"
SRC_URI[license.md5sum] = "dd98d01d471fac8d8dbdd975229dba03"
SRC_URI[license.sha256sum] = "0a4f3b38055f50f047a42521568fa6ddb9a5976c2884f6ae138796d0f71150ca"



PYPI_PACKAGE = "backports_abc"
inherit pypi setuptools
