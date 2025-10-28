#!/bin/bash

declare -a ampere_redfish_events=("ReportedSELInfo" "ReportedSELWarning" "ReportedSELCritical")

function add_ampere_redfish_log()
{
    if [ $# -lt 3 ] || [ $# -ge 5 ]
    then
        echo "Number of arguments is incorrect"
        return
    fi

    event_type=$1
    source=$2
    message=$3
    raw_data=""
    valid_event_flag=1

    for element in "${ampere_redfish_events[@]}"
    do
        if [[ "$event_type" == "$element" ]]
        then
            valid_event_flag=0
            break
        fi
    done

    if [ $valid_event_flag -eq 1 ]
    then
        echo "Invalid event type: ${event_type}"
        return
    fi

    if [ $# -eq 4 ]
    then
        raw_data=$4
    fi

    log-create com.ampere.Event.ReportedSEL."$event_type" -j \
        "{\"SOURCE\": \"$source\", \"MESSAGE\": \"$message\", \"RAW_DATA\": \"$raw_data\"}"
}

function add_ampere_info_sel()
{
    add_ampere_redfish_log "ReportedSELInfo" "$@"
}

function add_ampere_warning_sel()
{
    add_ampere_redfish_log "ReportedSELWarning" "$@"
}

function add_ampere_critical_sel()
{
    add_ampere_redfish_log "ReportedSELCritical" "$@"
}
