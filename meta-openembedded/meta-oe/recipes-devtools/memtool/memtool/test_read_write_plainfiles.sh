#!/bin/bash

# SPDX-FileCopyrightText: 2024 Bosch Sicherheitssysteme GmbH
#
# SPDX-License-Identifier: MIT

# This script verifies the behavior of memtool against plain files.

readonly PLAIN_FILE=$(mktemp)
FAIL_COUNT=0

setup() {
	echo "Hello World!" >"$PLAIN_FILE"
}

teardown() {
	rm "$PLAIN_FILE"
}

verify() {
	ACTUAL=$1
	EXPECTED=$2
	TEST_NAME=$3

	if [ "$ACTUAL" = "$EXPECTED" ]; then
		echo "pass: $TEST_NAME"
	else
		echo "FAIL: $TEST_NAME"
		echo "  Expected: $EXPECTED"
		echo "  Actual: $ACTUAL"
		FAIL_COUNT=$((FAIL_COUNT + 1))
	fi
}

# Test Case: Verifies that the expected string of bytes is read from a plain file starting from
# the offset 6 and reading 6 bytes.
test_memtool_read() {
	EXPECTED="00000006: 57 6f 72 6c 64 21                                  World!"
	ACTUAL=$(memtool md -s "$PLAIN_FILE" -b 0x6+6)
	verify "$ACTUAL" "$EXPECTED" "memtool read from plain file"
}

# Test Case 2: Verifies that the expected string of bytes is written to a plain file starting from
# and then read the result.
test_memtool_write() {
	# Usage of 'od' ensures correct endianess.
	readonly replace_str_bytes=$(echo "Yocto!" | od -t d4 -A n)

	# shellcheck disable=SC2086 # We want to pass the bytes as separate arguments.
	memtool mw -d "$PLAIN_FILE" 0x6+6 $replace_str_bytes

	EXPECTED="00000006: 59 6f 63 74 6f 21                                  Yocto!"
	ACTUAL=$(memtool md -s "$PLAIN_FILE" -b 0x6+6)
	verify "$ACTUAL" "$EXPECTED" "memtool write to plain file"
}

for test_case in $(declare -F | grep test_memtool_ | cut -f 3 -d ' '); do
	setup
	$test_case
	teardown
done

if [ $FAIL_COUNT -eq 0 ]; then
	echo "Test Passed: memtool plain file read/write functionality is correct."
	exit 0
else
	echo "Test FAILED: memtool plain file read/write functionality is incorrect. Check the logs."
	exit 1
fi
