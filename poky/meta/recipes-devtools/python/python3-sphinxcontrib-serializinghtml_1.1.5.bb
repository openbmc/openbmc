DESCRIPTION = 'sphinxcontrib-serializinghtml is a sphinx extension which outputs "serialized" HTML files (json and pickle).'
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=32a84ac5cd3bbd10c4d479233ad588b6"

SRC_URI[sha256sum] = "aa5f6de5dfdf809ef505c4895e51ef5c9eac17d0f287933eb49ec495280b6952"

PYPI_PACKAGE = "sphinxcontrib-serializinghtml"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
