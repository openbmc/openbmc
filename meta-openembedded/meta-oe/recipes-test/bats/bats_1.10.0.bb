SUMMARY = "Bash Automated Testing System"
DESCRIPTION = "Bats is a TAP-compliant testing framework for Bash. It \
provides a simple way to verify that the UNIX programs you write behave as expected."
HOMEPAGE = "https://github.com/bats-core/bats-core"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=2970203aedf9e829edb96a137a4fe81b"

SRC_URI = "\
  git://github.com/bats-core/bats-core.git;branch=master;protocol=https \
  "

# v1.10.0
SRCREV = "f7defb94362f2053a3e73d13086a167448ea9133"

S = "${WORKDIR}/git"

# Numerous scripts assume ${baselib} == lib, which is not true.
#
do_configure:prepend() {
	for f in ${S}/libexec/bats-core/* ${S}/lib/bats-core/* ; do
		sed -i 's:\$BATS_ROOT/lib/:\$BATS_ROOT/${baselib}/:g' $f
	done
}

do_install() {
	# Just a bunch of bash scripts to install
	${S}/install.sh ${D}${prefix} ${baselib}
}

RDEPENDS:${PN} = "bash"
FILES:${PN} += "${libdir}/bats-core/*"

PACKAGECONFIG ??= "pretty"
PACKAGECONFIG[pretty] = ",,,ncurses"
