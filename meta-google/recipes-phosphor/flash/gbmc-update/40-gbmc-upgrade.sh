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
  [ -n "${bootfile_url-}" ] || return 0

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
  local tflags=()
  if [[ "$bootfile_url" =~ [\&?]format=TAR(_GZIP)?(&|$) ]]; then
    local t="${BASH_REMATCH[1]}"
    [ "$t" = '_GZIP' ] && tflags+=('-z')
  else
    echo "Unknown upgrade unpack method: $bootfile_url" >&2
    return 1
  fi

  # Determine the path of the image file for the correct machine
  # Our netboot can serve us images for multiple models
  local machine
  machine="$(source /etc/os-release && echo "$OPENBMC_TARGET_MACHINE")" || return

  # Ensure some sane output file limit
  # Currently no BMC image is larger than 64M
  # We want to allow 2 images and a small amount of metadata (2*64+2)M
  local max_mb=$((2*64 + 2))
  ulimit -f $((max_mb * 1024 * 1024 / 512)) || return
  timeout=$((SECONDS + 300))
  stime=5
  while true; do
    local st=()
    curl -LSsk --max-time $((timeout - SECONDS)) "$bootfile_url" |
      tar "${tflags[@]}" --wildcards -xC "$tmpdir" "*/firmware-gbmc/$machine" \
      && st=("${PIPESTATUS[@]}") || st=("${PIPESTATUS[@]}")
    # Curl failures should continue
    if (( st[0] == 0 )); then
      # Tar failures when curl succeeds are hard errors to start over.
      if (( st[1] != 0 )); then
        echo 'Unpacking failed' >&2
        return 1
      fi
      # Success should continue without retry
      break
    fi
    if (( SECONDS + stime >= timeout )); then
      echo 'Timed out fetching image' >&2
      return 1
    fi
    (shopt -s nullglob dotglob; rm -rf -- "${tmpdir:?}"/*)
    sleep $stime
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
