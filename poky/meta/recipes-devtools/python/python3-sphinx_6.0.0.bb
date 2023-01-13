DESCRIPTION = "Python documentation generator"
HOMEPAGE = "http://sphinx-doc.org/"
SECTION = "devel/python"
LICENSE = "BSD-2-Clause & MIT & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2214a89317448c7f792f5b75dd6726e4"

PYPI_PACKAGE = "Sphinx"

SRC_URI[sha256sum] = "58c140ecd9aa0abbc8ff6da48a266648eac9e5bfc8e49576efd2979bf46f5961"

inherit python_flit_core pypi

 
do_install:append () {
	# The cache format of "{None, 'en', 'ja'}" doesn't seem to be consistent (dict ordering?)
	rm ${D}${libdir}/${PYTHON_DIR}/site-packages/sphinx/writers/__pycache__/*latex*
}

RDEPENDS:${PN} = "\
    python3-alabaster \
    python3-babel \
    python3-docutils \
    python3-imagesize \
    python3-jinja2 \
    python3-packaging \
    python3-pygments \
    python3-requests \
    python3-snowballstemmer \
    python3-sphinxcontrib-applehelp \
    python3-sphinxcontrib-devhelp \
    python3-sphinxcontrib-htmlhelp \
    python3-sphinxcontrib-jsmath \
    python3-sphinxcontrib-qthelp \
    python3-sphinxcontrib-serializinghtml \
    "

BBCLASSEXTEND = "native nativesdk"
