DESCRIPTION = "sphinxcontrib-applehelp is a sphinx extension which outputs Apple help books"
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c7715857042d4c8c0105999ca0c072c5"

SRC_URI[sha256sum] = "828f867945bbe39817c210a1abfd1bc4895c8b73fcaade56d45357a348a07d7e"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
