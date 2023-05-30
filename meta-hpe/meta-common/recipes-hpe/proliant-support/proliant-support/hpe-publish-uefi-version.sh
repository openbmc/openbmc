#!/bin/sh

#
# Copyright (c) 2021 Hewlett-Packard Development Company, L.P.
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
#

# RL300 doesn't support VROM, so we will be reading directly from
# SPI-NOR. The whole process is a little bit slow but works

# Find an MTD /dev file by name
findmtd() {
  echo "parameter $1"
  m=$(grep -xl "$1" /sys/class/mtd/*/name)
  m=${m%/name}
  m=${m##*/}
  echo "$m"
}

rom=uefi-master
echo "Checking for mtd partition ${rom}"
image=$(findmtd ${rom})
if test -z "$image"
then
  echo "Unable to find mtd partition for ${rom}"
  exit 1
fi
rom_mtd=${image}

uefi_version="hpe-uefi-version /dev/${rom_mtd}"
busctl set-property xyz.openbmc_project.Software.BMC.Updater /xyz/openbmc_project/software/bios_active xyz.openbmc_project.Software.Version Version s "$uefi_version"
busctl get-property xyz.openbmc_project.Software.BMC.Updater /xyz/openbmc_project/software/bios_active xyz.openbmc_project.Software.Version Version
