#!/bin/bash
# Copyright 2024 Google LLC
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

update_rtr() {
  default_update_rtr "$@"
}

update_fqdn() {
  true
}

update_pfx() {
  true
}

RA_IF=$1
IP_OFFSET=0
# This is guaranteed to be closer to the ToR than NCSI, for reliability
# and bandwidth we want to prefer this path.
ROUTE_METRIC=800

# shellcheck source=meta-google/recipes-google/networking/gbmc-net-common/gbmc-ra.sh
source /usr/share/gbmc-ra.sh || exit
