require gdb-common.inc

inherit gettext

#LDFLAGS_append = " -s"
#export CFLAGS_append=" -L${STAGING_LIBDIR}"

# cross-canadian must not see this
PACKAGES =+ "gdbserver"
FILES_gdbserver = "${bindir}/gdbserver"

ALLOW_EMPTY_gdbserver_riscv64 = "1"
ALLOW_EMPTY_gdbserver_riscv32 = "1"

