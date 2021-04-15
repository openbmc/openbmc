SUMMARY = "Bash Automated Testing System"
DESCRIPTION = "Bats is a TAP-compliant testing framework for Bash. It \
provides a simple way to verify that the UNIX programs you write behave as expected."
AUTHOR = "Sam Stephenson & bats-core organization"
HOMEPAGE = "https://github.com/bats-core/bats-core"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=2970203aedf9e829edb96a137a4fe81b"

SRC_URI = "git://github.com/bats-core/bats-core.git \
          "
# v1.3.0
SRCREV = "9086c47854652f2731861b40385689c85f12103f"

S = "${WORKDIR}/git"

do_install() {
	# Just a bunch of bash scripts to install
	${S}/install.sh ${D}${prefix}
}

RDEPENDS_${PN} = "bash"
FILES_${PN} += "${libdir}/bats-core/*"

PACKAGECONFIG ??= "pretty"
PACKAGECONFIG[pretty] = ",,,ncurses"
