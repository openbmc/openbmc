SUMMARY = "Python documentation generator"
HOMEPAGE = "http://sphinx-doc.org/"
SECTION = "devel/python"
LICENSE = "BSD-2-Clause AND MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=00d5910f77dc7db0f754b71c3c11317b"

SRC_URI[sha256sum] = "90470459f3cab15d9dfed146c71cf77694b0445df17b2e0e69059cb9f7fc9ee4"

SRC_URI += "file://0001-Revert-singlehtml-deprecate-the-fix_refuris-helper-f.patch"

inherit python_flit_core pypi

do_install:append () {
	# The cache format of "{None, 'en', 'ja'}" doesn't seem to be consistent (dict ordering?)
	rm ${D}${libdir}/${PYTHON_DIR}/site-packages/sphinx/writers/__pycache__/*latex*
}

RDEPENDS:${PN} = "\
    python3-alabaster \
    python3-babel \
    python3-compile \
    python3-docutils \
    python3-imagesize \
    python3-jinja2 \
    python3-packaging \
    python3-pygments \
    python3-requests \
    python3-roman-numerals \
    python3-snowballstemmer \
    python3-sphinxcontrib-applehelp \
    python3-sphinxcontrib-devhelp \
    python3-sphinxcontrib-htmlhelp \
    python3-sphinxcontrib-jsmath \
    python3-sphinxcontrib-qthelp \
    python3-sphinxcontrib-serializinghtml \
    "

BBCLASSEXTEND = "native nativesdk"
