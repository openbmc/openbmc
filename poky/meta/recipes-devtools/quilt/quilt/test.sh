#! /bin/sh

set -e -u

export LANG=C
export LC_ALL=C
export P=patches/
export _P=../patches/
export QUILTRC=$(pwd)/test/test.quiltrc
export QUILT_PC=.pc
export QUILT_DIR=/usr/share/quilt/

# Specify on the commandline, else runs all of the tests
TESTS=${@:-test/*.test}

for FILENAME in $TESTS; do
    TESTNAME=$(basename $FILENAME .test)
    ./test/run $FILENAME
    if [ $? -eq 0 ];
        then echo PASS: $TESTNAME
    else
        echo FAIL: $TESTNAME
    fi
done
