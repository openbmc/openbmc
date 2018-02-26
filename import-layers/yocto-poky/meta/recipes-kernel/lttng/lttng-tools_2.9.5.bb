SECTION = "devel"
SUMMARY = "Linux Trace Toolkit Control"
DESCRIPTION = "The Linux trace toolkit is a suite of tools designed \
to extract program execution details from the Linux operating system \
and interpret them."

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=01d7fc4496aacf37d90df90b90b0cac1 \
                    file://gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://lgpl-2.1.txt;md5=0f0d71500e6a57fd24d825f33242b9ca"

DEPENDS = "liburcu popt libxml2 util-linux"
RDEPENDS_${PN} = "libgcc"
RDEPENDS_${PN}-ptest += "make perl bash gawk ${PN} babeltrace procps"
# babelstats.pl wants getopt-long
RDEPENDS_${PN}-ptest += "perl-module-getopt-long"

PYTHON_OPTION = "am_cv_python_pyexecdir='${PYTHON_SITEPACKAGES_DIR}' \
                 am_cv_python_pythondir='${PYTHON_SITEPACKAGES_DIR}' \
                 PYTHON_INCLUDE='-I${STAGING_INCDIR}/python${PYTHON_BASEVERSION}${PYTHON_ABI}' \
"
PACKAGECONFIG ??= "lttng-ust"
PACKAGECONFIG[python] = "--enable-python-bindings ${PYTHON_OPTION},,python3 swig-native"
PACKAGECONFIG[lttng-ust] = "--with-lttng-ust, --without-lttng-ust, lttng-ust"
PACKAGECONFIG[kmod] = "--enable-kmod, --disable-kmod, kmod"
PACKAGECONFIG[manpages] = "--enable-man-pages, --disable-man-pages, asciidoc-native xmlto-native libxslt-native"
PACKAGECONFIG_remove_libc-musl = "lttng-ust"

SRC_URI = "https://lttng.org/files/lttng-tools/lttng-tools-${PV}.tar.bz2 \
           file://x32.patch \
           file://run-ptest \
           "

SRC_URI[md5sum] = "051224eb991aee07f8721ff1877d0b96"
SRC_URI[sha256sum] = "77839eb6fc6c652125f08acfd9369701c2516eb05cc2084160e7efc7a3fb731c"

inherit autotools ptest pkgconfig useradd python3-dir manpages

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "tracing"

FILES_${PN} += "${libdir}/lttng/libexec/* ${datadir}/xml/lttng \
                ${PYTHON_SITEPACKAGES_DIR}/*"
FILES_${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/*.a"
FILES_${PN}-dev += "${PYTHON_SITEPACKAGES_DIR}/*.la"

# Since files are installed into ${libdir}/lttng/libexec we match 
# the libexec insane test so skip it.
# Python module needs to keep _lttng.so
INSANE_SKIP_${PN} = "libexec dev-so"
INSANE_SKIP_${PN}-dbg = "libexec"

do_install_ptest () {
    for f in Makefile tests/Makefile tests/utils/utils.sh ; do
        install -D "${B}/$f" "${D}${PTEST_PATH}/$f"
    done

    for f in config/tap-driver.sh config/test-driver ; do
        install -D "${S}/$f" "${D}${PTEST_PATH}/$f"
    done

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
        test -r "${B}/tests/$d/Makefile" && \
            install -t "${D}${PTEST_PATH}/tests/$d" "${B}/tests/$d/Makefile"
    done

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

    # These objects trigger [rpaths] QA checks; the test harness
    # skips the associated tests if they're missing, so delete
    # them.
    objs=""
    objs="$objs regression/ust/ust-dl/libbar.so"
    objs="$objs regression/ust/ust-dl/libfoo.so"
    for obj in $objs ; do
        rm -f "${D}${PTEST_PATH}/tests/${obj}"
    done

    find "${D}${PTEST_PATH}" -name Makefile -type f -exec \
        touch -r "${B}/Makefile" {} +

    # Substitute links to installed binaries.
    for prog in lttng lttng-relayd lttng-sessiond lttng-consumerd ; do
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
