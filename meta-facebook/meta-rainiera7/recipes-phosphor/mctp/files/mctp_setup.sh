#!/bin/bash
# shellcheck source=meta-facebook/meta-yosemite5/recipes-yosemite5/plat-tool/files/yosemite5-common-functions
source /usr/libexec/yosemite5-common-functions

dev="${1:-}"
mode="${2:-setup}"

get_mctp_iface_assigned_eid() {
    local iface="$1"
    mctp route show | \
        awk -v dev="$iface" '$0 ~ "dev "dev && $3 != "8" {print $3}' | head -n 1
}

is_eid_assigned() {
    busctl tree au.com.codeconstruct.MCTP1 | grep -q "/endpoints/$1"
}

get_cxl_mctp_iface() {
    local cxl_fru_names=("Maple_Falls" "Victoria_Falls")
    local version

    for name in "${cxl_fru_names[@]}"; do
        version=$(get_product_version "$name")

        if [ "$version" != "Unknown" ] && [ -n "$version" ]; then
            # start from DVT3, CXL cable moves from MCIO4A to MCIO3A
            case "$version" in
                EVT|DVT|DVT1|DVT2)
                    echo "mctpi2c12"
                    ;;
                *)
                    echo "mctpi2c15"
                    ;;
            esac

            return 0
        fi
    done

    echo ""
    return 1
}

setup_endpoint() {
    local devname="$1"
    local iface="$2"
    local physaddr="$3"
    local eid="$4"
    local cur_eid

    if [[ -z "$iface" || -z "$physaddr" || -z "$eid" ]]; then
        echo "Setup $devname: Failed (missing required parameters)" >&2
        return 1
    fi

    cur_eid=$(get_mctp_iface_assigned_eid "$iface")
    if [[ -n "$cur_eid" ]]; then
        echo "Setup $devname on $iface: Skipped (already assigned EID=$cur_eid)"
        return 0
    fi

    if is_eid_assigned "$eid"; then
        echo "Setup $devname on $iface: Failed (EID $eid in use)" >&2
        return 1
    fi

    if busctl call "au.com.codeconstruct.MCTP1" \
        "/au/com/codeconstruct/mctp1/interfaces/$iface" \
        "au.com.codeconstruct.MCTP.BusOwner1" \
        AssignEndpointStatic ayy 1 "$physaddr" "$eid"; then
        echo "Setup $devname on $iface (EID=$eid, Addr=$physaddr): Success"
        return 0
    else
        echo "Setup $devname on $iface (EID=$eid, Addr=$physaddr): Failed" >&2
        return 1
    fi
}

remove_endpoint() {
    local devname="$1"
    local iface="$2"
    local eid

    eid=$(get_mctp_iface_assigned_eid "$iface")

    if [[ -z "$eid" ]]; then
        echo "Remove $devname on $iface: no EID found" >&2
        return 1
    fi

    echo "Removing $devname on $iface (EID=$eid)..."

    if busctl call au.com.codeconstruct.MCTP1 \
        "/au/com/codeconstruct/mctp1/networks/1/endpoints/$eid" \
        au.com.codeconstruct.MCTP.Endpoint1 Remove ; then
        echo "Remove $devname on $iface: Success"
        return 0
    else
        echo "Remove $devname on $iface: Failed" >&2
        return 1
    fi
}

CXL_IFACE=$(get_cxl_mctp_iface)
if [[ $? -ne 0 || -z "$CXL_IFACE" ]]; then
    echo "get_cxl_mctp_iface failed or returned empty. Using default iface"
    CXL_IFACE="mctpi2c12"
fi

# Mapping table: "devname:iface:physaddr:eid"
declare -a endpoint_map=(
    "nic_mctp:mctpi2c4:0x32:10"
    "cxl_mctp:${CXL_IFACE}:0x32:20"
)

for entry in "${endpoint_map[@]}"; do
    IFS=":" read -r devname iface physaddr eid <<< "$entry"

    if [[ -n "$dev" && "$devname" != "$dev" ]]; then
        continue
    fi

    if [[ "$mode" == "remove" ]]; then
        remove_endpoint "$devname" "$iface"
    else
        setup_endpoint "$devname" "$iface" "$physaddr" "$eid"
    fi

    if [[ -n "$dev" ]]; then
        exit 0
    fi
done
