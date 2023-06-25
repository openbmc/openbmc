SUMMARY = "Argcomplete provides easy, extensible command line tab completion of arguments for your Python script."
HOMEPAGE = "https://github.com/kislyuk/argcomplete"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI[sha256sum] = "3a81445fa51c9875c36789ae5386d5e95c5c43d503963a0029a747aea74de1e3"

PYPI_PACKAGE = "argcomplete"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-io \
"

BBCLASSEXTEND = "native nativesdk"

