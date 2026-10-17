#!/bin/bash
# Copyright 2026 Google LLC
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

# Solicits RAs on gbmcbr after one of its ports ($1) gained carrier, and logs
# which routers answered. gbmcbr itself stays up when a port loses its peer
# (another BMC or the host), so nothing else solicits when the peer comes
# back. networkd and gbmc-br-ra process the answers like any other RA.

port="${1:-gbmcbr}"
own="$(ip -6 -o addr show dev gbmcbr scope link | awk '{sub("/.*", "", $4); print $4}')"
# Let the peer's end of the link settle. A freshly enumerated USB link often
# joins the peer's bridge a moment after our carrier comes up, so retry a few
# times while nobody answers.
for try in 1 2 3; do
  sleep $(( 2 ** (try - 1) ))
  # rdisc6 only prints the sender ("from <ll>") on a terminal, see gbmc-ra.sh
  rtrs="$(script -q -c 'rdisc6 -m gbmcbr -r 1 -w 1000' /dev/null 2>/dev/null |
    tr -d '\r' | sed -n 's/^ *from //p' | sort -u | grep -v -F -x "${own:-::}" |
    tr '\n' ' ')"
  [[ -z $rtrs ]] || break
done
rtrs="${rtrs% }"
echo "gBMC Bridge port RS ($port): answered by ${rtrs:-nobody} (try $try)" >&2
