inherit python3-dir

PYTHON="${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN}"
EXTRANATIVEPATH += "${PYTHON_PN}-native"
DEPENDS_append = " ${PYTHON_PN}-native "

# python-config and other scripts are using distutils modules
# which we patch to access these variables
export STAGING_INCDIR
export STAGING_LIBDIR

# suppress host user's site-packages dirs.
export PYTHONNOUSERSITE = "1"

# autoconf macros will use their internal default preference otherwise
export PYTHON
