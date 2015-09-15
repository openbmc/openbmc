require zip.inc

PR="r2"

# zip-2.32 still uses directory name of zip-2.30
S = "${WORKDIR}/zip30"

SRC_URI[md5sum] = "7b74551e63f8ee6aab6fbc86676c0d37"
SRC_URI[sha256sum] = "f0e8bb1f9b7eb0b01285495a2699df3a4b766784c1765a8f1aeedf63c0806369"

# zip.inc sets CFLAGS, but what Makefile actually uses is
# CFLAGS_NOOPT.  It will also force -O3 optimization, overriding
# whatever we set.
#
EXTRA_OEMAKE_append = " 'CFLAGS_NOOPT=-I. -DUNIX ${CFLAGS}'"
