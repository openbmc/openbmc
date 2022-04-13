#!/bin/bash
# Copyright 2022 Google LLC
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

# obmc-console-client will immediately exit if we don't give it an stdin
# that it can block on forever, so we hook it up to /dev/null since that never
# feeds it data.
# We only allow printable characters we know to be good. This set is
# currently newline + all printable ASCII chars (space through hyphen).
obmc-console-client < <(tail -f /dev/null) | tr -cd '\12\40-\176'
