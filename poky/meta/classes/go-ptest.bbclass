inherit go ptest

do_compile_ptest_base() {
	export TMPDIR="${GOTMPDIR}"
	rm -f ${B}/.go_compiled_tests.list
	go_list_package_tests | while read pkg; do
		cd ${B}/src/$pkg
		${GO} test ${GOPTESTBUILDFLAGS} $pkg
		find . -mindepth 1 -maxdepth 1 -type f -name '*.test' -exec echo $pkg/{} \; | \
			sed -e's,/\./,/,'>> ${B}/.go_compiled_tests.list
	done
	do_compile_ptest
}

do_compile_ptest_base[dirs] =+ "${GOTMPDIR}"

go_make_ptest_wrapper() {
	cat >${D}${PTEST_PATH}/run-ptest <<EOF
#!/bin/sh
RC=0
run_test() (
    cd "\$1"
    ((((./\$2 ${GOPTESTFLAGS}; echo \$? >&3) | sed -r -e"s,^(PASS|SKIP|FAIL)\$,\\1: \$1/\$2," >&4) 3>&1) | (read rc; exit \$rc)) 4>&1
    exit \$?)
EOF

}

do_install_ptest_base() {
	test -f "${B}/.go_compiled_tests.list" || exit 0
	install -d ${D}${PTEST_PATH}
	go_stage_testdata
	go_make_ptest_wrapper
	havetests=""
	while read test; do
		testdir=`dirname $test`
		testprog=`basename $test`
		install -d ${D}${PTEST_PATH}/$testdir
		install -m 0755 ${B}/src/$test ${D}${PTEST_PATH}/$test
	echo "run_test $testdir $testprog || RC=1" >> ${D}${PTEST_PATH}/run-ptest
		havetests="yes"
	done < ${B}/.go_compiled_tests.list
	if [ -n "$havetests" ]; then
		echo "exit \$RC" >> ${D}${PTEST_PATH}/run-ptest
		chmod +x ${D}${PTEST_PATH}/run-ptest
	else
		rm -rf ${D}${PTEST_PATH}
	fi
	do_install_ptest
	chown -R root:root ${D}${PTEST_PATH}
}

INSANE_SKIP_${PN}-ptest += "ldflags"

