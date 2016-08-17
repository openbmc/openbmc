require gdb-common.inc

inherit gettext

SRC_URI += "file://0002-Change-order-of-CFLAGS.patch \
            file://0003-Add-support-for-Renesas-SH-sh4-architecture.patch \
            file://0001-Use-exported-definitions-of-SIGRTMIN.patch \
            file://0001-include-sys-types.h-for-mode_t.patch \
            file://0001-use-asm-sgidefs.h.patch \
            file://force-readline-static.patch \
            file://0001-mips-linux-nat-Define-_ABIO32-if-not-defined.patch \
           "
#LDFLAGS_append = " -s"
#export CFLAGS_append=" -L${STAGING_LIBDIR}"

# cross-canadian must not see this
PACKAGES =+ "gdbserver"
FILES_gdbserver = "${bindir}/gdbserver"
