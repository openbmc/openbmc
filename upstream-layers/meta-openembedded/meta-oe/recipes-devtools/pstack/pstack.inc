SUMMARY = "A utility to print stack traces of running processes"
DESCRIPTION = "A from-scratch implementation of pstack using DWARF debugging and unwind information. \
Works for C/C++, Go, Rust, and Python. A traditional pstack command can generally print a backtrace \
of each thread in a running program, and sometimes from a core file. This version of pstack uses its \
own self contained ELF and DWARF parsing library, libdwelf to parse the DWARF debug and unwind \
information, to get a stack trace."
HOMEPAGE = "https://github.com/peadar/pstack"
SECTION = "devel"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=671019a96ba80415b696240ed2ca5e80"

inherit cmake

DEPENDS += "zlib xz libunwind elfutils"

RDEPENDS:${PN} += "elfutils"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/pstack ${D}${bindir}/pstack

    install -d ${D}${libdir}
    install -m 0755 ${B}/libprocman.so.${PV} ${D}${libdir}/libprocman.so.${PV}
    ln -sf libprocman.so.${PV} ${D}${libdir}/libprocman.so
    install -m 0755 ${B}/libdwelf.so.${PV}  ${D}${libdir}/libdwelf.so.${PV}
    ln -sf libdwelf.so.${PV}  ${D}${libdir}/libdwelf.so
}

PACKAGES = "${PN}"
FILES:${PN} = "${bindir}/pstack"
FILES:${PN} += "${libdir}/libprocman.so.${PV}"
FILES:${PN} += "${libdir}/libdwelf.so.${PV}"

PACKAGES += "${PN}-dbg"
FILES:${PN}-dbg += "${base_bindir}/.debug ${base_sbindir}/.debug ${bindir}/.debug ${sbindir}/.debug"

PACKAGES += "${PN}-dev"
FILES:${PN}-dev += "${base_libdir}/*.so"

TOOLCHAIN = "gcc"

COMPATIBLE_HOST:riscv64 = "null"
COMPATIBLE_HOST:riscv32 = "null"
COMPATIBLE_HOST:arm = "null"
COMPATIBLE_HOST:libc-musl = "null"
