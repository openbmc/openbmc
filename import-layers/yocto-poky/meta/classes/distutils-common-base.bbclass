inherit python-dir

EXTRA_OEMAKE = ""

export STAGING_INCDIR
export STAGING_LIBDIR

FILES_${PN} += "${libdir}/* ${libdir}/${PYTHON_DIR}/*"

FILES_${PN}-staticdev += "\
  ${PYTHON_SITEPACKAGES_DIR}/*.a \
"
FILES_${PN}-dev += "\
  ${datadir}/pkgconfig \
  ${libdir}/pkgconfig \
  ${PYTHON_SITEPACKAGES_DIR}/*.la \
"
