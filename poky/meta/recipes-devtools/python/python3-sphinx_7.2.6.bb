SUMMARY = "Python documentation generator"
HOMEPAGE = "http://sphinx-doc.org/"
SECTION = "devel/python"
LICENSE = "BSD-2-Clause & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5eb6ac1b115a1ed24a12d9f15b633993"

SRC_URI[sha256sum] = "9a5160e1ea90688d5963ba09a2dcd8bdd526620edbb65c328728f1b2228d5ab5"

inherit python_flit_core pypi
UPSTREAM_CHECK_REGEX = "/Sphinx/(?P<pver>(\d+[\.\-_]*)+)/"

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
