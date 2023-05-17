#!/bin/sh

findmtd() {
  m=$(grep -xl "$1" /sys/class/mtd/*/name)
  m=${m%/name}
  m=${m##*/}
  echo "$m"
}

set -- host-prime host-second vrom-prime vrom-second

for f in "$@"
do
  image=$(findmtd "${f}")
  if test -z "$image"
  then
    echo "Unable to find mtd partition for ${f}"
    exit 1
  fi
done

#enable vrom
# host-prime to vrom-prime
dd if="/dev/$(findmtd host-prime)" of="/dev/$(findmtd vrom-prime)"
# host-second to vrom-second
dd if="/dev/$(findmtd host-second)" of="/dev/$(findmtd vrom-second)"

echo 0x1800008a > /sys/class/soc/srom/vromoff

while true
do
        devmem 0x8000005C 8 0
        devmem 0xd1000306 8 5
        devmem 0xd1000318 8 0x03
        devmem 0xd100030f 8 0x04
        sleep 1
done

 