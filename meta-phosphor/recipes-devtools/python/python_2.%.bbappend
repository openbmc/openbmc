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
# Remove all python .py files from python recipe. Only the .pyc
# files are required. Only do this if the openbmc-phosphor-tiny
# distro feature is enabled
do_install_append_openbmc-phosphor-tiny() {
    # The _sysconfigdata.py is a system configuration file generated
    # during build time. It's used in the yocto packaging process so
    # it is required to remain in the image.
    find ${D}${libdir}/python${PYTHON_MAJMIN} -name \*.py ! -name _sysconfigdata.py -exec rm {} \;
}
