SUMMARY = "Argcomplete provides easy, extensible command line tab completion of arguments for your Python script."
HOMEPAGE = "https://github.com/kislyuk/argcomplete"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI[sha256sum] = "fe3ce77125f434a0dd1bffe5f4643e64126d5731ce8d173d36f62fa43d6eb6f7"

PYPI_PACKAGE = "argcomplete"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-io \
"

BBCLASSEXTEND = "native nativesdk"

