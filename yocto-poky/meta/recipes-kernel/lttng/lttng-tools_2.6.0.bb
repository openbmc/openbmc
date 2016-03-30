SECTION = "devel"
SUMMARY = "Linux Trace Toolkit Control"
DESCRIPTION = "The Linux trace toolkit is a suite of tools designed \
to extract program execution details from the Linux operating system \
and interpret them."

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=01d7fc4496aacf37d90df90b90b0cac1 \
                    file://gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://lgpl-2.1.txt;md5=0f0d71500e6a57fd24d825f33242b9ca"

DEPENDS = "liburcu popt lttng-ust libxml2"
RDEPENDS_${PN}-ptest += "make perl bash"

SRCREV = "d522c1f14285e2e8b10b7c0cd011847696ffe779"

PYTHON_OPTION = "am_cv_python_pyexecdir='${libdir}/python${PYTHON_BASEVERSION}/site-packages' \
                 am_cv_python_pythondir='${libdir}/python${PYTHON_BASEVERSION}/site-packages' \
                 PYTHON_INCLUDE='-I${STAGING_INCDIR}/python${PYTHON_BASEVERSION}' \
"
PACKAGECONFIG ??= "lttng-ust"
PACKAGECONFIG[python] = "--enable-python-bindings ${PYTHON_OPTION},,python swig-native"
PACKAGECONFIG[lttng-ust] = "--enable-lttng-ust, --disable-lttng-ust, lttng-ust"
PACKAGECONFIG[kmod] = "--enable-kmod, --disable-kmod, kmod"

SRC_URI = "git://git.lttng.org/lttng-tools.git;branch=stable-2.6 \
           file://runtest-2.4.0.patch \
           file://extern-decls.patch \
           file://run-ptest \
           file://lttng-tools-Fix-live-timer-calculation-error.patch \
           file://0001-Fix-sessiond-disable-match-app-event-by-name.patch \
	  "

S = "${WORKDIR}/git"

inherit autotools-brokensep ptest pkgconfig useradd

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "tracing"

FILES_${PN} += "${libdir}/lttng/libexec/* ${datadir}/xml/lttng \
                ${libdir}/python${PYTHON_BASEVERSION}/site-packages/*"
FILES_${PN}-dbg += "${libdir}/lttng/libexec/.debug \
                    ${libdir}/python2.7/site-packages/.debug"
FILES_${PN}-staticdev += "${libdir}/python${PYTHON_BASEVERSION}/site-packages/*.a"
FILES_${PN}-dev += "${libdir}/python${PYTHON_BASEVERSION}/site-packages/*.la"

# Since files are installed into ${libdir}/lttng/libexec we match 
# the libexec insane test so skip it.
# Python module needs to keep _lttng.so
INSANE_SKIP_${PN} = "libexec dev-so"
INSANE_SKIP_${PN}-dbg = "libexec"

do_configure_prepend () {
	# Delete a shipped m4 file that overrides our patched one
	rm -f ${S}/config/libxml.m4
}

do_install_ptest () {
	chmod +x ${D}${PTEST_PATH}/tests/utils/utils.sh
	for i in `find ${D}${PTEST_PATH} -perm /u+x -type f`; do
		sed -e "s:\$TESTDIR.*/src/bin/lttng/\$LTTNG_BIN:\$LTTNG_BIN:g" \
		  -e "s:\$TESTDIR/../src/bin/lttng-sessiond/\$SESSIOND_BIN:\$SESSIOND_BIN:g" \
		  -e "s:\$DIR/../src/bin/lttng-sessiond/\$SESSIOND_BIN:\$SESSIOND_BIN:g" \
		  -e "s:\$TESTDIR/../src/bin/lttng-consumerd/:${libdir}/lttng/libexec/:g" \
		  -e "s:\$DIR/../src/bin/lttng-consumerd/:${libdir}/lttng/libexec/:g" \
		  -e "s:\$TESTDIR/../src/bin/lttng-relayd/\$RELAYD_BIN:\$RELAYD_BIN:g" \
		  -e "s:\$DIR/../src/bin/lttng-sessiond/lttng-sessiond:\$SESSIOND_BIN:g" \
		  -e "s:\$DIR/../src/bin/lttng-relayd/\$RELAYD_BIN:\$RELAYD_BIN:g" \
		  -e "s:\$DIR/../bin/lttng-relayd/\$RELAYD_BIN:\$RELAYD_BIN:g" \
		  -i $i
	done

	sed -e "s:src/bin/lttng-sessiond:$bindir:g" \
	    -e "s:src/bin/lttng-consumerd:${libexecdir}/libexec/:g" \
	-i ${D}${PTEST_PATH}/tests/regression/run-report.py
	sed -e "s:src/bin:bin:g" -e "s:lt-::g" \
	-i ${D}${PTEST_PATH}/tests/utils/utils.sh
	sed -e "s:ini_config:\.libs\/ini_config:" \
	-i ${D}${PTEST_PATH}/tests/unit/ini_config/test_ini_config
}
