SUMMARY = "A C program to run all installed ptests"
DESCRIPTION = "The ptest-runner2 package installs a ptest-runner \
program which loops through all installed ptest test suites and \
runs them in sequence."
HOMEPAGE = "http://git.yoctoproject.org/cgit/cgit.cgi/ptest-runner2/about/"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=751419260aa954499f7abaabaa882bbe"

SRCREV = "bcb82804daa8f725b6add259dcef2067e61a75aa"
PV .= "+git${SRCPV}"

SRC_URI = "git://git.yoctoproject.org/ptest-runner2;branch=master \
"

S = "${WORKDIR}/git"

FILES:${PN} = "${bindir}/ptest-runner ${bindir}/ptest-runner-collect-system-data"

EXTRA_OEMAKE = "-e MAKEFLAGS= CFLAGS="${CFLAGS} -DDEFAULT_DIRECTORY=\\\"${libdir}\\\"""

do_compile () {
	oe_runmake
}

do_install () {
	install -D -m 0755 ${S}/ptest-runner ${D}${bindir}/ptest-runner
	install -D -m 0755 ${S}/ptest-runner-collect-system-data ${D}${bindir}/ptest-runner-collect-system-data
}

RDEPENDS:${PN}:append:libc-glibc = " libgcc"

# pstree is called by ptest-runner-collect-system-data
RDEPENDS:${PN}:append = " pstree"
