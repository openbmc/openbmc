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

# Every 30 seconds, send out an RA so that the kernel will receive a response.
# This ensures that all BMCs (even ones that think they are routers) get updated
# information from the other systems on the network.
w=30
while true; do
  start=$SECONDS
  rdisc6 -m gbmcbr -r 1 -w $(( w * 1000 )) >/dev/null 2>/dev/null
  # If rdisc6 exits early we still want to wait the full `w` time before
  # starting again.
  (( timeout = start + w - SECONDS ))
  sleep $(( timeout < 0 ? 0 : timeout ))
done
