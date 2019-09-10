
inherit python-dir

PYTHON="${STAGING_BINDIR_NATIVE}/python-native/python"
# PYTHON_EXECUTABLE is used by cmake
PYTHON_EXECUTABLE="${PYTHON}"
EXTRANATIVEPATH += "python-native"
DEPENDS_append = " python-native "

# python-config and other scripts are using distutils modules
# which we patch to access these variables
export STAGING_INCDIR
export STAGING_LIBDIR

# Packages can use
# find_package(PythonInterp REQUIRED)
# find_package(PythonLibs REQUIRED)
# which ends up using libs/includes from build host
# Therefore pre-empt that effort
export PYTHON_LIBRARY="${STAGING_LIBDIR}/lib${PYTHON_DIR}${PYTHON_ABI}.so"
export PYTHON_INCLUDE_DIR="${STAGING_INCDIR}/${PYTHON_DIR}${PYTHON_ABI}"

# suppress host user's site-packages dirs.
export PYTHONNOUSERSITE = "1"

# autoconf macros will use their internal default preference otherwise
export PYTHON
