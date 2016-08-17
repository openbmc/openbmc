SUMMARY = "Python library for displaying tabular data in a ASCII table format"
HOMEPAGE = "http://code.google.com/p/prettytable"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=3e73500ffa52de5071cff65990055282"

SRC_URI[md5sum] = "0c1361104caff8b09f220748f9d69899"
SRC_URI[sha256sum] = "a53da3b43d7a5c229b5e3ca2892ef982c46b7923b51e98f0db49956531211c4f"

SRCNAME = "prettytable"

SRC_URI = "https://pypi.python.org/packages/source/P/PrettyTable/${SRCNAME}-${PV}.zip"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

do_install_append() {
    perm_files=`find "${D}${PYTHON_SITEPACKAGES_DIR}/" -name "top_level.txt"`
    for f in $perm_files; do
        chmod 644 "${f}"
    done
}
