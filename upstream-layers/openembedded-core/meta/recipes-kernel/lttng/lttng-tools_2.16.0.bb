SECTION = "devel"
SUMMARY = "Linux Trace Toolkit Control"
DESCRIPTION = "The Linux trace toolkit is a suite of tools designed \
to extract program execution details from the Linux operating system \
and interpret them."
HOMEPAGE = "https://github.com/lttng/lttng-tools"

CODE_LICENSES = "BSD-2-Clause AND BSD-3-Clause AND BSL-1.0 AND CC0-1.0 AND FSFAP AND GPL-2.0-only AND GPL-2.0-or-later AND GPL-2.0-or-later WITH Autoconf-exception-2.0 AND GPL-2.0-or-later WITH Autoconf-exception-macro AND LGPL-2.1-only AND LGPL-2.1-or-later AND MIT"

LICENSE = "${CODE_LICENSES} AND CC-BY-SA-4.0"
LICENSE:${PN} = "${CODE_LICENSES}"
LICENSE:${PN}-dev = "${CODE_LICENSES}"
LICENSE:${PN}-dbg = "${CODE_LICENSES}"
LICENSE:${PN}-ptest = "${CODE_LICENSES}"
LICENSE:${PN}-src = "${CODE_LICENSES}"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f7adf214dab700ec91b393898b515f13 \
                    file://LICENSES/BSD-2-Clause.txt;md5=5d6306d1b08f8df623178dfd81880927 \
                    file://LICENSES/BSD-3-Clause.txt;md5=4e91b9e6ef320f74590c8c7a64a9188a \
                    file://LICENSES/BSL-1.0.txt;md5=4415c1f5128c6d51273f9c8362305778 \
                    file://LICENSES/CC0-1.0.txt;md5=65d3616852dbf7b1a6d4b53b00626032 \
                    file://LICENSES/CC-BY-SA-4.0.txt;md5=62ccd6e67b4925bbb3063925cd0e57e5 \
                    file://LICENSES/FSFAP.txt;md5=232368338ef6dc99de71c2e05ff12176 \
                    file://LICENSES/GPL-2.0-only.txt;md5=3d26203303a722dedc6bf909d95ba815 \
                    file://LICENSES/GPL-2.0-or-later.txt;md5=3d26203303a722dedc6bf909d95ba815 \
                    file://LICENSES/GPL-3.0-or-later.txt;md5=75d892af193fd5a298f724c4377d8f62 \
                    file://LICENSES/LGPL-2.1-only.txt;md5=41890f71f740302b785c27661123bff5 \
                    file://LICENSES/LGPL-2.1-or-later.txt;md5=41890f71f740302b785c27661123bff5 \
                    file://LICENSES/MIT.txt;md5=e8f57dd048e186199433be2c41bd3d6d \
                    file://LICENSES/Autoconf-exception-2.0.txt;md5=13a739a6793bb6742c30b1d3727df7e2 \
                    file://LICENSES/Autoconf-exception-macro.txt;md5=887fe9d860687d5c7602a5d8ab978171"

include lttng-platforms.inc

DEPENDS = "liburcu popt libxml2 util-linux bison-native babeltrace2"
RDEPENDS:${PN} = "libgcc"
RDEPENDS:${PN}-ptest += "make perl bash gawk procps perl-module-overloading coreutils util-linux kmod ${LTTNGMODULES} sed python3-core grep binutils python3-multiprocessing"
RDEPENDS:${PN}-ptest:append:libc-glibc = " glibc-utils"
RDEPENDS:${PN}-ptest:append:libc-musl = " musl-utils"
# babelstats.pl wants getopt-long
RDEPENDS:${PN}-ptest += "perl-module-getopt-long \
                         babeltrace2 \
                         lttng-ust-dev \
                         python3-asyncio \
                         python3-logging \
                         python3-math \
                         python3-numbers \
                         python3-json \
                         python3-io \
                         python3-shell \
                         python3-sqlite3 \
                         python3-xml \
                         python3-resource \
"

INSANE_SKIP:${PN}-ptest += "dev-deps"

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
           file://disable-tests2.patch \
           file://0001-m4-ax_am_macros_static.m4-do-not-write-generation-da.patch \
           file://muslfix.patch \
           "

SRC_URI[sha256sum] = "b8b3244894e49e773d4942b8899f768057974edf75c18dbb48b65bb123c7b2c7"

inherit autotools ptest pkgconfig useradd python3-dir manpages systemd upstream-stable-release-point

CACHED_CONFIGUREVARS = "PGREP=/usr/bin/pgrep"

EXTRA_OECONF += "--disable-libtool-linkdep-fixup"

SYSTEMD_SERVICE:${PN} = "lttng-sessiond.service"
SYSTEMD_AUTO_ENABLE = "disable"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "tracing"

FILES:${PN} += "${libdir}/lttng/libexec/* ${datadir}/xml/lttng \
                ${PYTHON_SITEPACKAGES_DIR}/* \
                ${libdir}/lttng/libtpp*"
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
            tests/utils/lttng-build-profile.json \
            tests/regression/tools/save-load/configuration/load-42*.lttng tests/regression/tools/health/test_health.sh \
            tests/regression/tools/metadata/utils.sh tests/regression/tools/rotation/rotate_utils.sh \
            tests/regression/tools/trace-format/ust-local-trace-pretty.expect* \
            tests/regression/tools/trace-format/kernel-local-trace-pretty.expect* \
            tests/regression/tools/base-path/*.lttng; do
        install -D "${B}/$f" "${D}${PTEST_PATH}/$f"
    done

    for f in tests/utils/tap-driver.sh config/test-driver src/common/session.xsd src/common/mi-lttng-4.3.xsd \
             tests/regression/tests.serial; do
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

    chrpath --delete ${D}${PTEST_PATH}/tests/utils/testapp/userspace-probe-elf-binary
    chrpath --delete ${D}${PTEST_PATH}/tests/utils/testapp/userspace-probe-elf-cxx-binary
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

do_install_ptest:append:libc-musl () {
    # filter-out running regression testsuite on musl systems, it hangs
    # Keep it until https://bugs.lttng.org/issues/1432 is fixed upstream
    sed -i -e '$a\' -e 'SUBDIRS := $(filter-out regression,$(SUBDIRS))' ${D}${PTEST_PATH}/tests/Makefile
}

INHIBIT_PACKAGE_STRIP_FILES = "\
    ${PKGD}${PTEST_PATH}/tests/utils/testapp/userspace-probe-elf-binary \
    ${PKGD}${PTEST_PATH}/tests/utils/testapp/.libs/userspace-probe-elf-binary \
    ${PKGD}${PTEST_PATH}/tests/utils/testapp/userspace-probe-elf-cxx-binary \
    ${PKGD}${PTEST_PATH}/tests/utils/testapp/.libs/userspace-probe-elf-cxx-binary \
    ${PKGD}${PTEST_PATH}/tests/utils/testapp/gen-syscall-events \
    ${PKGD}${PTEST_PATH}/tests/utils/testapp/.libs/gen-syscall-events \
    ${PKGD}${PTEST_PATH}/tests/utils/testapp/gen-syscall-events-callstack \
    ${PKGD}${PTEST_PATH}/tests/utils/testapp/.libs/gen-syscall-events-callstack \
    "
