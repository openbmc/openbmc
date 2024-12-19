#!/bin/bash
# Initialize variables
boot_stage=00
boot_status=00
uefi_code=00000000

function set_postcode()
{
	# shellcheck disable=SC2086
	busctl set-property xyz.openbmc_project.State.Boot.Raw \
		/xyz/openbmc_project/state/boot/raw0 \
		xyz.openbmc_project.State.Boot.Raw Value \(tay\) "$1" 0
}

function update_boot_progress_last_state_time()
{
	# Get BMC current time
	bp_last_state_time=$(busctl get-property xyz.openbmc_project.Time.Manager \
		/xyz/openbmc_project/time/bmc \
		xyz.openbmc_project.Time.EpochTime \
		Elapsed | cut -d' ' -f2)

	# Update the Boot Progress LastStateTime
	busctl set-property xyz.openbmc_project.State.Host \
		/xyz/openbmc_project/state/host0 \
		xyz.openbmc_project.State.Boot.Progress \
		BootProgressLastUpdate t \
		"$bp_last_state_time"
}

function update_boot_progress()
{
	bootprog=$1

	busctl set-property xyz.openbmc_project.State.Host \
		/xyz/openbmc_project/state/host0 \
		xyz.openbmc_project.State.Boot.Progress \
		BootProgress s \
		"xyz.openbmc_project.State.Boot.Progress.ProgressStages.$bootprog"

	# Update Boot Progress LastStateTime
	update_boot_progress_last_state_time
}

function get_boot_stage_string()
{
	bootstage=$1
	ueficode=$2

	case $bootstage in

		00)
			boot_stage_str="SMpro"
			;;

		01)
			boot_stage_str="PMpro"
			;;

		02)
			boot_stage_str="ATF BL1 (Code=${ueficode})"
			;;

		03)
			boot_stage_str="DDR initialization (Code=${ueficode})"
			;;

		04)
			boot_stage_str="DDR training progress (Code=${ueficode})"
			;;

		05)
			boot_stage_str="ATF BL2 (Code=${ueficode})"
			;;

		06)
			boot_stage_str="ATF BL31 (Code=${ueficode})"
			;;

		07)
			boot_stage_str="ATF BL32 (Code=${ueficode})"
			;;

		08)
			boot_stage_str="UEFI booting (UEFI Code=${ueficode})"
			;;
		09)
			boot_stage_str="OS booting"
			;;

	esac

	echo "$boot_stage_str"
}

function set_boot_progress()
{
	boot_stage=$1
	uefi_code=$2

	case $boot_stage in

		02)
			update_boot_progress "PrimaryProcInit"
			;;

		03)
			update_boot_progress "MemoryInit"
			;;

		08)
			if [[ "$uefi_code" =~ 0201* ]]; then
				update_boot_progress "PCIInit"
			fi
			;;
		09)
			update_boot_progress "OSStart"
			;;

	esac
}

function log_redfish_biosboot_ok_event()
{
	logger --journald << EOF
MESSAGE=
PRIORITY=2
SEVERITY=
REDFISH_MESSAGE_ID=OpenBMC.0.1.BIOSBoot
REDFISH_MESSAGE_ARGS="UEFI firmware booting done"
EOF
}

function log_redfish_bios_panic_event()
{
	boot_state_str=$(get_boot_stage_string "$1" "$2")

	logger --journald << EOF
MESSAGE=
PRIORITY=2
SEVERITY=
REDFISH_MESSAGE_ID=OpenBMC.0.1.BIOSFirmwarePanicReason
REDFISH_MESSAGE_ARGS=${boot_state_str}
EOF
}

cnt=0
# If any reason makes SCP fail to access in 6s, break the service.
while [ $cnt -lt 30 ];
do
	# Sleep 200ms
	sleep 1s
	if ! read -r bg <<< "$(cat /sys/bus/platform/devices/smpro-misc.2.auto/boot_progress)";
	then
		cnt=$((cnt + 1))
		continue
	fi
	cnt=0

	# Check if any update from previous check
	if [ "$last_bg" == "$bg" ]; then
		continue
	fi
	last_bg=$bg

	# Check if the Host is already ON or not. If Host is already boot, update boot progress and break.
	if [ "${boot_stage}" == "00" ] && [ "${bg[0]}" == "09" ];
	then
		update_boot_progress "OSRunning"
		break
	fi

	# Update current boot progress
	boot_stage=${bg:2:2}
	boot_status=${bg:0:2}
	uefi_code=${bg:4}
	echo "Boot Progress = ${boot_stage} ${boot_status} ${uefi_code}"

	# Log Boot Progress to dbus
	if [ "${boot_status}" == "03" ]; then
		# Log Redfish Event if failure.
		log_redfish_bios_panic_event "$boot_stage" "$uefi_code"
		# Dimm training failed, check errors
		if [ "${boot_stage}" == "04" ]; then
			/usr/sbin/dimm_train_fail_log.sh 0
			/usr/sbin/dimm_train_fail_log.sh 1
		fi
	elif [ "${boot_status}" == "01" ]; then
		# Check and set boot progress to dbus
		set_boot_progress "$boot_stage" "$uefi_code"
	fi

	# Log POST Code to dbus.
	set_postcode "0x$boot_stage$boot_status$uefi_code"

	# Stop the service when booting to OS
	if [ "${boot_stage}" == "08" ] && [ "${boot_status}" == "02" ]; then
		update_boot_progress "SystemInitComplete"
		log_redfish_biosboot_ok_event
	elif [ "${boot_stage}" == "09" ] && [ "${boot_status}" == "02" ];
	then
		update_boot_progress "OSRunning"
		break
	fi
done

