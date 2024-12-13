SUMMARY = "the blessed package to manage your versions by scm tags"
HOMEPAGE = "https://pypi.org/project/setuptools-scm/"
DESCRIPTION = "setuptools_scm handles managing your Python package \
versions in SCM metadata instead of declaring them as the version \
argument or in a SCM managed file."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=838c366f69b72c5df05c96dff79b35f2"

SRC_URI[sha256sum] = "42dea1b65771cba93b7a515d65a65d8246e560768a66b9106a592c8e7f26c8a7"

PYPI_PACKAGE = "setuptools_scm"

inherit pypi python_setuptools_build_meta

UPSTREAM_CHECK_REGEX = "scm-(?P<pver>.*)\.tar"

DEPENDS += "python3-packaging-native python3-typing-extensions-native"

RDEPENDS:${PN} = "\
    python3-packaging \
    python3-pip \
    python3-pyparsing \
    python3-setuptools \
    python3-typing-extensions \
"

RDEPENDS:${PN}:append:class-target = " \
    python3-debugger \
    python3-json \
"

BBCLASSEXTEND = "native nativesdk"
