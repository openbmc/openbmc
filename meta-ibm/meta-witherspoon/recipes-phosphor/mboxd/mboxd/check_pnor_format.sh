#!/bin/sh

# Get the mtd device number (mtdX)
findmtd() {
  m="$(grep -xl "$1" /sys/class/mtd/*/name)"
  m="${m%/name}"
  m="${m##*/}"
  echo "${m}"
}

pnormtd="$(findmtd pnor)"
pnor="${pnormtd#mtd}"
pnordev="/dev/mtd${pnor}"

if [[ ! "$(dd if=${pnordev} bs=1 count=3 2> /dev/null)" = "UBI" ]]; then
  echo "${pnordev} is not formatted UBI"
  exit 1
fi
