#!/bin/bash
shopt -s nullglob
declare -A basemap=()
i=0
for dir in /run/nftables /etc/nftables /usr/share/nftables; do
  for file in "$dir"/*.rules; do
    basemap["${file##*/}$i"]="$file"
  done
  let i+=1
done
rc=0
for key in $(printf "%s\n" "${!basemap[@]}" | sort -r); do
  echo "Executing ${basemap[$key]}" >&2
  nft -f "${basemap[$key]}" || rc=$?
done
exit $rc
