SUMMARY = "A C program to run all installed ptests"
DESCRIPTION = "The ptest-runner2 package installs a ptest-runner \
program which loops through all installed ptest test suites and \
runs them in sequence."
HOMEPAGE = "http://git.yoctoproject.org/cgit/cgit.cgi/ptest-runner2/about/"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=751419260aa954499f7abaabaa882bbe"

SRCREV = "cce0edb4282ee081d043030bfdf29f3e4052f86c"
PV .= "+git${SRCPV}"

SRC_URI = "git://git.yoctoproject.org/ptest-runner2 \
"

S = "${WORKDIR}/git"

FILES:${PN} = "${bindir}/ptest-runner"

EXTRA_OEMAKE = "-e MAKEFLAGS= CFLAGS="${CFLAGS} -DDEFAULT_DIRECTORY=\\\"${libdir}\\\"""

do_compile () {
	oe_runmake
}

do_install () {
	install -D -m 0755 ${S}/ptest-runner ${D}${bindir}/ptest-runner
}

RDEPENDS:${PN}:append:libc-glibc = " libgcc"
