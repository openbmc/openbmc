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

[ -z "${inplace_gbmc_upgrade-}" ] || exit

# SC doesn't know another file depends on this variable
# shellcheck disable=SC2034
GBMC_UPGRADE_IMG=/run/initramfs/bmc-image

gbmc_upgrade_internal() {
  local version
  if [[ -n "${GBMC_UPGRADE_METADATA}" ]]; then
    version="$(gbmc_upgrade_get_version)" || return
  else
    version="$(gbmc_upgrade_fetch)" || return
  fi

  update_netboot_status "bmc_flash" "Target firmware version is  $version" "START"
  local active_version
  active_version="$(inplace-gbmc-version.sh)" || return
  update_netboot_status "bmc_flash" "Running firmware version $active_version" "ONGOING"
  if [[ "$version" == "$active_version" ]]; then
    update_netboot_status "bmc_flash" "Version ${version} is already active" "SUCCESS"
    return 0
  fi

  if [[ -n "${GBMC_UPGRADE_METADATA}" ]]; then
    gbmc_upgrade_download_image_and_sig "$version"
  fi
  update_netboot_status "bmc_flash" "Verifying image $version" "ONGOING"
  systemctl start inplace-gbmc-verify || return
  update_netboot_status "bmc_flash" "Rebooting to perform update to $version" "ONGOING"
  reboot || return
  # Ensure that we don't "complete" the netboot process until
  # after the update completes
  exit 0
}

inplace_gbmc_upgrade=1
