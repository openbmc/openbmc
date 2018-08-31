FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://0001-json-Use-int-long.__str__-to-convert-subclasses.patch"

require wsgiref-${PYTHON_MAJMIN}-manifest.inc

PROVIDES_prepend = "${PN}-spwd "
PACKAGES_prepend = "${PN}-spwd "

SUMMARY_${PN}-spwd = "Shadow database support"
RDEPENDS_${PN}-spwd = "${PN}-core"
FILES_${PN}-spwd= " \
        ${libdir}/python${PYTHON_MAJMIN}/lib-dynload/spwd.so \
        ${libdir}/python${PYTHON_MAJMIN}/lib-dynload/grp.so \
        "
