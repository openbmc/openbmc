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

GBMC_UPGRADE_UNPACK_FILES=()
# shellcheck disable=SC2034
GBMC_UPGRADE_HOOKS=(gbmc_upgrade_internal)

# metadata stored in an array
GBMC_UPGRADE_METADATA=()

if machine="$(source /etc/os-release && echo "$GBMC_TARGET_MACHINE")"; then
  GBMC_UPGRADE_UNPACK_FILES+=("*/firmware-gbmc/$machine")
else
  echo 'Failed to find GBMC machine type from /etc/os-release' >&2
fi

gbmc_upgrade_get_version(){
  local version_file
  if ! version_file=$(gbmc_upgrade_metadata_first_match "^.*/firmware-gbmc/${machine}(/[^/]+)*/VERSION$"); then
    update_netboot_status "upgrade" "Couldn't find version file, trying regex" "ONGOING"
    # we expect this regex to match something like: packages/firmware-gbmc/MACHINE/some-services/22.47.18.0/image
    if gbmc_upgrade_metadata_first_match "^.*/firmware-gbmc/${machine}(/[^/]+)*/(([0-9]+[.]){3}[0-9]+)/[^/]+$" > /dev/null; then
      echo "${BASH_REMATCH[2]}"
      update_netboot_status "upgrade" "Obtained version from metadata filenames." "ONGOING"
      return 0
    else
      update_netboot_status "upgrade" "Failed to get version from metadata filenames" "FAIL"
      return 1
    fi
  fi
  if ! gbmc_upgrade_download "&selection=${version_file}" "$tmpdir/version_file" "ver"; then
    return 1
  fi
  cat "$tmpdir/version_file"
  rm -f "$tmpdir/version_file"
  return 0
}

gbmc_upgrade_download_image_and_sig(){
  local image
  local version=$1
  if ! image=$(gbmc_upgrade_metadata_first_match "^.*/firmware-gbmc/${machine}(/[^/]+)*/${version}/image-gbmc-${machine}$"); then
     update_netboot_status "upgrade" "Could not find image" "FAIL"
     return 1
  fi
  local sig="$image.sig"
  # shellcheck disable=SC2153
  if ! gbmc_upgrade_download "&selection=$image" "${GBMC_UPGRADE_IMG}" "bmc_img"; then
    return 1
  fi

  if ! gbmc_upgrade_download "&selection=$sig" "${GBMC_UPGRADE_SIG}" "bmc_sig"; then
    return 1
  fi
}

gbmc_upgrade_metadata_first_match() {
  local regex="$1"

  for item in "${GBMC_UPGRADE_METADATA[@]}"; do
    if [[ "$item" =~ $regex ]]; then
      echo "$item" # Return the whole line match
      return 0 # Exit the function after the first match
    fi
  done
  return 1 # Return 1 if no match is found
}

gbmc_upgrade_download() {
  local retry=0
  local path="$1"
  local output="$2"
  local state="$3_fetch"
  local deadline="${4-600}"
  # give a chance to retry the curl if it stuck until the maximum timeout
  local single_deadline=$(( deadline / 3 ))
  local stime=5
  local timeout=$((SECONDS + deadline))
  update_netboot_status "$state" "Fetching URI: ${bootfile_url}${path}" "START" "$retry"
  while true; do
    local st=()
    if [[ -z "$path" ]]; then
      curl -LSsk --max-time "${single_deadline}" "$bootfile_url" |
        tar "${tflags[@]}" --wildcards --warning=none -xC "$tmpdir" "${GBMC_UPGRADE_UNPACK_FILES[@]}" 2>"$tmpdir"/tarerr \
        && st=("${PIPESTATUS[@]}") || st=("${PIPESTATUS[@]}")
      # Curl failures should continue
      if (( st[0] == 0 )); then
        # Tar failures when curl succeeds are hard errors to start over.
        # shellcheck disable=SC2143
        if (( st[1] != 0 )) && [[ -n $(grep -v '\(Exiting with failure status\|Not found in archive\|Cannot hard link\)' "$tmpdir"/tarerr) ]]; then
          update_netboot_status "fetch_tar" "couldn't get TAR file, netboot fail" "FAIL"
          return 1
        fi
        # Success should continue without retry
        break
      fi
    else
      if curl -LSsk --max-time "${single_deadline}" "${bootfile_url}${path}" -o "${output}"; then
        # Success should continue without retry
        update_netboot_status "$state" "Succesfully fetched" "SUCCESS" "$retry"
        break
      fi
    fi
    if (( SECONDS + stime >= timeout )); then
      update_netboot_status "$state" "Failed to fetch" "FETCH_FAILED" "$retry"
      return 1
    fi
    sleep $stime
    update_netboot_status "$state" "Failed to fetch $state, retrying" "RETRYING" "$retry"
    (( retry = retry + 1 ))
  done
  return 0
}

gbmc_upgrade_dl_metadata() {
  #download metadata file
  if ! gbmc_upgrade_download "&metadata=true" "$tmpdir/metadata_file" "meta" 90; then
    update_netboot_status "netboot" "couldn't get metadata file,  attempting to use v1 install flow" "ONGOING"
    return 1
  fi
  # shellcheck disable=SC2034
  mapfile -t GBMC_UPGRADE_METADATA < <(sort "$tmpdir/metadata_file")
  rm -f "$tmpdir/metadata_file"
  return 0
}

gbmc_upgrade_dl_unpack() {
  update_netboot_status "upgrade" "Fetching $bootfile_url" "ONGOING"
  # We only support tarballs at the moment, our URLs will always denote
  # this with a URI query param of `format=TAR`.
  local tflags=()
  if [[ "$bootfile_url" =~ [\&?]format=TAR(_GZIP)?(&|$) ]]; then
    local t="${BASH_REMATCH[1]}"
    [ "$t" = '_GZIP' ] && tflags+=('-z')
  else
    update_netboot_status "upgrade" "Unknown upgrade unpack method: $bootfile_url" "FAIL"
    return 1
  fi

 if ! gbmc_upgrade_download "" "$tmpdir" "tar"; then
    return 1
  fi
  return 0
}

gbmc_upgrade_hook() {
  if [ -z "${bootfile_url-}" ]; then
    update_netboot_status "netboot" "Boot file URL is empty, failure" "FAIL"
    return 1
  fi
  local tmpdir
  local max_mb=$((2*64 + 2))
  update_netboot_status "upgrade" "Upgrade started" "START"
  ulimit -f $((max_mb * 1024 * 1024 / 512)) || return
  tmpdir="$(mktemp -d)" || return
  if ! gbmc_upgrade_dl_metadata && ! gbmc_upgrade_dl_unpack; then
    # shellcheck disable=SC2153
    rm -rf -- "$tmpdir" "$GBMC_UPGRADE_SIG" "$GBMC_UPGRADE_IMG"
    update_netboot_status "upgrade" "Upgrade fail" "FAIL"
    return 1
  fi
  # shellcheck disable=SC2015
  gbmc_br_run_hooks GBMC_UPGRADE_HOOKS || true
  # shellcheck disable=SC2153
  rm -rf -- "$tmpdir" "$GBMC_UPGRADE_SIG" "$GBMC_UPGRADE_IMG"
  update_netboot_status "upgrade" "Upgrade complete" "SUCCESS"
}

gbmc_upgrade_fetch() (
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
  update_netboot_status "upgrade" "Failed to get version from metadata filenames" "FAIL"
  return 1
)

GBMC_BR_DHCP_HOOKS+=(gbmc_upgrade_hook)

gbmc_upgrade=1
