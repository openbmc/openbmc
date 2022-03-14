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

rules=""
trap 'rm -f -- "$rules"' TERM INT EXIT ERR
rules="$(mktemp)" || exit
echo 'flush ruleset' >"$rules"
for key in $(printf "%s\n" "${!basemap[@]}" | sort -r); do
  echo "Loading ${basemap[$key]}" >&2
  echo '' >>"$rules"
  cat "${basemap[$key]}" >>"$rules"
done
nft -f "$rules" || exit
