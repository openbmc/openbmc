SUMMARY = "A Python module for decorators, wrappers and monkey patching."
HOMEPAGE = "http://wrapt.readthedocs.org/"
LICENSE = "BSD-2-Clause"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fdfc019b57affbe1d7a32e3d34e83db4"

SRC_URI[md5sum] = "6d56ed0de4336462a73350341462f45e"
SRC_URI[sha256sum] = "b62ffa81fb85f4332a4f609cab4ac40709470da05643a082ec1eb88e6d9b97d7"

inherit pypi setuptools3 

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-stringold \
    ${PYTHON_PN}-threading \
"
