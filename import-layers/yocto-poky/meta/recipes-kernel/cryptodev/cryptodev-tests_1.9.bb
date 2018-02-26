require cryptodev.inc

SUMMARY = "A test suite for /dev/crypto device driver"

DEPENDS += "openssl10"

SRC_URI += " \
file://0001-Add-the-compile-and-install-rules-for-cryptodev-test.patch \
"

EXTRA_OEMAKE='KERNEL_DIR="${STAGING_EXECPREFIXDIR}" PREFIX="${D}"'

do_compile() {
	oe_runmake testprogs
}

do_install() {
	oe_runmake install_tests
}

FILES_${PN} = "${bindir}/*"
