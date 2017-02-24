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

SRCREV = "d11e0dba0df9024b8613c51e167a379b91e8b20b"
PV = "2.8.1+git${SRCPV}"

PYTHON_OPTION = "am_cv_python_pyexecdir='${PYTHON_SITEPACKAGES_DIR}' \
                 am_cv_python_pythondir='${PYTHON_SITEPACKAGES_DIR}' \
                 PYTHON_INCLUDE='-I${STAGING_INCDIR}/python${PYTHON_BASEVERSION}${PYTHON_ABI}' \
"
PACKAGECONFIG ??= "lttng-ust"
PACKAGECONFIG[python] = "--enable-python-bindings ${PYTHON_OPTION},,python3 swig-native"
PACKAGECONFIG[lttng-ust] = "--with-lttng-ust, --without-lttng-ust, lttng-ust"
PACKAGECONFIG[kmod] = "--enable-kmod, --disable-kmod, kmod"
PACKAGECONFIG[manpages] = "--enable-man-pages, --disable-man-pages, asciidoc-native"
PACKAGECONFIG_remove_libc-musl = "lttng-ust"

SRC_URI = "git://git.lttng.org/lttng-tools.git;branch=stable-2.8 \
           file://0001-Fix-error.h-common-error.h.patch \
           file://run-ptest"

S = "${WORKDIR}/git"

inherit autotools-brokensep ptest pkgconfig useradd python3-dir

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

do_configure_prepend () {
	# Delete a shipped m4 file that overrides our patched one
	rm -f ${S}/m4/libxml.m4
}

do_install_ptest () {
	mkdir -p ${D}${PTEST_PATH}

	cp -a -T ${B} ${D}${PTEST_PATH}

	# Prevent attempts to update Makefiles during test runs, and
	# silence "Making check in $SUBDIR" messages.
	find ${D}${PTEST_PATH} -name Makefile -type f -exec \
		sed -i -e 's!^Makefile:!_Makefile:!' \
		-e '/echo "Making $$target in $$subdir"; \\/d' {} +

	# Prevent attempts to update version.h during test runs.
	sed -i -e '/^\.PHONY: version\.h$/d' ${D}${PTEST_PATH}/include/Makefile

	# Silence "Making check in $SUBDIR" messages.
	find ${D}${PTEST_PATH} -name Makefile -type f -exec \
		sed -i -e '/echo "Making $$target in $$subdir"; \\/d' {} +

	# Substitute links to installed binaries.
	for prog in lttng lttng-relayd lttng-sessiond lttng-consumerd ; do
		orig="${D}${PTEST_PATH}/src/bin/${prog}/${prog}"
		rm "$orig"
		case "$prog" in
			lttng-consumerd)
				ln -s "${libdir}/lttng/libexec/$prog" "$orig"
				;;
			*)
				ln -s "${bindir}/$prog" "$orig"
				;;
		esac
	done

	# Remove libtool artifacts.
	find ${D}${PTEST_PATH} \( -name '*.l[ao]' -o -name '*.lai' \) -delete

	# Remove object files and archives.
	find ${D}${PTEST_PATH} -name '*.[oa]' -type f -delete

	# Remove Makefile.am and Makefile.in.
	find ${D}${PTEST_PATH} -name 'Makefile.*' -type f -delete

	# Remove autom4te.cache.
	rm -rf ${D}${PTEST_PATH}/autom4te.cache

	# Replace libtool wrapper scripts (which won't work on the
	# target) with their corresponding binaries.
	for prog in unit/ini_config/ini_config \
		regression/tools/live/live_test \
		regression/tools/health/health_check ; do
		basename=${prog##*/}
		ldir=${D}${PTEST_PATH}/tests/${prog%/*}
		mv -f ${ldir}/.libs/${basename} ${ldir}
	done

	# checkpatch.pl is unneeded on target and causes file-rdeps QA
	# warnings.
	rm -f ${D}${PTEST_PATH}/extras/checkpatch.pl
}
