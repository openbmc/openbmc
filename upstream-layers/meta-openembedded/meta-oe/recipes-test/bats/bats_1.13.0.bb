SUMMARY = "Bash Automated Testing System"
DESCRIPTION = "Bats is a TAP-compliant testing framework for Bash. It \
provides a simple way to verify that the UNIX programs you write behave as expected."
HOMEPAGE = "https://github.com/bats-core/bats-core"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=2970203aedf9e829edb96a137a4fe81b"

SRC_URI = "\
  git://github.com/bats-core/bats-core.git;branch=master;protocol=https;tag=v${PV} \
  "

# v1.13.0
SRCREV = "3bca150ec86275d6d9d5a4fd7d48ab8b6c6f3d87"


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
