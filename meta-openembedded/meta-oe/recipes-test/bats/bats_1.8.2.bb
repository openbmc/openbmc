SUMMARY = "Bash Automated Testing System"
DESCRIPTION = "Bats is a TAP-compliant testing framework for Bash. It \
provides a simple way to verify that the UNIX programs you write behave as expected."
AUTHOR = "Sam Stephenson & bats-core organization"
HOMEPAGE = "https://github.com/bats-core/bats-core"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=2970203aedf9e829edb96a137a4fe81b"

SRC_URI = "\
  git://github.com/bats-core/bats-core.git;branch=master;protocol=https \
  "

# v1.7.0
SRCREV = "e8c840b58f0833e23461c682655fe540aa923f85"

S = "${WORKDIR}/git"

do_configure:prepend() {
	sed -i 's:\$BATS_ROOT/lib:\$BATS_ROOT/${baselib}:g' ${S}/libexec/bats-core/bats
	sed -i 's:\$BATS_ROOT/lib:\$BATS_ROOT/${baselib}:g' ${S}/libexec/bats-core/bats-exec-file
	sed -i 's:\$BATS_ROOT/lib:\$BATS_ROOT/${baselib}:g' ${S}/libexec/bats-core/bats-exec-test
}

do_install() {
	# Just a bunch of bash scripts to install
	${S}/install.sh ${D}${prefix} ${baselib}
}

RDEPENDS:${PN} = "bash"
FILES:${PN} += "${libdir}/bats-core/*"

PACKAGECONFIG ??= "pretty"
PACKAGECONFIG[pretty] = ",,,ncurses"
