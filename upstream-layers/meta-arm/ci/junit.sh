#! /bin/bash

# $ ci/junit.sh <build directory>
#
# If there is a OEQA test report in JSON format present in the build directory,
# transform it to JUnit XML using resulttool.

set -e -u

BUILDDIR=$1
JSON=$BUILDDIR/tmp/log/oeqa/testresults.json

if test -f $JSON; then
    resulttool junit $JSON
fi
