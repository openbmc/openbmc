inherit python3-dir

PYTHON="${STAGING_BINDIR_NATIVE}/python3-native/python3"
EXTRANATIVEPATH += "python3-native"
DEPENDS_append = " python3-native "

# python-config and other scripts are using distutils modules
# which we patch to access these variables
export STAGING_INCDIR
export STAGING_LIBDIR

# suppress host user's site-packages dirs.
export PYTHONNOUSERSITE = "1"

# autoconf macros will use their internal default preference otherwise
export PYTHON
