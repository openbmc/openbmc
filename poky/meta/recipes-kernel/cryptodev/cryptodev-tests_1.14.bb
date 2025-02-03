require cryptodev.inc

SUMMARY = "A test suite for /dev/crypto device driver"

DEPENDS += "openssl"

EXTRA_OEMAKE = 'KERNEL_DIR="${STAGING_EXECPREFIXDIR}" PREFIX="${D}"'

do_compile() {
	oe_runmake tests
}

do_install() {
	oe_runmake install_tests
}

FILES:${PN} = "${bindir}/*"
