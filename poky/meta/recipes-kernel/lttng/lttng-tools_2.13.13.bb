SECTION = "devel"
SUMMARY = "Linux Trace Toolkit Control"
DESCRIPTION = "The Linux trace toolkit is a suite of tools designed \
to extract program execution details from the Linux operating system \
and interpret them."
HOMEPAGE = "https://github.com/lttng/lttng-tools"

LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=40ef17463fbd6f377db3c47b1cbaded8 \
                    file://LICENSES/GPL-2.0;md5=e68f69a54b44ba526ad7cb963e18fbce \
                    file://LICENSES/LGPL-2.1;md5=9920968d0f2ff585ce61fae30344dd95"

include lttng-platforms.inc

DEPENDS = "liburcu popt libxml2 util-linux bison-native"
RDEPENDS:${PN} = "libgcc"
RRECOMMENDS:${PN} += "${LTTNGMODULES}"
RDEPENDS:${PN}-ptest += "make perl bash gawk babeltrace procps perl-module-overloading coreutils util-linux kmod ${LTTNGMODULES} sed python3-core grep binutils"
RDEPENDS:${PN}-ptest:append:libc-glibc = " glibc-utils"
RDEPENDS:${PN}-ptest:append:libc-musl = " musl-utils"
# babelstats.pl wants getopt-long
RDEPENDS:${PN}-ptest += "perl-module-getopt-long"

PYTHON_OPTION = "am_cv_python_pyexecdir='${PYTHON_SITEPACKAGES_DIR}' \
                 am_cv_python_pythondir='${PYTHON_SITEPACKAGES_DIR}' \
                 PYTHON_INCLUDE='-I${STAGING_INCDIR}/python${PYTHON_BASEVERSION}${PYTHON_ABI}' \
"
PACKAGECONFIG ??= "${LTTNGUST} kmod"
PACKAGECONFIG[python] = "--enable-python-bindings ${PYTHON_OPTION},,python3 swig-native"
PACKAGECONFIG[lttng-ust] = "--with-lttng-ust, --without-lttng-ust, lttng-ust"
PACKAGECONFIG[kmod] = "--with-kmod, --without-kmod, kmod"
PACKAGECONFIG[manpages] = "--enable-man-pages, --disable-man-pages, asciidoc-native xmlto-native libxslt-native"

SRC_URI = "https://lttng.org/files/lttng-tools/lttng-tools-${PV}.tar.bz2 \
           file://0001-tests-do-not-strip-a-helper-library.patch \
           file://run-ptest \
           file://lttng-sessiond.service \
           file://disable-tests.patch \
           file://0001-compat-Define-off64_t-as-off_t-on-linux.patch \
           file://0001-tests-add-check_skip_kernel_test-to-check-root-user-.patch \
           file://0001-Fix-rotation-destroy-flush-fix-session-daemon-abort-.patch \
           "

SRC_URI[sha256sum] = "ff5f4f00b081dac66092afe8e72b7c790670931cf1c1ee0deaa7f80fbc53883e"

inherit autotools ptest pkgconfig useradd python3-dir manpages systemd

CACHED_CONFIGUREVARS = "PGREP=/usr/bin/pgrep"

SYSTEMD_SERVICE:${PN} = "lttng-sessiond.service"
SYSTEMD_AUTO_ENABLE = "disable"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "tracing"

FILES:${PN} += "${libdir}/lttng/libexec/* ${datadir}/xml/lttng \
                ${PYTHON_SITEPACKAGES_DIR}/*"
