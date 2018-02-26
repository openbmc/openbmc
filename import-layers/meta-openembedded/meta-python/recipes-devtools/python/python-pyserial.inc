SUMMARY = "Serial Port Support for Python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d476d94926db6e0008a5b3860d1f5c0d"

# FIXME might stop packaging serialwin32 and serialjava files

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-fcntl \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-stringold \
    ${PYTHON_PN}-threading \
"

SRC_URI[md5sum] = "ed6183b15519a0ae96675e9c3330c69b"
SRC_URI[sha256sum] = "6e2d401fdee0eab996cf734e67773a0143b932772ca8b42451440cfed942c627"

inherit pypi

do_install_append() {
	# We don't support jpython now.
	if [ -e ${D}${PYTHON_SITEPACKAGES_DIR}/serial/serialjava.py ]; then
		rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/serial/serialjava.py
		rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/serial/serialjava.pyc
	fi
}
