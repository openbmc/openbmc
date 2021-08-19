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


# This script will check the signature for the BMC image against
# the baked in keyring available.  If any aspect of this fails,
# the scripts returns non-zero and this can be reported to the
# host.
#
# 1. Verify the image
# 2. Rename the image

KEYRING=/etc/googlekeys/gbmc/gbmc.gpg
SIGNATURE_FILE=/tmp/bmc.sig
STATUS_FILE=/tmp/bmc.verify

# Store in /run/initramfs because the behaviour of mv changes
# depending on whether the file is moving within a tree or not.
IMAGE_FILE=/run/initramfs/bmc-image
VERIFIED_FILE=/run/initramfs/image-bmc

# Make sure we run ERR traps when a function returns an error
set -e

# Write out the result of the script to a status file upon exiting
# normally or due to an error
exit_handler() {
  local status="$?"
  if (( status == 0 )); then
    echo "success" >"${STATUS_FILE}"
  else
    echo "failed" >"${STATUS_FILE}"
  fi
  trap - EXIT ERR
  exit "$status"
}
trap exit_handler EXIT ERR

echo "running" > ${STATUS_FILE}

# Verify the image.
verify-bmc-image.sh @ALLOW_DEV@ "$IMAGE_FILE" "$SIGNATURE_FILE" || exit

# Rename the staged file for initramfs updates.
mv ${IMAGE_FILE} ${VERIFIED_FILE}
