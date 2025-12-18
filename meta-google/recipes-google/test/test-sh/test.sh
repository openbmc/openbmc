#!/bin/bash
# shellcheck disable=SC2317
# Copyright 2021 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# shellcheck source=meta-google/recipes-google/test/test-sh/lib.sh
source "$(dirname "$0")/lib.sh" || exit

(
  echo '## Test Pass' >&2
  # shellcheck disable=SC2329
  test_pass() {
    return 0
  }
  main || exit
) || exit

(
  echo '## Test Fail' >&2
  i=0
  # shellcheck disable=SC2329
  test_pass1() {
    (( i++ ))
    return 0
  }
  # shellcheck disable=SC2329
  test_fail() {
    return 1
    (( i++ ))
    return 0
  }
  # shellcheck disable=SC2329
  test_pass2() {
    (( i++ ))
    return 0
  }
  ! main || exit
  (( i == 2 )) || exit
) || exit

(
  echo '## Test Deferred Fail' >&2
  i=0
  # shellcheck disable=SC2329
  test_expect_fail() {
    test_err=1 || return
    (( i++ ))
    return 0
  }
  # shellcheck disable=SC2329
  test_pass() {
    (( i++ ))
    return 0
  }
  ! main || exit
  (( i == 2 )) || exit
) || exit

(
  echo '## Test Fail' >&2
  i=0
  # shellcheck disable=SC2329
  test_fail() {
    fail 'Failed' || return
    (( i++ ))
    return 0
  }
  ! main || exit
  (( i == 1 )) || exit
) || exit

(
  echo '## Test Expect Err' >&2
  i=0
  # shellcheck disable=SC2329
  test_expect_err() {
    expect_err 1 false || return
    (( i++ ))
    return 0
  }
  main || exit
  (( i == 1 )) || exit
) || exit

(
  echo '## Test Expect Err Error' >&2
  i=0
  # shellcheck disable=SC2329
  test_expect_err() {
    expect_err 0 false || return
    (( i++ ))
    return 0
  }
  ! main || exit
  (( i == 1 )) || exit
) || exit

(
  echo '## Test Num EQ' >&2
  i=0
  # shellcheck disable=SC2329
  test_num_eq() {
    expect_numeq 15 0xf || return
    expect_numeq 1 1 || return
    (( i++ ))
    return 0
  }
  main || exit
  (( i == 1 )) || exit
) || exit

(
  echo '## Test Num EQ Error' >&2
  i=0
  # shellcheck disable=SC2329
  test_num_eq() {
    expect_numeq 15 10 || return
    (( i++ ))
    return 0
  }
  ! main || exit
  (( i == 1 )) || exit
) || exit

(
  echo '## Test Str EQ' >&2
  i=0
  # shellcheck disable=SC2329
  test_str_eq() {
    expect_streq abz abz || return
    (( i++ ))
    return 0
  }
  main || exit
  (( i == 1 )) || exit
) || exit

(
  echo '## Test Str EQ Error' >&2
  i=0
  # shellcheck disable=SC2329
  test_str_eq() {
    expect_streq 15 0xf || return
    (( i++ ))
    return 0
  }
  ! main || exit
  (( i == 1 )) || exit
) || exit
