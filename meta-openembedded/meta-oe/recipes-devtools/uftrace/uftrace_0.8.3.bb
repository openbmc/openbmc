SUMMARY = "Trace and analyze execution of a program written in C/C++"
HOMEPAGE = "https://github.com/namhyung/uftrace"
BUGTRACKER = "https://github.com/namhyung/uftrace/issues"
SECTION = "devel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "elfutils"
DEPENDS_append_libc-musl = " argp-standalone"

inherit autotools

# v0.8.3
SRCREV = "8b723a6fae2ef30495cd6279774fba9c95cd9c88"
SRC_URI = "git://github.com/namhyung/${BPN} \
           file://0001-include-dlfcn.h-for-RTLD_DEFAULT.patch \
           "
S = "${WORKDIR}/git"

LDFLAGS_append_libc-musl = " -largp"

def set_target_arch(d):
    import re
    arch = d.getVar('TARGET_ARCH', True)
    if re.match(r'i.86', arch, re.I):
        return 'i386'
    else:
        return arch

EXTRA_UFTRACE_OECONF = "ARCH=${@set_target_arch(d)} \
                        with_elfutils=/use/libelf/from/sysroot"

do_configure() {
    ${S}/configure ${EXTRA_UFTRACE_OECONF}
}

FILES_SOLIBSDEV = ""
FILES_${PN} += "${libdir}/*.so"

COMPATIBLE_HOST = "(i.86|x86_64|aarch64|arm)"

# uftrace supports armv6 and above
COMPATIBLE_HOST_armv4 = 'null'
COMPATIBLE_HOST_armv5 = 'null'
