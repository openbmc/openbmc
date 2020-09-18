# Copyright (C) 2019 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "ucontext implementation featuring glibc-compatible ABI"
HOMEPAGE = "https://github.com/kaniini/libucontext"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6eed01fa0e673c76f5a5715438f65b1d"
SECTION = "libs"
DEPENDS = ""

PV = "0.10+${SRCPV}"
SRCREV = "19fa1bbfc26efb92147b5e85cc0ca02a0e837561"
SRC_URI = "git://github.com/kaniini/libucontext \
"

S = "${WORKDIR}/git"

COMPATIBLE_HOST = ".*-musl.*"

valid_archs = " \
    x86 x86_64  \
    ppc ppc64   \
    mips mips64 \
    arm aarch64 \
    s390x       \
"

def map_kernel_arch(a, d):
    import re

    valid_archs = d.getVar('valid_archs').split()

    if a in valid_archs:                            return a
    elif re.match('(i.86|athlon)$', a):             return 'x86'
    elif re.match('x86.64$', a):                    return 'x86_64'
    elif re.match('armeb$', a):                     return 'arm'
    elif re.match('aarch64$', a):                   return 'aarch64'
    elif re.match('aarch64_be$', a):                return 'aarch64'
    elif re.match('aarch64_ilp32$', a):             return 'aarch64'
    elif re.match('aarch64_be_ilp32$', a):          return 'aarch64'
    elif re.match('mips(isa|)(32|)(r6|)(el|)$', a): return 'mips'
    elif re.match('mips(isa|)64(r6|)(el|)$', a):    return 'mips64'
    elif re.match('p(pc|owerpc)', a):               return 'ppc'
    elif re.match('p(pc64|owerpc64)', a):           return 'ppc64'
    elif re.match('riscv64$', a):                   return 'riscv64'
    elif re.match('riscv32$', a):                   return 'riscv32'
    else:
        if not d.getVar("TARGET_OS").startswith("linux"):
            return a
        bb.error("cannot map '%s' to a linux kernel architecture" % a)

export ARCH = "${@map_kernel_arch(d.getVar('TARGET_ARCH'), d)}"

CFLAGS += "-Iarch/${ARCH} -Iarch/common"

EXTRA_OEMAKE = "CFLAGS='${CFLAGS}' LDFLAGS='${LDFLAGS}' LIBDIR='${base_libdir}'"

do_compile() {
    oe_runmake ARCH=${ARCH}
}

do_install() {
    oe_runmake ARCH="${ARCH}" DESTDIR="${D}" install
}
