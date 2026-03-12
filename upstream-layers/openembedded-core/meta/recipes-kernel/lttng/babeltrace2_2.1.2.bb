SUMMARY = "Babeltrace2 - Trace Format Babel Tower"
DESCRIPTION = "Babeltrace provides trace read and write libraries in host side, as well as a trace converter, which used to convert LTTng 2.0 traces into human-readable log."
HOMEPAGE = "http://babeltrace.org/"
BUGTRACKER = "https://bugs.lttng.org/projects/babeltrace"
LICENSE = "MIT & GPL-2.0-only & LGPL-2.1-only & BSD-2-Clause & BSD-4-Clause & GPL-3.0-or-later & CC-BY-SA-4.0 & PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f6b015e4f388d6e78adb1b1f9a887d06"

DEPENDS = "glib-2.0 util-linux popt bison-native flex-native virtual/libiconv swig-native"

SRC_URI = "git://git.efficios.com/babeltrace.git;branch=stable-2.1;protocol=https;tag=v${PV} \
           file://run-ptest \
           file://0001-Make-manpages-multilib-identical.patch \
           file://0001-tests-fix-test-applications-in-cpp-common.patch \
           file://0001-tests-set-the-correct-plugin-directory.patch \
           file://0001-Make-bt_field_blob_get_length-return-size_t-instead-.patch \
           file://external-python-tests.patch \
           file://0001-src-explicitly-only-build-shared-plugins.patch \
           "
SRCREV = "d0e946a71faf5f0c2d7f1fb5b92a369983e9cf10"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>2(\.\d+)+)$"

inherit autotools pkgconfig ptest setuptools3-base

EXTRA_OECONF = "--disable-debug-info --disable-Werror --enable-python-plugins --enable-python-bindings"

CCACHE_DISABLE = "1"

export DISTSETUPOPTS = " --install-lib=${PYTHON_SITEPACKAGES_DIR}"

PACKAGECONFIG ??= "manpages"
PACKAGECONFIG[manpages] = ", --disable-man-pages, asciidoc-native xmlto-native"

FILES:${PN} += "${libdir}/babeltrace2/plugins/*.so ${PYTHON_SITEPACKAGES_DIR}/*"

ASNEEDED = ""
LDFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld ptest', '-fuse-ld=bfd ', '', d)}"

# coreutils since we need full mktemp
RDEPENDS:${PN}-ptest += "bash gawk python3 make grep coreutils findutils"
RDEPENDS:${PN}-ptest:append:libc-glibc = " glibc-gconv-utf-16 glibc-gconv-utf-32"

do_configure:append() {
    # when doing cross compile, the path ${B}/src/plugins/ctf/common/src/metadata/tsdl
    # is not created by the babeltrace2 build system. It is need when generating
    # parser.cpp by executing /bin/bash ../../git/config/ylwrap.
    # So make this directory after configuration.
    mkdir -p ${B}/src/plugins/ctf/common/src/metadata/tsdl
}

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
	find "${S}/tests/$d" -maxdepth 1 -name *.ref \
	     -exec install -t "${D}${PTEST_PATH}/tests/$d" {} \;
	find "${S}/tests/$d" -maxdepth 1 -name *.mctf \
	     -exec install -t "${D}${PTEST_PATH}/tests/$d" {} \;
	find "${S}/tests/$d" -maxdepth 1 -name *.json \
	     -exec install -t "${D}${PTEST_PATH}/tests/$d" {} \;
    done
    install -d "${D}${PTEST_PATH}/tests/data/"
    cp -a ${S}/tests/data/* ${D}${PTEST_PATH}/tests/data/

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

    # Set the correct environment variables when running embedded environment
    envsh=${D}${PTEST_PATH}/tests/utils/env.sh
    sed -i "/BT_TESTS_SRCDIR/c\_set_var_def BT_TESTS_SRCDIR '${PTEST_PATH}/tests'" $envsh
    sed -i "/BT_TESTS_BUILDDIR/c\_set_var_def BT_TESTS_BUILDDIR '${PTEST_PATH}/tests'" $envsh
    sed -i "/BT_TESTS_AWK_BIN/c\_set_var_def BT_TESTS_AWK_BIN 'gawk'" $envsh
    sed -i "/BT_TESTS_GREP_BIN/c\_set_var_def BT_TESTS_GREP_BIN 'grep'" $envsh
    sed -i "/BT_TESTS_PYTHON_BIN/c\_set_var_def BT_TESTS_PYTHON_BIN 'python3'" $envsh
    sed -i "/BT_TESTS_SED_BIN/c\_set_var_def BT_TESTS_SED_BIN 'sed'" $envsh
    sed -i "/BT_TESTS_CC_BIN/c\_set_var_def BT_TESTS_CC_BIN ''" $envsh
}

do_install:append:class-nativesdk() {
    mkdir -p ${D}${SDKPATHNATIVE}/environment-setup.d
    cat <<- EOF > ${D}${SDKPATHNATIVE}/environment-setup.d/babeltrace2.sh
	export BABELTRACE_PLUGIN_PATH="${libdir}/babeltrace2/plugins"
	export LIBBABELTRACE2_PLUGIN_PROVIDER_DIR="${libdir}/babeltrace2/plugin-providers"
	EOF
}

FILES:${PN}:append:class-nativesdk = " ${SDKPATHNATIVE}/environment-setup.d/babeltrace2.sh"

BBCLASSEXTEND = "nativesdk"
