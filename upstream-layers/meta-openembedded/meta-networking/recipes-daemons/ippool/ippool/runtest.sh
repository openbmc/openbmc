#!/bin/sh
BANNER="----------------------------------------------------------------------------"
TCLSH="tclsh all.tcl -preservecore 3 -verbose bps -tmpdir ./results -outfile test-ippool.result"

test_setup() {
        if [ -d ./results ]; then rm -fr ./results; fi
        mkdir ./results
}

test_ippool() {
        echo "${BANNER}"
        eval $TCLSH -constraints "ipPool"
}
test_postprocess() {
        echo "${BANNER}"
        (failed=`grep FAILED results/*.result | wc -l`; \
        let failed2=failed/2 ;\
        passed=`grep PASSED results/*.result | wc -l`; \
        echo "TEST SUMMARY: $passed tests PASSED, $failed2 tests FAILED" ;\
        exit $failed2)
}

test_setup
test_ippool
test_postprocess

