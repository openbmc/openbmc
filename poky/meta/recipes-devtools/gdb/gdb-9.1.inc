LICENSE = "GPLv2 & GPLv3 & LGPLv2 & LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
		    file://COPYING3;md5=d32239bcb673463ab874e80d47fae504 \
		    file://COPYING3.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
		    file://COPYING.LIB;md5=9f604d8a4f8e74f4f5140845a21b6674"

SRC_URI = "${GNU_MIRROR}/gdb/gdb-${PV}.tar.xz \
           file://0001-make-man-install-relative-to-DESTDIR.patch \
           file://0002-mips-linux-nat-Define-_ABIO32-if-not-defined.patch \
           file://0003-ppc-ptrace-Define-pt_regs-uapi_pt_regs-on-GLIBC-syst.patch \
           file://0004-Add-support-for-Renesas-SH-sh4-architecture.patch \
           file://0005-Dont-disable-libreadline.a-when-using-disable-static.patch \
           file://0006-use-asm-sgidefs.h.patch \
           file://0007-Use-exorted-definitions-of-SIGRTMIN.patch \
           file://0008-Change-order-of-CFLAGS.patch \
           file://0009-resolve-restrict-keyword-conflict.patch \
           file://0010-Fix-invalid-sigprocmask-call.patch \
           file://0011-gdbserver-ctrl-c-handling.patch \
           "
SRC_URI[md5sum] = "f7e9f6236c425097d9e5f18a6ac40655"
SRC_URI[sha256sum] = "699e0ec832fdd2f21c8266171ea5bf44024bd05164fdf064e4d10cc4cf0d1737"
