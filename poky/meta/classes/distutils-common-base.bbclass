export STAGING_INCDIR
export STAGING_LIBDIR

# LDSHARED is the ld *command* used to create shared library
export LDSHARED  = "${CCLD} -shared"
# LDXXSHARED is the ld *command* used to create shared library of C++
# objects
export LDCXXSHARED  = "${CXX} -shared"
# CCSHARED are the C *flags* used to create objects to go into a shared
# library (module)
export CCSHARED  = "-fPIC -DPIC"
# LINKFORSHARED are the flags passed to the $(CC) command that links
# the python executable
export LINKFORSHARED = "{SECURITY_CFLAGS} -Xlinker -export-dynamic"

FILES_${PN} += "${libdir}/* ${libdir}/${PYTHON_DIR}/*"

FILES_${PN}-staticdev += "\
  ${PYTHON_SITEPACKAGES_DIR}/*.a \
"
FILES_${PN}-dev += "\
  ${datadir}/pkgconfig \
  ${libdir}/pkgconfig \
  ${PYTHON_SITEPACKAGES_DIR}/*.la \
"
