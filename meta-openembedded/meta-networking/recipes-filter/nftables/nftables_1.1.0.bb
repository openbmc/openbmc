SUMMARY = "Netfilter Tables userspace utillites"
DESCRIPTION = "nftables replaces the popular {ip,ip6,arp,eb}tables. \
               This software provides an in-kernel packet classification framework \
               that is based on a network-specific Virtual Machine (VM), \
               nft, a userspace command line tool and libnftables, a high-level userspace library."
HOMEPAGE = "https://netfilter.org/projects/nftables"
SECTION = "net"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=81ec33bb3e47b460fc993ac768c74b62"

DEPENDS = "libmnl libnftnl bison-native \
           ${@bb.utils.contains('PACKAGECONFIG', 'mini-gmp', '', 'gmp', d)}"

SRC_URI = "http://www.netfilter.org/projects/nftables/files/${BP}.tar.xz \
           file://run-ptest \
          "
SRC_URI[sha256sum] = "ef3373294886c5b607ee7be82c56a25bc04e75f802f8e8adcd55aac91eb0aa24"

inherit autotools manpages pkgconfig ptest

PACKAGECONFIG ?= "python readline json"
PACKAGECONFIG[editline] = "--with-cli=editline, , libedit, , , linenoise readline"
PACKAGECONFIG[json] = "--with-json, --without-json, jansson"
PACKAGECONFIG[linenoise] = "--with-cli=linenoise, , linenoise, , , editline readline"
PACKAGECONFIG[manpages] = "--enable-man-doc, --disable-man-doc, asciidoc-native"
PACKAGECONFIG[mini-gmp] = "--with-mini-gmp, --without-mini-gmp"
PACKAGECONFIG[python] = ""
PACKAGECONFIG[readline] = "--with-cli=readline, , readline, , , editline linenoise"
PACKAGECONFIG[xtables] = "--with-xtables, --without-xtables, iptables"

EXTRA_OECONF = " \
    ${@bb.utils.contains_any('PACKAGECONFIG', 'editline linenoise readline', '', '--without-cli', d)}"

PEP517_SOURCE_PATH = "${S}/py"

inherit_defer ${@bb.utils.contains('PACKAGECONFIG', 'python', 'python_setuptools_build_meta', '', d)}

PACKAGES =+ "${@bb.utils.contains('PACKAGECONFIG', 'python', '${PN}-python', '', d)}"
FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"
RDEPENDS:${PN}-python = "python3-core python3-json ${PN}"

# Explicitly define do_configure, do_compile and do_install because both autotools and setuptools3
# have EXPORT_FUNCTIONS do_configure do_compile do_install
do_configure() {
    autotools_do_configure
    if ${@bb.utils.contains('PACKAGECONFIG', 'python', 'true', 'false', d)}; then
        python_pep517_do_configure
    fi
}

do_compile() {
    autotools_do_compile
    if ${@bb.utils.contains('PACKAGECONFIG', 'python', 'true', 'false', d)}; then
        python_pep517_do_compile
    fi
}

do_install() {
    autotools_do_install
    if ${@bb.utils.contains('PACKAGECONFIG', 'python', 'true', 'false', d)}; then
        python_pep517_do_install
    fi
}

RDEPENDS:${PN}-ptest += " ${PN}-python bash coreutils make iproute2 iputils-ping procps python3-core python3-ctypes python3-json python3-misc sed util-linux"

RRECOMMENDS:${PN}-ptest += "\
kernel-module-nft-chain-nat     kernel-module-nft-queue \
kernel-module-nft-compat        kernel-module-nft-quota \
kernel-module-nft-connlimit     kernel-module-nft-redir \
kernel-module-nft-ct            kernel-module-nft-reject \
kernel-module-nft-flow-offload  kernel-module-nft-reject-inet \
kernel-module-nft-hash          kernel-module-nft-reject-ipv4 \
kernel-module-nft-limit         kernel-module-nft-reject-ipv6 \
kernel-module-nft-log           kernel-module-nft-socket \
kernel-module-nft-masq          kernel-module-nft-synproxy \
kernel-module-nft-nat           kernel-module-nft-tunnel \
kernel-module-nft-numgen        kernel-module-nft-xfrm \
kernel-module-nft-osf \
kernel-module-nf-flow-table \
kernel-module-nf-flow-table-inet \
kernel-module-nf-nat \
kernel-module-nf-log-syslog \
kernel-module-nf-nat-ftp \
kernel-module-nf-nat-sip \
kernel-module-8021q \
kernel-module-dummy"

TESTDIR = "tests"

PRIVATE_LIBS:${PN}-ptest:append = " libnftables.so.1"

do_install_ptest() {
    cp -rf ${S}/build-aux ${D}${PTEST_PATH}
    cp -rf ${S}/src ${D}${PTEST_PATH}
    mkdir -p ${D}${PTEST_PATH}/src/.libs
    cp -rf ${B}/src/.libs/* ${D}${PTEST_PATH}/src/.libs
    cp -rf ${B}/src/.libs/nft ${D}${PTEST_PATH}/src/
    cp -rf ${S}/${TESTDIR} ${D}${PTEST_PATH}/${TESTDIR}
    sed -i 's#/usr/bin/python#/usr/bin/python3#' ${D}${PTEST_PATH}/${TESTDIR}/json_echo/run-test.py
    sed -i 's#/usr/bin/env python#/usr/bin/env python3#' ${D}${PTEST_PATH}/${TESTDIR}/py/nft-test.py
    # handle multilib
    sed -i s:@libdir@:${libdir}:g ${D}${PTEST_PATH}/run-ptest
}
