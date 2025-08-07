#!/bin/sh

echo "${1}" > "/run/psu_timedelay"
systemctl start gbmc-psu-hardreset.target --no-block
