SUMMARY = "Python library for displaying tabular data in a ASCII table format"
HOMEPAGE = "http://code.google.com/p/prettytable"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c9a6829fcd174d9535b46211917c7671"

SRC_URI[sha256sum] = "59f2590776527f3c9e8cf9fe7b66dd215837cca96a9c39567414cbc632e8ddb0"

inherit pypi ptest-python-pytest python_hatchling

do_install:append() {
    perm_files=`find "${D}${PYTHON_SITEPACKAGES_DIR}/" -name "*.txt" -o -name "PKG-INFO"`
    for f in $perm_files; do
        chmod 644 "${f}"
    done
}

DEPENDS += "\
    python3-hatch-vcs-native \
"

RDEPENDS:${PN} += " \
    python3-compression \
    python3-html \
    python3-importlib-metadata \
    python3-json \
    python3-math \
    python3-wcwidth \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest-lazy-fixtures \
    python3-sqlite3 \
"

BBCLASSEXTEND = "native nativesdk"
