SUMMARY = "the blessed package to manage your versions by scm tags"
HOMEPAGE = "https://pypi.org/project/setuptools-scm/"
DESCRIPTION = "setuptools_scm handles managing your Python package \
versions in SCM metadata instead of declaring them as the version \
argument or in a SCM managed file."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=838c366f69b72c5df05c96dff79b35f2"

SRC_URI += "file://0001-respect-GIT_CEILING_DIRECTORIES.patch"
SRC_URI[sha256sum] = "b5f43ff6800669595193fd09891564ee9d1d7dcb196cab4b2506d53a2e1c95c7"

inherit pypi python_setuptools_build_meta

UPSTREAM_CHECK_REGEX = "scm-(?P<pver>.*)\.tar"

DEPENDS += "python3-tomli-native python3-packaging-native python3-typing-extensions-native"

RDEPENDS:${PN} = "\
    python3-packaging \
    python3-pip \
    python3-pyparsing \
    python3-setuptools \
    python3-tomli \
    python3-typing-extensions \
"

RDEPENDS:${PN}:append:class-target = " \
    python3-debugger \
    python3-json \
"

BBCLASSEXTEND = "native nativesdk"
