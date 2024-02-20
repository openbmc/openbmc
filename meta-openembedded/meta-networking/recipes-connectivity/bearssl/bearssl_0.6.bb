SUMMARY = "BearSSL is an implementation of the SSL/TLS protocol (RFC 5246) written in C"
DESCRIPTION = "BearSSL is an implementation of the SSL/TLS protocol (RFC \
5246) written in C. It aims at offering the following features: \
  * Be correct and secure. In particular, insecure protocol versions and \
    choices of algorithms are not supported, by design; cryptographic \
    algorithm implementations are constant-time by default. \
  * Be small, both in RAM and code footprint. For instance, a minimal \
    server implementation may fit in about 20 kilobytes of compiled code \
    and 25 kilobytes of RAM. \
  * Be highly portable. BearSSL targets not only “big” operating systems \
    like Linux and Windows, but also small embedded systems and even special \
    contexts like bootstrap code. \
  * Be feature-rich and extensible. SSL/TLS has many defined cipher suites \
    and extensions; BearSSL should implement most of them, and allow extra \
    algorithm implementations to be added afterwards, possibly from third \
    parties."
HOMEPAGE = "https://bearssl.org"

SECTION = "libs"

inherit lib_package

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=1fc37e1037ae673975fbcb96a98f7191"

PV .= "+git"
SRCREV = "79c060eea3eea1257797f15ea1608a9a9923aa6f"
SRC_URI = "git://www.bearssl.org/git/BearSSL;protocol=https;branch=master \
           file://0002-test-test_x509.c-fix-potential-overflow-issue.patch \
           "

SONAME = "libbearssl.so.6"
# without compile errors like 
# <..>/ld: build/obj/ghash_pclmul.o: warning: relocation against `br_ghash_pclmul' in read-only section `.text'
CFLAGS += "-fPIC"

EXTRA_OEMAKE += 'CC="${CC}" CFLAGS="${CFLAGS}" LDDLL="${CCLD} ${LDFLAGS}" LD="${CCLD}" LDFLAGS="${LDFLAGS}" \
                 BEARSSLDLL=build/${SONAME} \
                 LDDLLFLAGS="-shared -Wl,-soname,${SONAME}" \
                 ${@ "STATICLIB=no" if d.getVar('DISABLE_STATIC') != "" else "" } \
'

S = "${WORKDIR}/git"

do_install() {
	install -d ${D}/${bindir} ${D}/${libdir} ${D}/${includedir}
	install -m 0755 ${B}/build/brssl ${D}/${bindir}
	oe_libinstall -C ${B}/build libbearssl ${D}/${libdir}
	for inc in ${S}/inc/*.h; do
		install -m 0644 "${inc}" ${D}/${includedir}
	done
}
