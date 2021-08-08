SUMMARY = "Bash Automated Testing System"
DESCRIPTION = "Bats is a TAP-compliant testing framework for Bash. It \
provides a simple way to verify that the UNIX programs you write behave as expected."
AUTHOR = "Sam Stephenson & bats-core organization"
HOMEPAGE = "https://github.com/bats-core/bats-core"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=2970203aedf9e829edb96a137a4fe81b"

SRC_URI = "git://github.com/bats-core/bats-core.git \
           file://0001-install.sh-consider-multilib.patch \
          "
# v1.4.1
SRCREV = "54e965fa9d269c2b3ff9036d81f32bac3df0edea"

S = "${WORKDIR}/git"

do_install() {
	# Just a bunch of bash scripts to install
	${S}/install.sh ${D}${prefix} ${baselib}
}

RDEPENDS:${PN} = "bash"
FILES:${PN} += "${libdir}/bats-core/*"

PACKAGECONFIG ??= "pretty"
PACKAGECONFIG[pretty] = ",,,ncurses"
