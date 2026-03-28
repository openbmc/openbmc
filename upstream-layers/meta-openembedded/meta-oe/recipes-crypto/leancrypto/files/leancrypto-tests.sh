#!/bin/sh
# SPDX-License-Identifier: MIT
#
# leancrypto test runner
# Runs all leancrypto test binaries and reports pass/fail summary
#

TESTDIR="/usr/libexec/leancrypto/tests"
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m'
PASS=0
FAIL=0
SKIP=0
FAILED=""

if [ ! -d "$TESTDIR" ]; then
    echo "ERROR: Test directory $TESTDIR not found"
    exit 1
fi

count=$(find "$TESTDIR" -maxdepth 1 -type f -executable | wc -l)
if [ "$count" -eq 0 ]; then
    echo "ERROR: No test binaries found in $TESTDIR"
    exit 1
fi

echo "Running $count leancrypto tests..."
echo ""

for t in "$TESTDIR"/*; do
    [ -x "$t" ] || continue
    name=$(basename "$t")
    printf "%-60s " "$name"
    "$t" > /dev/null 2>&1
    rc=$?
    if [ "$rc" -eq 0 ]; then
        printf "${GREEN}PASS${NC}\n"
        PASS=$((PASS + 1))
    elif [ "$rc" -eq 77 ]; then
        printf "${YELLOW}SKIP${NC}\n"
        SKIP=$((SKIP + 1))
    else
        printf "${RED}FAIL${NC}\n"
        FAIL=$((FAIL + 1))
        FAILED="$FAILED $name"
    fi
done

echo ""
echo "Results: $PASS passed, $FAIL failed, $SKIP skipped, $((PASS + FAIL + SKIP)) total"

if [ "$FAIL" -gt 0 ]; then
    echo "Failed tests:$FAILED"
    exit 1
fi
