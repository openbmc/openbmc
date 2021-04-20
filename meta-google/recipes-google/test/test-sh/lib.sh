#!/bin/bash
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

expect_streq() {
  local r="${2-$(cat)}"
  [ "$1" = "$r" ] && return
  echo "  Line ${BASH_LINENO[0]} '$1' != '$r'" >&2
  test_err=1
}

expect_numeq() {
  (( "$1" == "$2" )) && return
  echo "  Line ${BASH_LINENO[0]} '$1' != '$2'" >&2
  test_err=1
}

expect_err() {
  local expected=$1
  shift
  local rc=0
  "$@" || rc="$?"
  (( rc == expected )) && return
  echo "  Line ${BASH_LINENO[0]} Status '$rc' != '$expected'" >&2
  test_err=1
}

fail() {
  echo "  Line ${BASH_LINENO[0]} Fail" >&2
  test_err=1
}

main() {
  local agg_err=0
  for f in $(declare -F | grep 'declare -f test[A-Z_]' | awk '{print $3}'); do
    echo "[$f] Running..." >&2
    local test_err=0
    if "$f" && (( test_err == 0 )); then
      echo "[$f] Success" >&2
    else
      echo "[$f] Failed ($?)" >&2
      agg_err=1
    fi
  done
  return $agg_err
}
