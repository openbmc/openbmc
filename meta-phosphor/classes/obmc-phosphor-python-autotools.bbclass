inherit pythonnative

export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

FILES_${PN} += "${PYTHON_SITEPACKAGES_DIR}"
