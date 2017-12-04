LICENSE = "GPLv2 & GPLv3 & LGPLv2 & LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
		    file://COPYING3;md5=d32239bcb673463ab874e80d47fae504 \
		    file://COPYING3.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
		    file://COPYING.LIB;md5=9f604d8a4f8e74f4f5140845a21b6674"

SRC_URI = "http://ftp.gnu.org/gnu/gdb/gdb-${PV}.tar.xz \
           file://0001-include-sys-types.h-for-mode_t.patch \
           file://0002-make-man-install-relative-to-DESTDIR.patch \
           file://0003-mips-linux-nat-Define-_ABIO32-if-not-defined.patch \
           file://0004-ppc-ptrace-Define-pt_regs-uapi_pt_regs-on-GLIBC-syst.patch \
           file://0005-Add-support-for-Renesas-SH-sh4-architecture.patch \
           file://0006-Dont-disable-libreadline.a-when-using-disable-static.patch \
           file://0007-use-asm-sgidefs.h.patch \
           file://0008-Use-exorted-definitions-of-SIGRTMIN.patch \
           file://0009-Change-order-of-CFLAGS.patch \
           file://0010-resolve-restrict-keyword-conflict.patch \
           file://0011-avx_mpx.patch \
"

SRC_URI[md5sum] = "5aa71522e488e358243917967db87476"
SRC_URI[sha256sum] = "e9216da4e3755e9f414c1aa0026b626251dfc57ffe572a266e98da4f6988fc70"
