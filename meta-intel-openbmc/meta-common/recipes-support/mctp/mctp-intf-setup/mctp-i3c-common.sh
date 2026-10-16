#!/bin/sh
# Shared i3c physbus/mmio/kernel-busidx helpers for mctp-cpu-discover.sh and
# mctp-cpu-reset-watch.sh. Meant to be sourced, not executed directly.
# ast2600 only; not portable to other SoCs.

# List index is the "physbus" number (the board's fixed controller
# numbering) -- can differ from the kernel's i3c-N enumeration order
# (see i3c_busidx_for_mmio).
I3C_AST2600_MMIOS="1e7a2000 1e7a3000 1e7a4000 1e7a5000 1e7a6000 1e7a7000"

i3c_mmio_to_physbus() {
    mmio="$1"
    i=0
    for addr in $I3C_AST2600_MMIOS; do
        [ "$addr" = "$mmio" ] && { echo "$i"; return 0; }
        i=$((i + 1))
    done
    return 1
}

i3c_physbus_to_mmio() {
    physbus="$1"
    i=0
    for addr in $I3C_AST2600_MMIOS; do
        [ "$i" = "$physbus" ] && { echo "$addr"; return 0; }
        i=$((i + 1))
    done
    return 1
}

# sysfs layout is <mmio>.i3c/i3c-<N> (bus index in a child dir), not <mmio>.i3cN
_i3c_devpath_to_mmio() {
    sed -n 's|.*/\([0-9a-f]\{8\}\)\.i3c/i3c-[0-9]\+$|\1|p'
}

i3c_busidx_to_mmio() {
    readlink /sys/bus/i3c/devices/i3c-"$1" 2>/dev/null | _i3c_devpath_to_mmio
}

i3c_busidx_for_mmio() {
    target="$1"
    mmio=""

    for dev in /sys/bus/i3c/devices/i3c-*; do
        [ -e "$dev" ] || continue
        mmio=$(readlink "$dev" | _i3c_devpath_to_mmio)
        [ "$mmio" = "$target" ] && { basename "$dev" | sed 's/^i3c-//'; return 0; }
    done
    return 1
}

# Unbind+rebind the i3c master driver for a physbus, forcing a fresh dynamic
# address assignment round when a target hasn't been enumerated yet.
i3c_reassign_addresses() {
    physbus="$1"
    mmio=""
    drv=ast2600-i3c-master

    mmio=$(i3c_physbus_to_mmio "$physbus") || return 1

    echo "${mmio}.i3c" > "/sys/bus/platform/drivers/${drv}/unbind" 2>/dev/null
    echo "${mmio}.i3c" > "/sys/bus/platform/drivers/${drv}/bind" 2>/dev/null
}
