#!/bin/bash

die() { logger -s -t bios-update "Error: $*"; exit 1; }
info() { logger -s -t bios-update "$*"; }

# shellcheck disable=SC1091
. /etc/default/bios-update || die "Failed: unable to load /etc/default/bios-update"

[ -n "$BIOS_UPDATE_MAGIC_OFFSET" ] || die "BIOS_UPDATE_MAGIC_OFFSET not set"
[ -n "$BIOS_UPDATE_MAGIC" ] || die "BIOS_UPDATE_MAGIC not set"
[ -n "$BIOS_UPDATE_SIZE" ] || die "BIOS_UPDATE_SIZE not set"

declare -A prep_gpios_pids

bios_flash_spidev="1e630000.spi"
smc_drvdir="/sys/bus/platform/drivers/spi-aspeed-smc"

hoststate_svc="xyz.openbmc_project.State.Host"
hoststate_path="/xyz/openbmc_project/state/host0"
hoststate_intf="xyz.openbmc_project.State.Host"
hoststate_prop="CurrentHostState"
hoststate_off="xyz.openbmc_project.State.Host.HostState.Off"

check_host_off()
{
	local state
	state="$(busctl get-property "$hoststate_svc" "$hoststate_path" \
			"$hoststate_intf" "$hoststate_prop")"
	if [ "$state" != "s \"$hoststate_off\"" ]; then
		die "host must be off before performing BIOS update"
	fi
}

# sets variables (gpioset background PIDs and bios flash mtd chardev,
# commented as "global") for later use
attach_bios_flash()
{
	for gpio in "${BIOS_UPDATE_PREP_GPIOS[@]}" ; do
		read -ra kv <<<"${gpio/=/ }"
		info "Setting ${kv[0]} to ${kv[1]}..."
		gpio="$(gpiofind "${kv[0]}")" || die "Failed to find ${kv[0]} GPIO"
		# shellcheck disable=SC2086
		gpioset -m signal ${gpio}="${kv[1]}" &
		prep_gpios_pids[${kv[0]}]=$! # global
		sleep 1
	done

	info "Attaching BIOS flash..."
	echo "$bios_flash_spidev" > "$smc_drvdir/bind" || die "failed to attach aspeed-smc driver to BIOS SPI flash"

	local tmp
	tmp="$(grep -xl bios /sys/class/mtd/*/name)"
	tmp="${tmp%/name}"
	tmp="${tmp##*/}"
	bios_mtd_dev="/dev/$tmp" # global
	[ -c "$bios_mtd_dev" ] || die "bios mtd chardev not found"
}

