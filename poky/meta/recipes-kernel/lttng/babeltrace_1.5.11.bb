SUMMARY = "Babeltrace - Trace Format Babel Tower"
DESCRIPTION = "Babeltrace provides trace read and write libraries in host side, as well as a trace converter, which used to convert LTTng 2.0 traces into human-readable log."
HOMEPAGE = "http://babeltrace.org/"
BUGTRACKER = "https://bugs.lttng.org/projects/babeltrace"
LICENSE = "MIT & GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=76ba15dd76a248e1dd526bca0e2125fa"

DEPENDS = "glib-2.0 util-linux popt bison-native flex-native"

SRC_URI = "git://git.efficios.com/babeltrace.git;branch=stable-1.5 \
	   file://run-ptest \
	  "
SRCREV = "91c00f70884887ff5c4849a8e3d47e311a22ba9d"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>1(\.\d+)+)$"

S = "${WORKDIR}/git"

inherit autotools pkgconfig ptest

EXTRA_OECONF = "--disable-debug-info"

ASNEEDED = ""

RDEPENDS:${PN}-ptest += "bash gawk"

addtask do_patch_ptest_path after do_patch before do_configure
do_patch_ptest_path () {
    for f in $(grep -l -r abs_top_srcdir ${S}/tests); do
	sed -i 's:\@abs_top_srcdir\@:${PTEST_PATH}:' ${f}
    done

    for f in $(grep -l -r abs_top_builddir ${S}/tests); do
	sed -i 's:\@abs_top_builddir\@:${PTEST_PATH}:' ${f}
    done
    for f in $(grep -l -r GREP ${S}/tests); do
	sed -i 's:\@GREP\@:grep:' ${f}
    done

    for f in $(grep -l -r SED ${S}/tests); do
	sed -i 's:\@SED\@:sed:' ${f}
    done
}

do_compile_ptest () {
    make -C tests all
}

do_install_ptest () {
    # Copy required files from source directory
    for f in config/tap-driver.sh config/test-driver; do
	install -D "${S}/$f" "${D}${PTEST_PATH}/$f"
    done
    install -d "$f" "${D}${PTEST_PATH}/tests/ctf-traces/"
    cp -a ${S}/tests/ctf-traces/* ${D}${PTEST_PATH}/tests/ctf-traces/

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

    install -D ${B}/formats/ctf/metadata/.libs/ctf-parser-test \
	    ${D}${PTEST_PATH}/formats/ctf/metadata/ctf-parser-test

    # Prevent attempts to update Makefiles during test runs, and
    # silence "Making check in $SUBDIR" messages.
    find "${D}${PTEST_PATH}" -name Makefile -type f -exec \
	 sed -i \
	 -e '/Makefile:/,/^$/d' \
	 -e '/$(check_SCRIPTS)/s/^/#/' \
	 -e '/%: %.in/,/^$/d' \
	 -e '/echo "Making $$target in $$subdir"; \\/d' \
	 -e 's/^srcdir = \(.*\)/srcdir = ./' \
	 -e 's/^builddir = \(.*\)/builddir = ./' \
	 -e 's/^all-am:.*/all-am:/' \
	 {} +

    # Remove path to babeltrace.
    for f in $(grep -l -r "^BABELTRACE_BIN" ${D}${PTEST_PATH}); do
	sed -i 's:^BABELTRACE_BIN.*:BABELTRACE_BIN=/usr/bin/babeltrace:' ${f}
    done
    for f in $(grep -l -r "^BTBIN" ${D}${PTEST_PATH}); do
	sed -i 's:^BTBIN.*:BTBIN=/usr/bin/babeltrace:' ${f}
    done
}
