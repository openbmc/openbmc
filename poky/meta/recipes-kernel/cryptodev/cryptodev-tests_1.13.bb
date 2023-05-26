require cryptodev.inc

SUMMARY = "A test suite for /dev/crypto device driver"

DEPENDS += "openssl"

SRC_URI += " \
           file://0001-tests-Makefile-do-not-use-Werror.patch \
           "

EXTRA_OEMAKE='KERNEL_DIR="${STAGING_EXECPREFIXDIR}" PREFIX="${D}"'

do_compile() {
	oe_runmake tests
}

do_install() {
	oe_runmake install_tests
}

FILES:${PN} = "${bindir}/*"
