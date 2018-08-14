SUMMARY = "Trace and analyze execution of a program written in C/C++"
HOMEPAGE = "https://github.com/namhyung/uftrace"
BUGTRACKER = "https://github.com/namhyung/uftrace/issues"
SECTION = "devel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "elfutils"
DEPENDS_append_libc-musl = " argp-standalone"

inherit autotools

SRCREV = "5af9ff9fa89c340617e52c8ed05798b352a7145c"
SRC_URI = "git://github.com/namhyung/${BPN}"
S = "${WORKDIR}/git"

LDFLAGS_append_libc-musl = " -largp"
EXTRA_OECONF = "ARCH=${TARGET_ARCH}"

do_configure() {
    ${S}/configure ${EXTRA_OECONF}
}

FILES_SOLIBSDEV = ""
FILES_${PN} += "${libdir}/*.so"

COMPATIBLE_HOST = "(x86_64|aarch64|arm)"

# uftrace supports armv6 and above
COMPATIBLE_HOST_armv4 = 'null'
COMPATIBLE_HOST_armv5 = 'null'
