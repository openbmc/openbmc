DESCRIPTION = "Concurrency Kit provides a plethora of concurrency primitives, \
safe memory reclamation mechanisms and non-blocking data structures \
designed to aid in the design and implementation of high performance \
concurrent systems."

LICENSE = "BSD-2-Clause & Apache-2.0"
HOMEPAGE = "http://concurrencykit.org"
SECTION = "base"

PV = "0.7.0+git"
SRCREV = "6e8e5bec2e2f8cef2072a68579cbb07ababf3331"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a0b24c1a8f9ad516a297d055b0294231"
SRC_URI = "git://github.com/concurrencykit/ck.git;branch=master;protocol=https \
           file://0001-configure-Fix-compoiler-detection-logic-for-cross-co.patch \
           file://0001-build-Use-ilp32d-abi-on-riscv32-and-lp64d-on-rv64.patch"

S = "${WORKDIR}/git"

COMPATIBLE_HOST = "(arm|aarch64|i.86|x86_64|powerpc|powerpc64|riscv32|riscv64).*-linux*"

inherit autotools-brokensep

PLAT:powerpc64 = "ppc64"
PLAT:powerpc64le = "ppc64"
PLAT:riscv32 = "riscv"
PLAT ?= "${HOST_ARCH}"

do_configure () {
    export PLATFORM=${PLAT}
    ${S}/configure \
    --prefix=${prefix} \
    --includedir=${includedir} \
    --libdir=${libdir}
}

do_compile () {
    oe_runmake
}

do_install () {
    oe_runmake 'DESTDIR=${D}' install
}
