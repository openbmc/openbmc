
inherit python-dir

PYTHON="${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN}"
# PYTHON_EXECUTABLE is used by cmake
PYTHON_EXECUTABLE="${PYTHON}"
EXTRANATIVEPATH += "${PYTHON_PN}-native"
DEPENDS_append = " ${PYTHON_PN}-native "

# python-config and other scripts are using distutils modules
# which we patch to access these variables
export STAGING_INCDIR
export STAGING_LIBDIR

# autoconf macros will use their internal default preference otherwise
export PYTHON
