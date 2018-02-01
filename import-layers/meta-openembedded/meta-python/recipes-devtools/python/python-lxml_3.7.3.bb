inherit setuptools
require python-lxml.inc

SRC_URI = "${PYPI_SRC_URI} \
           file://0001-lxml.etree-Add-empty-variadic-argument.patch \
           "
