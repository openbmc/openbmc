SUMMARY = "A linter for YAML files."
HOMEPAGE = "https://github.com/adrienverge/yamllint"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

inherit pypi setuptools3

PYPI_PACKAGE = "yamllint"

SRC_URI[sha256sum] = "2dceab9ef2d99518a2fcf4ffc964d44250ac4459be1ba3ca315118e4a1a81f7d"

DEPENDS += "python3-setuptools-scm-native"
RDEPENDS:${PN} += "python3-pathspec python3-pyyaml"

BBCLASSEXTEND = "native nativesdk"
