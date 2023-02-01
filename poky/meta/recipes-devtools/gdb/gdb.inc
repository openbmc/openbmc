LICENSE = "GPL-2.0-only & GPL-3.0-only & LGPL-2.0-only & LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
		    file://COPYING3;md5=d32239bcb673463ab874e80d47fae504 \
		    file://COPYING3.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
		    file://COPYING.LIB;md5=9f604d8a4f8e74f4f5140845a21b6674"

SRC_URI = "${GNU_MIRROR}/gdb/gdb-${PV}.tar.xz \
           file://0001-make-man-install-relative-to-DESTDIR.patch \
           file://0002-mips-linux-nat-Define-_ABIO32-if-not-defined.patch \
           file://0003-ppc-ptrace-Define-pt_regs-uapi_pt_regs-on-GLIBC-syst.patch \
           file://0004-Dont-disable-libreadline.a-when-using-disable-static.patch \
           file://0005-use-asm-sgidefs.h.patch \
           file://0006-Change-order-of-CFLAGS.patch \
           file://0007-resolve-restrict-keyword-conflict.patch \
           file://0008-Fix-invalid-sigprocmask-call.patch \
           file://0009-gdbserver-ctrl-c-handling.patch \
           file://readline-8.2.patch \
           file://0008-Define-alignof-using-_Alignof-when-using-C11-or-newe.patch \
           "
SRC_URI[sha256sum] = "0e1793bf8f2b54d53f46dea84ccfd446f48f81b297b28c4f7fc017b818d69fed"
