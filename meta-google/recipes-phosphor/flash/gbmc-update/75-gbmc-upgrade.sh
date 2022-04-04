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

[ -z "${gbmc_upgrade-}" ] || exit

: "${GBMC_UPGRADE_SIG=/tmp/bmc.sig}"

gbmc_upgrade_hook() {
  [ -n "${bootfile_url-}" ] || return

  local tmpdir
  tmpdir="$(mktemp -d)" || return
  gbmc_upgrade_internal || true
  # SC doesn't know our variable is defined elsewhere
  # shellcheck disable=SC2153
  rm -rf -- "$tmpdir" "$GBMC_UPGRADE_SIG" "$GBMC_UPGRADE_IMG"
}

gbmc_upgrade_fetch() (
  echo "Fetching $bootfile_url" >&2

  # We only support tarballs at the moment, our URLs will always denote
  # this with a URI query param of `format=TAR`.
  if ! [[ "$bootfile_url" =~ [\&?]format=TAR(&|$) ]]; then
    echo "Unknown upgrade unpack method: $bootfile_url" >&2
    return 1
  fi

  # Ensure some sane output file limit
  # Currently no BMC image is larger than 64M
  ulimit -H -f $((96 * 1024 * 1024)) || return
  timeout=$((SECONDS + 120))
  while (( SECONDS < timeout )); do
    local st=(0)
    wget -q -O - "$bootfile_url" | tar -xC "$tmpdir" || st=("${PIPESTATUS[@]}")
    (( st[0] != 0 )) || break
    (shopt -s nullglob dotglob; rm -rf -- "${tmpdir:?}"/*)
    sleep 5
  done

  local sig
  sig="$(find "$tmpdir" -name 'image-*.sig' | head -n 1)" || return
  local img="${sig%.sig}"
  mv "$sig" "$GBMC_UPGRADE_SIG" || return
  mv "$img" "$GBMC_UPGRADE_IMG" || return

  # Regular packages have a VERSION file with the image
  local imgdir="${sig%/*}"
  if [ -f "$imgdir/VERSION" ]; then
    cat "$imgdir/VERSION" || return
    return 0
  fi

  # Staging packages have a directory named after the version
  local vdir="${imgdir##*/}"
  if [[ "$vdir" =~ ([0-9]+[.]){3}[0-9]+ ]]; then
    echo "$vdir"
    return 0
  fi

  return 1
)

GBMC_BR_DHCP_HOOKS+=(gbmc_upgrade_hook)

gbmc_upgrade=1
