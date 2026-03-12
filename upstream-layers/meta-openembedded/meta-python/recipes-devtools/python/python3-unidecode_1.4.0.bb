SUMMARY = "ASCII transliterations of Unicode text"
HOMEPAGE = "https://pypi.org/project/Unidecode/"
DESCRIPTION = "This is a Python port of Text::Unidecode Perl module by Sean M. Burke"
SECTION = "devel/python"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit pypi setuptools3

PYPI_PACKAGE = "Unidecode"

SRC_URI[sha256sum] = "ce35985008338b676573023acc382d62c264f307c8f7963733405add37ea2b23"
