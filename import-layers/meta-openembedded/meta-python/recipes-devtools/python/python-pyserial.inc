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

RSRC_URI[md5sum] = "6afe4c2e47bcec6eb7051b8c1ce19d52"
SRC_URI[sha256sum] = "2949cddffc2b05683065a3cd2345114b1a49b08df8cb843d69ba99dc3e19edc2"

inherit pypi

do_install_append() {
	# We don't support jpython now.
	if [ -e ${D}${PYTHON_SITEPACKAGES_DIR}/serial/serialjava.py ]; then
		rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/serial/serialjava.py
		rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/serial/serialjava.pyc
	fi
}
