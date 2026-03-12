SUMMARY = "A linter for YAML files."
HOMEPAGE = "https://github.com/adrienverge/yamllint"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "yamllint"

SRC_URI[sha256sum] = "09e5f29531daab93366bb061e76019d5e91691ef0a40328f04c927387d1d364d"

RDEPENDS:${PN} += "python3-pathspec python3-pyyaml"

BBCLASSEXTEND = "native nativesdk"
