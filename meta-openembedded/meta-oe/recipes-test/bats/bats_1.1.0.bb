SUMMARY = "Bash Automated Testing System"
DESCRIPTION = "Bats is a TAP-compliant testing framework for Bash. It \
provides a simple way to verify that the UNIX programs you write behave as expected."
AUTHOR = "Sam Stephenson & bats-core organization"
HOMEPAGE = "https://github.com/bats-core/bats-core"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=2970203aedf9e829edb96a137a4fe81b"

SRC_URI = "git://github.com/bats-core/bats-core.git;branch=master;protocol=https \
          "
# v1.1.0
SRCREV = "c706d1470dd1376687776bbe985ac22d09780327"

S = "${WORKDIR}/git"

do_install() {
	# Just a bunch of bash scripts to install
	${S}/install.sh ${D}${prefix}
}

RDEPENDS_bats = "bash"
