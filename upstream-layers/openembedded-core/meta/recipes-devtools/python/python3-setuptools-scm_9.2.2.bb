SUMMARY = "the blessed package to manage your versions by scm tags"
HOMEPAGE = "https://pypi.org/project/setuptools-scm/"
DESCRIPTION = "setuptools_scm handles managing your Python package \
versions in SCM metadata instead of declaring them as the version \
argument or in a SCM managed file."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=838c366f69b72c5df05c96dff79b35f2"

SRC_URI[sha256sum] = "1c674ab4665686a0887d7e24c03ab25f24201c213e82ea689d2f3e169ef7ef57"

PYPI_PACKAGE = "setuptools_scm"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-packaging-native"

RDEPENDS:${PN} = "\
    python3-packaging \
    python3-setuptools \
"

RDEPENDS:${PN}:append:class-target = " \
    python3-debugger \
    python3-json \
"

BBCLASSEXTEND = "native nativesdk"