detach_bios_flash()
{
	info "Detaching BIOS flash..."
	echo "$bios_flash_spidev" > "$smc_drvdir/unbind" || die "failed to detach aspeed-smc driver from BIOS SPI flash"

	# Detach in reverse order
	for ((i = ${#BIOS_UPDATE_PREP_GPIOS[@]} - 1; i >= 0; i--)) ; do
		read -ra kv <<<"${BIOS_UPDATE_PREP_GPIOS[i]/=/ }"
		notvalue=$((! kv[1]))
		info "Resetting ${kv[0]} to ${notvalue}..."
		kill -INT "${prep_gpios_pids[${kv[0]}]}"
		wait "${prep_gpios_pids[${kv[0]}]}"
		gpio="$(gpiofind "${kv[0]}")" || die "Failed to find ${kv[0]} GPIO"
		# shellcheck disable=SC2086
		gpioset -m exit ${gpio}="$notvalue"
		sleep 1
	done
}

check_bios_image()
{
	[ -r "$1" ] || die "can't read BIOS image $1"

	local imgsize magic
	imgsize="$(stat -c %s "$1")"
	[ "$imgsize" = "${BIOS_UPDATE_SIZE}" ] || die "invalid BIOS image (wrong size)"

	magic="$(dd if="$1" bs=1 count=4 skip="${BIOS_UPDATE_MAGIC_OFFSET}" 2>/dev/null | hexdump -e '3/1 "%02x" "%02x\n"')"
	[ "$magic" = "${BIOS_UPDATE_MAGIC}" ] || die "invalid BIOS image (magic number mismatch)"
}

flash_bios_image()
{
	local bios_img="$1"

	info "Checking BIOS image..."
	check_bios_image "$bios_img"

	info "Checking host state..."
	check_host_off

	attach_bios_flash

	info "Writing BIOS image to SPI flash..."
	if flashcp -v "$bios_img" "$bios_mtd_dev"; then
		info "Flash update successful"
		local status=0
	else
		info "Error updating flash! (proceeding with detach)"
		local status=1
	fi

	detach_bios_flash

	return "$status"
}

# HACK: for unknown reasons, on e3c246d4i, the host seems to refuse to power on
# after we switch the BIOS SPI flash to the BMC and back to the host,
# but it recovers if we hold the POWER_OUT GPIO as in a press-and-hold
# of the front-panel power button (even though the host is already
# powered off).  I don't really know what's going on here.
do_power_hack()
{
	# power-control holds the POWER_OUT gpio, so we need to stop it if it's on
	local powerctl_svc="xyz.openbmc_project.Chassis.Control.Power.service"
	local powerhack_time=8
	local psout_gpio

	psout_gpio="$(gpiofind "${BIOS_UPDATE_POWER_GPIO}")"

	prev_powerctl_state="$(systemctl show --property=ActiveState "$powerctl_svc")"
	if [ "$prev_powerctl_state" = "ActiveState=active" ]; then
		systemctl stop "$powerctl_svc" || info "Warning: failed to stop $powerctl_svc"
	fi

	info "Holding host power line for $powerhack_time seconds..."

	# shellcheck disable=SC2086
	gpioset -m time -s "$powerhack_time" ${psout_gpio}=0 || die "Failed to assert ${BIOS_UPDATE_POWER_GPIO}) GPIO"
	# shellcheck disable=SC2086
	gpioset ${psout_gpio}=1 || die "Failed to release ${BIOS_UPDATE_POWER_GPIO} GPIO"

	info "Host power line released..."

	# if the power-control service was for some reason not running to
	# start with, leave it that way.
	if [ "$prev_powerctl_state" = "ActiveState=active" ]; then
		systemctl start "$powerctl_svc" || info "Warning: failed to restore $powerctl_svc"
	fi
}

# Find the image file within a /tmp/images/$IMGHASH directory (should
# be the one file not named MANIFEST).  We could be a little more
# automagic and run check_bios_image on each candidate in case there's
# more than one (discarding any that fail), but for now we'll keep it
# simple and not try to handle anything unexpected.
find_imgfile()
{
	[ -d "$1" ] || die "$1: not a directory"
	local img='' path name
	for path in "$1"/*; do
		name="$(basename "$path")"
		if [ "$name" = "MANIFEST" ]; then
			# ignore MANIFEST file
			continue
		elif [ -n "$img" ]; then
			# if we've already hit a non-MANIFEST file, bail
			die "multiple potential image files in $1"
		else
			img="$path"
		fi
	done
	[ -n "$img" ] || die "no image file found in $1"
	echo "$img"
}

# when invoked by the systemd unit as part of the web-UI machinery we
# get passed /tmp/images/$IMGHASH (directory containing the BIOS
# image), but for manual use it's nice to be able to just pass the raw
# image file directly, so we support both, differentiated by a '-d'
# flag.
imgdir_mode=false

while getopts d opt; do
	case "$opt" in
	d) imgdir_mode=true;;
	*) exit 1;;
	esac
done

shift $((OPTIND-1))
[ $# = 1 ] || die "usage: $0 [ BIOS_IMAGE | -d IMAGE_DIR ]"

if $imgdir_mode; then
	imgfile="$(find_imgfile "$1")"
else
	imgfile="$1"
fi

if flash_bios_image "$imgfile"; then
	info "BIOS update complete."
else
	die "BIOS update failed!"
fi

if [ -n "$BIOS_UPDATE_POWER_GPIO" ]; then
	do_power_hack
fi

info "Done."
