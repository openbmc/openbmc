SUMMARY = "A linter for YAML files."
HOMEPAGE = "https://github.com/adrienverge/yamllint"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "yamllint"

SRC_URI[sha256sum] = "3835a65994858679ea06fd998dd968c3f71935cd93742990405999d888e21130"

RDEPENDS:${PN} += "python3-pathspec python3-pyyaml"

BBCLASSEXTEND = "native nativesdk"
