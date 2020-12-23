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

SRCREV = "8ef7680081c61b486622f2d983c0d3d21e83caad"
SRC_URI = "git://www.bearssl.org/git/BearSSL;protocol=https;nobranch=1 \
	   file://0001-conf-Unix.mk-remove-fixed-command-definitions.patch \
	   file://0002-test-test_x509.c-fix-potential-overflow-issue.patch \
           file://0001-make-Pass-LDFLAGS-when-building-shared-objects.patch \
	   "

# without compile errors like 
# <..>/ld: build/obj/ghash_pclmul.o: warning: relocation against `br_ghash_pclmul' in read-only section `.text'
CFLAGS += "-fPIC"

S = "${WORKDIR}/git"
B = "${S}"

do_install() {
    mkdir -p ${D}/${bindir} ${D}/${libdir}
    install -m 0644 ${B}/build/brssl ${D}/${bindir}
    install -m 0644 ${B}/build/libbearssl.so ${D}/${libdir}/libbearssl.so.6.0.0
    ln -s libbearssl.so.6.0.0 ${D}/${libdir}/libbearssl.so.6
    ln -s libbearssl.so.6.0.0 ${D}/${libdir}/libbearssl.so
}
