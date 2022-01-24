# Copyright (C) 2021 Mingli Yu <mingli.yu@windriver.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "General-purpose scalable concurrent malloc implementation"

DESCRIPTION = "jemalloc is a general purpose malloc(3) implementation that emphasizes \
fragmentation avoidance and scalable concurrency support."

HOMEPAGE = "https://github.com/jemalloc/jemalloc"
LICENSE = "BSD-2-Clause"

SECTION = "libs"

LIC_FILES_CHKSUM = "file://COPYING;md5=ea061f8731d5e6a5761dfad951ef5f5f"

SRC_URI = "git://github.com/jemalloc/jemalloc.git;branch=master;protocol=https \
           file://0001-Makefile.in-make-sure-doc-generated-before-install.patch \
           file://run-ptest \
"

# Workaround for https://github.com/llvm/llvm-project/issues/52765
SRC_URI:append:libc-glibc:toolchain-clang = " file://0001-test-Disable-optimization-with-clang-for-aligned_all.patch "

SRCREV = "ea6b3e973b477b8061e0076bb257dbd7f3faa756"

S = "${WORKDIR}/git"

inherit autotools ptest

EXTRA_AUTORECONF += "--exclude=autoheader"

EXTRA_OECONF:append:libc-musl = " --with-jemalloc-prefix=je_"

do_install:append() {
	sed -i -e 's@${STAGING_DIR_HOST}@@g' \
               -e 's@${STAGING_DIR_NATIVE}@@g' \
               -e 's@${WORKDIR}@@g' ${D}${bindir}/jemalloc-config
}

do_compile_ptest() {
	oe_runmake tests
}

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	subdirs="test/unit test/integration test/stress "
	for tooltest in ${subdirs}
	do
		cp -r ${B}/${tooltest} ${D}${PTEST_PATH}/tests
	done
	find ${D}${PTEST_PATH}/tests \( -name "*.d" -o -name "*.o" \) -exec rm -f {} \;
}
