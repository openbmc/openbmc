DESCRIPTION = "Python documentation generator"
HOMEPAGE = "http://sphinx-doc.org/"
SECTION = "devel/python"
LICENSE = "BSD-2-Clause & MIT & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=de4349dda741c550eb8b20f6b92f38d7"

PYPI_PACKAGE = "Sphinx"

SRC_URI[sha256sum] = "6caad9786055cb1fa22b4a365c1775816b876f91966481765d7d50e9f0dd35cc"

inherit setuptools3 pypi

 
do_install:append () {
	# The cache format of "{None, 'en', 'ja'}" doesn't seem to be consistent (dict ordering?)
	rm ${D}${libdir}/${PYTHON_DIR}/site-packages/sphinx/writers/__pycache__/*latex*
}

RDEPENDS:${PN} = "\
    python3-packaging python3-docutils python3-requests \
    python3-imagesize python3-alabaster python3-jinja2 \
    python3-babel python3-pygments python3-snowballstemmer \
    python3-sphinxcontrib-applehelp python3-sphinxcontrib-devhelp \
    python3-sphinxcontrib-jsmath python3-sphinxcontrib-htmlhelp \
    python3-sphinxcontrib-serializinghtml python3-sphinxcontrib-qthelp \
    "

BBCLASSEXTEND = "native nativesdk"
