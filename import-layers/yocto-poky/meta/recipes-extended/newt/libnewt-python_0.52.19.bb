require recipes-extended/newt/libnewt_${PV}.bb

SUMMARY .= " - python"
DEPENDS = "libnewt python3"
RDEPENDS_${PN} += "python3-core"

inherit python3native python3-dir

EXTRA_OECONF += "--with-python"
EXTRA_OEMAKE += "PYTHONVERS=${PYTHON_DIR}"


do_compile () {
	VERSION="$(sed -n 's/^VERSION = //p' Makefile)"
	oe_runmake _snack.so
}

do_install () {
	install -d ${D}${PYTHON_SITEPACKAGES_DIR}
	install -m 0755 ${PYTHON_DIR}/_snack.so ${D}${PYTHON_SITEPACKAGES_DIR}/
	install -m 0644 snack.py ${D}${PYTHON_SITEPACKAGES_DIR}/
}

PACKAGES_remove = "whiptail"

FILES_${PN} = "${PYTHON_SITEPACKAGES_DIR}/*"

BBCLASSEXTEND = "native"
