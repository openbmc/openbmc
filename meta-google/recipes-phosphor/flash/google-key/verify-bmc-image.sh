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

help_out() {
  echo "$ARG0 [--allow-dev] <image file> <sig file>" >&2
  exit 2
}

opts="$(getopt -o 'd' -l 'allow-dev' -- "$@")" || exit
dev=
eval set -- "$opts"
while true; do
  case "$1" in
    --allow-dev|-d)
      dev=1
      shift
      ;;
    --)
      shift
      break
      ;;
    *)
      echo "Bad option: $1" >&2
      help_out
      ;;
  esac
done
image_file="${1?Missing image file}" || help_out
sig_file="${2?Missing sig file}" || help_out

# gnupg needs a home directory even though we don't want to persist any
# information. We always make a new temporary directory for this
GNUPGHOME=
cleanup() {
  test -n "$GNUPGHOME" && rm -rf "$GNUPGHOME"
}
trap cleanup ERR EXIT INT
export GNUPGHOME="$(mktemp -d)" || exit

gpg() {
  command gpg --batch --allow-non-selfsigned-uid --no-tty "$@"
}
import_key() {
  gpg --import "/usr/share/google-key/$1.key"
}

import_key prod
if [ -n "$dev" ]; then
  import_key dev
fi
gpg --verify --ignore-time-conflict "$sig_file" "$image_file"
