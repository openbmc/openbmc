SUMMARY = "Trace and analyze execution of a program written in C/C++"
HOMEPAGE = "https://github.com/namhyung/uftrace"
BUGTRACKER = "https://github.com/namhyung/uftrace/issues"
SECTION = "devel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "elfutils"
DEPENDS:append:libc-musl = " argp-standalone"

inherit autotools

PV .= "+git${SRCPV}"
SRCREV = "a5e5a51d32bdfe59af8b453ca08d78cbdf0b3c61"
SRC_URI = "git://github.com/namhyung/${BPN} \
           "
S = "${WORKDIR}/git"

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

EXTRA_UFTRACE_OECONF = "ARCH=${@set_target_arch(d)} \
                        with_elfutils=/use/libelf/from/sysroot"

do_configure() {
    ${S}/configure ${EXTRA_UFTRACE_OECONF}
}

FILES_SOLIBSDEV = ""
FILES:${PN} += "${libdir}/*.so"

COMPATIBLE_HOST = "(i.86|x86_64|aarch64|arm)"

# uftrace supports armv6 and above
COMPATIBLE_HOST:armv4 = 'null'
COMPATIBLE_HOST:armv5 = 'null'
