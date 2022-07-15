SUMMARY = "Netfilter Tables userspace utillites"
SECTION = "net"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d1a78fdd879a263a5e0b42d1fc565e79"

DEPENDS = "libmnl libnftnl bison-native \
           ${@bb.utils.contains('PACKAGECONFIG', 'mini-gmp', '', 'gmp', d)}"

SRC_URI = "http://www.netfilter.org/projects/nftables/files/${BP}.tar.bz2 \
           file://0001-nftables-python-Split-root-from-prefix.patch \
           file://run-ptest \
          "

SRC_URI[sha256sum] = "927fb1fea1f685a328c10cf791eb655d7e1ed49d310eea5cb3101dfd8d6cba35"

inherit autotools manpages pkgconfig ptest

PACKAGECONFIG ?= "python readline json"
PACKAGECONFIG[editline] = "--with-cli=editline, , libedit, , , linenoise readline"
PACKAGECONFIG[json] = "--with-json, --without-json, jansson"
PACKAGECONFIG[linenoise] = "--with-cli=linenoise, , linenoise, , , editline readline"
PACKAGECONFIG[manpages] = "--enable-man-doc, --disable-man-doc, asciidoc-native"
PACKAGECONFIG[mini-gmp] = "--with-mini-gmp, --without-mini-gmp"
PACKAGECONFIG[python] = "--enable-python --with-python-bin=${PYTHON}, --disable-python, python3-setuptools-native"
PACKAGECONFIG[readline] = "--with-cli=readline, , readline, , , editline linenoise"
PACKAGECONFIG[xtables] = "--with-xtables, --without-xtables, iptables"

EXTRA_OECONF = "${@bb.utils.contains_any('PACKAGECONFIG', 'editline linenoise readline', '', '--without-cli', d)}"

inherit ${@bb.utils.contains('PACKAGECONFIG', 'python', 'python3native', '', d)}

RRECOMMENDS:${PN} += "kernel-module-nf-tables"

PACKAGES =+ "${PN}-python"
FILES:${PN}-python = "${nonarch_libdir}/${PYTHON_DIR}"
RDEPENDS:${PN}-python = "python3-core python3-json ${PN}"

RDEPENDS:${PN}-ptest += " ${PN}-python make bash python3-core python3-ctypes python3-json python3-misc util-linux"

TESTDIR = "tests"

PRIVATE_LIBS:${PN}-ptest:append = "libnftables.so.1"

do_install_ptest() {
    cp -rf ${S}/build-aux ${D}${PTEST_PATH}
    cp -rf ${S}/src ${D}${PTEST_PATH}
    mkdir -p ${D}${PTEST_PATH}/src/.libs
    cp -rf ${B}/src/.libs/* ${D}${PTEST_PATH}/src/.libs
    cp -rf ${B}/src/.libs/nft ${D}${PTEST_PATH}/src/
    cp -rf ${S}/${TESTDIR} ${D}${PTEST_PATH}/${TESTDIR}
    sed -i 's#/usr/bin/python#/usr/bin/python3#' ${D}${PTEST_PATH}/${TESTDIR}/json_echo/run-test.py
    sed -i 's#/usr/bin/env python#/usr/bin/env python3#' ${D}${PTEST_PATH}/${TESTDIR}/py/nft-test.py
}
