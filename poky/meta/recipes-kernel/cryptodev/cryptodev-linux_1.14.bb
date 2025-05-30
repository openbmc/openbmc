require cryptodev.inc

SUMMARY = "A /dev/crypto device driver header file"

do_compile[noexec] = "1"

do_install() {
	oe_runmake headers_install DESTDIR="${D}"
}

ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
