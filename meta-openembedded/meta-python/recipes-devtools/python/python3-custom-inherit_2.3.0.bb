SUMMARY = "A Python package that provides customized docstring inheritance schemes between derived classes and their parents."
HOMEPAGE = "https://github.com/rsokl/custom_inherit"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=adc1f231c76ee2f1f36025d56926ba2c"

PYPI_PACKAGE = "custom_inherit"
PYPI_SRC_URI = "https://files.pythonhosted.org/packages/0e/82/c5bb7ec49a7892a2c583e1017597e3921c59171f10602086ca93c8a83baa/custom_inherit-${PV}.tar.gz"

SRC_URI[md5sum] = "adf2850dc0e488df959821a5d4c16cbd"
SRC_URI[sha256sum] = "1609d283c2cffce3a58baf7c0c22b655c55f85e1ec39c4717a4423d34610723a"

inherit pypi setuptools3