FILES:${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/*.a"
FILES:${PN}-dev += "${PYTHON_SITEPACKAGES_DIR}/*.la"

# Since files are installed into ${libdir}/lttng/libexec we match 
# the libexec insane test so skip it.
# Python module needs to keep _lttng.so
INSANE_SKIP:${PN} = "libexec dev-so"
INSANE_SKIP:${PN}-dbg = "libexec"

PRIVATE_LIBS:${PN}-ptest = "libfoo.so"

do_install:append () {
    # install systemd unit file
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/lttng-sessiond.service ${D}${systemd_system_unitdir}
}

do_install_ptest () {
    for f in Makefile tests/Makefile tests/utils/utils.sh tests/regression/tools/save-load/*.lttng \
            tests/regression/tools/save-load/configuration/load-42*.lttng tests/regression/tools/health/test_health.sh \
            tests/regression/tools/metadata/utils.sh tests/regression/tools/rotation/rotate_utils.sh \
            tests/regression/tools/notification/util_event_generator.sh \
            tests/regression/tools/base-path/*.lttng; do
        install -D "${B}/$f" "${D}${PTEST_PATH}/$f"
    done

    for f in tests/utils/tap-driver.sh config/test-driver src/common/config/session.xsd src/common/mi-lttng-4.1.xsd; do
        install -D "${S}/$f" "${D}${PTEST_PATH}/$f"
    done

    # Patch in the correct path for the custom libraries a helper executable needs
    sed -i -e 's!FIXMEPTESTPATH!${PTEST_PATH}!g' "${D}${PTEST_PATH}/run-ptest"

    # Prevent 'make check' from recursing into non-test subdirectories.
    sed -i -e 's!^SUBDIRS = .*!SUBDIRS = tests!' "${D}${PTEST_PATH}/Makefile"

    # We don't need these
    sed -i -e '/dist_noinst_SCRIPTS = /,/^$/d' "${D}${PTEST_PATH}/tests/Makefile"

    # We shouldn't need to build anything in tests/utils
    sed -i -e 's!am__append_1 = . utils!am__append_1 = . !' \
        "${D}${PTEST_PATH}/tests/Makefile"

    # Copy the tests directory tree and the executables and
    # Makefiles found within.
    for d in $(find "${B}/tests" -type d -not -name .libs -printf '%P ') ; do
        install -d "${D}${PTEST_PATH}/tests/$d"
        find "${B}/tests/$d" -maxdepth 1 -executable -type f \
            -exec install -t "${D}${PTEST_PATH}/tests/$d" {} +
        # Take all .py scripts for tests using the python bindings.
        find "${B}/tests/$d" -maxdepth 1 -type f -name "*.py" \
            -exec install -t "${D}${PTEST_PATH}/tests/$d" {} +
        test -r "${B}/tests/$d/Makefile" && \
            install -t "${D}${PTEST_PATH}/tests/$d" "${B}/tests/$d/Makefile"
    done

    for d in $(find "${B}/tests" -type d -name .libs -printf '%P ') ; do
        for f in $(find "${B}/tests/$d" -maxdepth 1 -executable -type f -printf '%P ') ; do
            cp ${B}/tests/$d/$f ${D}${PTEST_PATH}/tests/`dirname $d`/$f
            case $f in
                *.so|userspace-probe-elf-*)
                    install -d ${D}${PTEST_PATH}/tests/$d/
                    ln -s  ../$f ${D}${PTEST_PATH}/tests/$d/$f
                    # Remove any rpath/runpath to pass QA check.
                    chrpath --delete ${D}${PTEST_PATH}/tests/$d/$f
                    ;;
            esac
        done
    done

    chrpath --delete ${D}${PTEST_PATH}/tests/utils/testapp/userspace-probe-elf-binary/userspace-probe-elf-binary
    chrpath --delete ${D}${PTEST_PATH}/tests/utils/testapp/userspace-probe-elf-cxx-binary/userspace-probe-elf-cxx-binary
    chrpath --delete ${D}${PTEST_PATH}/tests/regression/ust/ust-dl/libbar.so
    chrpath --delete ${D}${PTEST_PATH}/tests/regression/ust/ust-dl/libfoo.so

    #
    # Use the versioned libs of liblttng-ust-dl.
    #
    ustdl="${D}${PTEST_PATH}/tests/regression/ust/ust-dl/test_ust-dl.py"
    if [ -e $ustdl ]; then
        sed -i -e 's!:liblttng-ust-dl.so!:liblttng-ust-dl.so.0!' $ustdl
    fi

    install ${B}/tests/unit/ini_config/sample.ini ${D}${PTEST_PATH}/tests/unit/ini_config/

    # We shouldn't need to build anything in tests/regression/tools
    sed -i -e 's!^SUBDIRS = tools !SUBDIRS = !' \
        "${D}${PTEST_PATH}/tests/regression/Makefile"

    # Prevent attempts to update Makefiles during test runs, and
    # silence "Making check in $SUBDIR" messages.
    find "${D}${PTEST_PATH}" -name Makefile -type f -exec \
        sed -i -e '/Makefile:/,/^$/d' -e '/%: %.in/,/^$/d' \
        -e '/echo "Making $$target in $$subdir"; \\/d' \
        -e 's/^srcdir = \(.*\)/srcdir = ./' \
        -e 's/^builddir = \(.*\)/builddir = ./' \
        -e 's/^all-am:.*/all-am:/' \
        {} +

    find "${D}${PTEST_PATH}" -name Makefile -type f -exec \
        touch -r "${B}/Makefile" {} +

    #
    # Need to stop generated binaries from rebuilding by removing their source dependencies
    #
    sed -e 's#\(^test.*OBJECTS.=\)#disable\1#g' \
        -e 's#\(^test.*DEPENDENCIES.=\)#disable\1#g' \
        -e 's#\(^test.*SOURCES.=\)#disable\1#g' \
        -e 's#\(^test.*LDADD.=\)#disable\1#g' \
        -i ${D}${PTEST_PATH}/tests/unit/Makefile

    # Fix hardcoded build path
    sed -e 's#TESTAPP_PATH=.*/tests/regression/#TESTAPP_PATH="${PTEST_PATH}/tests/regression/#' \
        -i ${D}${PTEST_PATH}/tests/regression/ust/python-logging/test_python_logging

    # Substitute links to installed binaries.
    for prog in lttng lttng-relayd lttng-sessiond lttng-consumerd lttng-crash; do
        exedir="${D}${PTEST_PATH}/src/bin/${prog}"
        install -d "$exedir"
        case "$prog" in
            lttng-consumerd)
                ln -s "${libdir}/lttng/libexec/$prog" "$exedir"
                ;;
            *)
                ln -s "${bindir}/$prog" "$exedir"
                ;;
        esac
    done
}

INHIBIT_PACKAGE_STRIP_FILES = "\
    ${PKGD}${PTEST_PATH}/tests/utils/testapp/userspace-probe-elf-binary/userspace-probe-elf-binary \
    ${PKGD}${PTEST_PATH}/tests/utils/testapp/userspace-probe-elf-binary/.libs/userspace-probe-elf-binary \
    ${PKGD}${PTEST_PATH}/tests/utils/testapp/userspace-probe-elf-cxx-binary/userspace-probe-elf-cxx-binary \
    ${PKGD}${PTEST_PATH}/tests/utils/testapp/userspace-probe-elf-cxx-binary/.libs/userspace-probe-elf-cxx-binary \
    ${PKGD}${PTEST_PATH}/tests/utils/testapp/gen-syscall-events/gen-syscall-events \
    ${PKGD}${PTEST_PATH}/tests/utils/testapp/gen-syscall-events/.libs/gen-syscall-events \
    ${PKGD}${PTEST_PATH}/tests/utils/testapp/gen-syscall-events-callstack/gen-syscall-events-callstack \
    ${PKGD}${PTEST_PATH}/tests/utils/testapp/gen-syscall-events-callstack/.libs/gen-syscall-events-callstack \
    "
