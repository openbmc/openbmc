SUMMARY = "A C program to run all installed ptests"
DESCRIPTION = "The ptest-runner2 package installs a ptest-runner \
program which loops through all installed ptest test suites and \
runs them in sequence."
HOMEPAGE = "http://git.yoctoproject.org/cgit/cgit.cgi/ptest-runner2/about/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=751419260aa954499f7abaabaa882bbe"

SRCREV = "49956f65bb53ea2a2c1b394e5e59ffdfcdcc490f"
PV = "2.2+git${SRCPV}"

SRC_URI = "git://git.yoctoproject.org/ptest-runner2"
S = "${WORKDIR}/git"

FILES_${PN} = "${bindir}/ptest-runner"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_compile () {
	oe_runmake
}

do_install () {
	install -D -m 0755 ${S}/ptest-runner ${D}${bindir}/ptest-runner
}
