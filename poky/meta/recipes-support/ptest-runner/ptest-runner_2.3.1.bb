SUMMARY = "A C program to run all installed ptests"
DESCRIPTION = "The ptest-runner2 package installs a ptest-runner \
program which loops through all installed ptest test suites and \
runs them in sequence."
HOMEPAGE = "http://git.yoctoproject.org/cgit/cgit.cgi/ptest-runner2/about/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=751419260aa954499f7abaabaa882bbe"

SRCREV = "63d097cc46142157931682fed076b5407757a0bd"
PV = "2.3.1+git${SRCPV}"

SRC_URI = "git://git.yoctoproject.org/ptest-runner2 \
 file://0001-utils-Ensure-stdout-stderr-are-flushed.patch \
 file://0002-use-process-groups-when-spawning.patch \
 file://0003-utils-Ensure-pipes-are-read-after-exit.patch \
 file://0004-utils-ensure-child-can-be-session-leader.patch \
"

S = "${WORKDIR}/git"

FILES_${PN} = "${bindir}/ptest-runner"

EXTRA_OEMAKE = "-e MAKEFLAGS= CFLAGS="${CFLAGS} -DDEFAULT_DIRECTORY=\\\"${libdir}\\\"""

do_compile () {
	oe_runmake
}

do_install () {
	install -D -m 0755 ${S}/ptest-runner ${D}${bindir}/ptest-runner
}
