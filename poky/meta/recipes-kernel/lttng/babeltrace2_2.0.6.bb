SUMMARY = "Babeltrace2 - Trace Format Babel Tower"
DESCRIPTION = "Babeltrace provides trace read and write libraries in host side, as well as a trace converter, which used to convert LTTng 2.0 traces into human-readable log."
HOMEPAGE = "http://babeltrace.org/"
BUGTRACKER = "https://bugs.lttng.org/projects/babeltrace"
LICENSE = "MIT & GPL-2.0-only & LGPL-2.1-only & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a6a458c13f18385b7bc5069a6d7b176e"

DEPENDS = "glib-2.0 util-linux popt bison-native flex-native"

SRC_URI = "git://git.efficios.com/babeltrace.git;branch=stable-2.0;protocol=https \
           file://run-ptest \
           file://0001-tests-do-not-run-test-applications-from-.libs.patch \
           file://0001-Make-manpages-multilib-identical.patch \
           "
SRCREV = "0a6632f77801f3218a288604c646f8a39cb0d2c4"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>2(\.\d+)+)$"

S = "${WORKDIR}/git"

inherit autotools pkgconfig ptest python3targetconfig

EXTRA_OECONF = "--disable-debug-info --disable-Werror"

PACKAGECONFIG ??= "manpages"
PACKAGECONFIG[manpages] = ", --disable-man-pages, asciidoc-native xmlto-native"

FILES:${PN}-staticdev += "${libdir}/babeltrace2/plugins/*.a"
FILES:${PN} += "${libdir}/babeltrace2/plugins/*.so"

ASNEEDED = ""
LDFLAGS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld ptest', ' -fuse-ld=bfd ', '', d)}"

# coreutils since we need full mktemp
RDEPENDS:${PN}-ptest += "bash gawk python3 make grep coreutils findutils"

do_compile_ptest () {
    make -C tests all
}

do_install_ptest () {
    install -d "${D}${PTEST_PATH}/tests"

    # Copy required files from source directory
    for d in $(find "${S}/tests" -type d -printf '%P ') ; do
	install -d "${D}${PTEST_PATH}/tests/$d"
	find "${S}/tests/$d" -maxdepth 1 -executable -type f \
	     -exec install -t "${D}${PTEST_PATH}/tests/$d" {} +
	find "${S}/tests/$d" -maxdepth 1 -name *.sh \
	     -exec install -t "${D}${PTEST_PATH}/tests/$d" {} \;
	find "${S}/tests/$d" -maxdepth 1 -name *.py \
	     -exec install -t "${D}${PTEST_PATH}/tests/$d" {} \;
	find "${S}/tests/$d" -maxdepth 1 -name *.expect \
	     -exec install -t "${D}${PTEST_PATH}/tests/$d" {} \;
    done
    install -d "${D}${PTEST_PATH}/tests/data/ctf-traces/"
    cp -a ${S}/tests/data/ctf-traces/* ${D}${PTEST_PATH}/tests/data/ctf-traces/

    # Copy the tests directory tree and the executables and
    # Makefiles found within.
    install -D "${B}/tests/Makefile" "${D}${PTEST_PATH}/tests/"
    for d in $(find "${B}/tests" -type d -not -name .libs -printf '%P ') ; do
	install -d "${D}${PTEST_PATH}/tests/$d"
	find "${B}/tests/$d" -maxdepth 1 -executable -type f \
	     -exec install -t "${D}${PTEST_PATH}/tests/$d" {} +
	test -r "${B}/tests/$d/Makefile" && \
	    install -t "${D}${PTEST_PATH}/tests/$d" "${B}/tests/$d/Makefile"
	find "${B}/tests/$d" -maxdepth 1 -name *.sh \
	     -exec install -t "${D}${PTEST_PATH}/tests/$d" {} \;
    done

    for d in $(find "${B}/tests" -type d -name .libs -printf '%P ') ; do
	for f in $(find "${B}/tests/$d" -maxdepth 1 -executable -type f -printf '%P ') ; do
	    cp ${B}/tests/$d/$f ${D}${PTEST_PATH}/tests/`dirname $d`/$f
	done
    done

    # Prevent attempts to update Makefiles during test runs, and
    # silence "Making check in $SUBDIR" messages.
    find "${D}${PTEST_PATH}" -name Makefile -type f -exec \
	 sed -i \
	 -e '/Makefile:/,/^$/d' \
	 -e '/%: %.in/,/^$/d' \
	 -e '/echo "Making $$target in $$subdir"; \\/d' \
	 -e 's/^srcdir = \(.*\)/srcdir = ./' \
	 -e 's/^builddir = \(.*\)/builddir = ./' \
	 -e 's/^all-am:.*/all-am:/' \
	 {} +

    # Substitute links to installed binaries.
    install -d "${D}${PTEST_PATH}/src/cli/"
    ln -s "${bindir}/babeltrace2" ${D}${PTEST_PATH}/src/cli/

    # Remove architechture specific testfiles
    rm -rf ${D}${PTEST_PATH}/tests/data/plugins/flt.lttng-utils.debug-info/*
}
