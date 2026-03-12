SUMMARY = "Trace and analyze execution of a program written in C/C++, Rust and Python"
HOMEPAGE = "https://github.com/namhyung/uftrace"
BUGTRACKER = "https://github.com/namhyung/uftrace/issues"
SECTION = "devel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "elfutils"
DEPENDS:append:libc-musl = " argp-standalone"

SRCREV = "6d006b71ac32e28b7829cc21ccc7bb9c10add964"
SRC_URI = "git://github.com/namhyung/${BPN};branch=master;protocol=https;tag=v${PV} \
           file://0001-include-libgen.h-for-basename.patch \
           "

LDFLAGS:append:libc-musl = " -largp"

def set_target_arch(d):
    import re
    arch = d.getVar('TARGET_ARCH')
    if re.match(r'i.86', arch, re.I):
        return 'i386'
    elif re.match('armeb', arch, re.I):
        return 'arm'
    else:
        return arch


do_configure() {
    ${S}/configure --prefix=${prefix} \
    --objdir=${B} \
    --cflags='${CFLAGS}' \
    --ldflags='${LDFLAGS}' \
    --arch='${@set_target_arch(d)}' \
    --with-elfutils=${STAGING_EXECPREFIXDIR}
}

do_compile() {
    oe_runmake V=1
}

do_install() {
    oe_runmake DESTDIR=${D} install
    rm -f ${D}${libdir}/libelf.so*
}

FILES_SOLIBSDEV = ""
FILES:${PN} += "${libdir}/*.so"

COMPATIBLE_HOST = "(i.86|x86_64|aarch64|arm|riscv64)"

# uftrace supports armv6 and above
COMPATIBLE_HOST:armv4 = 'null'
COMPATIBLE_HOST:armv5 = 'null'
