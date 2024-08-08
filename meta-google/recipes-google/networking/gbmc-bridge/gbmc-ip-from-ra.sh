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

# shellcheck source=meta-google/recipes-google/networking/network-sh/lib.sh
source /usr/share/network/lib.sh || exit

: "${RA_IF:?No RA interface set}"
: "${IP_OFFSET:?No IP offset set}"

old_pfx=
old_fqdn=

w=60
while true; do
  start=$SECONDS
  while read -r line; do
    # `script` terminates all lines with a CRLF, remove it
    line="${line:0:-1}"
    if [ -z "$line" ]; then
      hextet=
      pfx=
      host=
      domain=
      lifetime=-1
    elif [[ "$line" =~ ^Prefix' '*:' '*(.*)/([0-9]+)$ ]]; then
      t_pfx="${BASH_REMATCH[1]}"
      t_pfx_len="${BASH_REMATCH[2]}"
      ip_to_bytes t_pfx_b "$t_pfx" || continue
      (( (t_pfx_len == 76 || t_pfx_len == 80) && (t_pfx_b[8] & 0xfd) == 0xfd )) || continue
      (( t_pfx_b[9] &= 0xf0 ))
      (( t_pfx_b[9] |= IP_OFFSET ))
      hextet="fd$(printf '%02x' "${t_pfx_b[9]}")"
      pfx="$(ip_bytes_to_str t_pfx_b)"
    elif [[ "$line" =~ ^'Router lifetime'' '*:' '*([0-9]+)' '+ ]]; then
      lifetime="${BASH_REMATCH[1]}"
    elif [[ "$line" =~ ^'DNS search list'' '*:' '*([^.]+)(.*[.]google[.]com)' '*$ ]]; then
      # Ideally, we use PCRE and with lookahead and can do this in a single regex
      #   ^([a-zA-Z0-9-]+(?=-n[a-fA-F0-9]{1,4})|[a-zA-Z0-9-]+(?!-n[a-fA-F0-9]{1,4}))[^.]*[.]((?:[a-zA-Z0-9]*[.])*google[.]com)$
      # Instead we do multiple steps to extract the needed info
      host="${BASH_REMATCH[1]}"
      domain="${BASH_REMATCH[2]#.}"
      if [[ "$host" =~ (-n[a-fA-F0-9]{1,4})$ ]]; then
        host="${host%"${BASH_REMATCH[1]}"}"
      fi
    elif [[ "$line" =~ ^from' '(.*)$ ]]; then
      # We only want to accept info from gateway providing routers
      (( lifetime > 0 )) || continue
      rtr="${BASH_REMATCH[1]}"
      if [[ -n $pfx && $pfx != "$old_pfx" ]]; then
        echo "Updating PFX($pfx) from $rtr" >&2
        old_pfx="$pfx"
        update_pfx "$pfx" || true
      fi
      if [[ -n $host && -n $hextet && -n $domain ]]; then
        fqdn="$host-n$hextet.$domain"
        if [[ $fqdn != "$old_fqdn" ]]; then
          echo "Updating FQDN($fqdn) from $rtr" >&2
          old_fqdn="$fqdn"
          update_fqdn "$fqdn" || true
        fi
      fi
    fi
  done < <(exec script -q -c "rdisc6 -d -m $RA_IF -w $(( w * 1000 ))" /dev/null 2>/dev/null)
  # If rdisc6 exits early we still want to wait the full `w` time before
  # starting again.
  (( timeout = start + w - SECONDS ))
  sleep $(( timeout < 0 ? 0 : timeout ))
done
