SUMMARY = "setuptools_scm plugin for git archives"
HOMEPAGE = "https://pypi.org/project/setuptools-scm-git-archive/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=838c366f69b72c5df05c96dff79b35f2"

SRC_URI[sha256sum] = "6026f61089b73fa1b5ee737e95314f41cb512609b393530385ed281d0b46c062"

inherit pypi setuptools3

PYPI_PACKAGE = "setuptools_scm_git_archive"
PYPI_SRC_URI = "https://files.pythonhosted.org/packages/7e/2c/0c15b29a1b5940250bfdc4a4f53272e35cd7cf8a34159291b6b4ec9eb291/${PYPI_ARCHIVE_NAME}"

DEPENDS += "python3-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"
