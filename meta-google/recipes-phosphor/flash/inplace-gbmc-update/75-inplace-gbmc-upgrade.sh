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
  version="$(gbmc_upgrade_fetch)" || return

  echo "IMG Version: $version" >&2
  local active_version
  active_version="$(inplace-gbmc-version.sh)" || return
  echo "Active Version: $active_version" >&2
  if [[ "$version" == "$active_version" ]]; then
    echo 'Version already active' >&2
    return 0
  fi

  echo 'Verifying image' >&2
  systemctl start inplace-gbmc-verify || return
  echo 'Rebooting to perform update' >&2
  reboot || return
  # Ensure that we don't "complete" the netboot process until
  # after the update completes
  exit 0
}

inplace_gbmc_upgrade=1
