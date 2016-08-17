SUMMARY = "Password strength checker library"
HOMEPAGE = "http://sourceforge.net/projects/cracklib"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=e3eda01d9815f8d24aae2dbd89b68b06"

DEPENDS = "cracklib-native zlib python"
RDEPEND_${PN}-python += "python"

PACKAGES += "${PN}-python"

EXTRA_OECONF = "--with-python --libdir=${base_libdir}"

SRC_URI = "${SOURCEFORGE_MIRROR}/cracklib/cracklib-${PV}.tar.gz \
           file://0001-packlib.c-support-dictionary-byte-order-dependent.patch \
           file://0002-craklib-fix-testnum-and-teststr-failed.patch"

SRC_URI[md5sum] = "376790a95c1fb645e59e6e9803c78582"
SRC_URI[sha256sum] = "59ab0138bc8cf90cccb8509b6969a024d5e58d2d02bcbdccbb9ba9b88be3fa33"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/cracklib/files/cracklib/"
UPSTREAM_CHECK_REGEX = "/cracklib/(?P<pver>(\d+[\.\-_]*)+)/"

inherit autotools gettext pythonnative python-dir

do_install_append_class-target() {
	create-cracklib-dict -o ${D}${datadir}/cracklib/pw_dict ${D}${datadir}/cracklib/cracklib-small
}

do_install_append() {
	src_dir="${D}${base_libdir}/${PYTHON_DIR}/site-packages"
	rm -f $src_dir/test_cracklib.py*

	if [ "${base_libdir}" != "${libdir}" ] ; then
	   # Move python files from ${base_libdir} to ${libdir} since used --libdir=${base_libdir}
	   install -d -m 0755 ${D}${PYTHON_SITEPACKAGES_DIR}/
	   mv $src_dir/* ${D}${PYTHON_SITEPACKAGES_DIR}
	   rm -fr ${D}${base_libdir}/${PYTHON_DIR}
	fi
}

BBCLASSEXTEND = "native nativesdk"

FILES_${PN}-python = "${PYTHON_SITEPACKAGES_DIR}/cracklib.py* \
	${PYTHON_SITEPACKAGES_DIR}/_cracklib.*"

FILES_${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/_cracklib.a"
