#!/bin/sh -eu

BMCPOS=$(awk -v RS=" " '/^bmcposition=/{print substr($0,13)}' /proc/cmdline)
sed -r "s/\"id\"\s*:\s*[0-9]+\s*,/\"id\": ${BMCPOS:-0},/" -i \
    /usr/share/ipmi-providers/dev_id.json
