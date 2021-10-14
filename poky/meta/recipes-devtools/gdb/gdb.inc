require gdb-common.inc

inherit gettext pkgconfig

#LDFLAGS:append = " -s"
#export CFLAGS:append=" -L${STAGING_LIBDIR}"

# cross-canadian must not see this
PACKAGES =+ "gdbserver"
FILES:gdbserver = "${bindir}/gdbserver"

