#!/bin/bash

# shellcheck source=meta-ampere/meta-common/recipes-ampere/platform/ampere-utils/ampere_redfish_utils.sh
source /usr/sbin/ampere_redfish_utils.sh

socket_id=$1
direction=$2

if [ "$direction" == "asserted" ]
then
    add_ampere_warning_sel "The S$socket_id High temperature GPIO" "CPU$socket_id HighTemp asserted"
elif [ "$direction" == "deasserted" ]
then
    add_ampere_info_sel "The S$socket_id High temperature GPIO" "CPU$socket_id HighTemp deasserted"
fi

