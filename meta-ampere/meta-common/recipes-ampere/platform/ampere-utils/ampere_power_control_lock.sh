#!/bin/bash

declare -a power_reboot_targets=(
				 obmc-host-reboot@0.target
				 obmc-host-warm-reboot@0.target
				 obmc-host-force-warm-reboot@0.target
				)

declare -a power_off_targets=(
			      obmc-chassis-hard-poweroff@0.target
			     )

declare -a power_on_targets=(
			     obmc-host-start@0.target
			    )

systemd1_service="org.freedesktop.systemd1"
systemd1_object_path="/org/freedesktop/systemd1"
systemd1_manager_interface="org.freedesktop.systemd1.Manager"
mask_method="MaskUnitFiles"
unmask_method="UnmaskUnitFiles"

function mask_reboot_targets()
{
	# To prevent reboot actions, this function will mask all reboot targets
	for target in "${power_reboot_targets[@]}"
	do
		busctl call $systemd1_service $systemd1_object_path $systemd1_manager_interface \
			$mask_method asbb 1 "$target" true true
	done
}

function unmask_reboot_targets()
{
	# Allow reboot targets work normal
	for target in "${power_reboot_targets[@]}"
	do
		busctl call $systemd1_service $systemd1_object_path $systemd1_manager_interface \
			$unmask_method asb 1 "$target" true
	done
}

function mask_off_targets()
{
	# To prevent off actions,this function will mask all off targets
	for target in "${power_off_targets[@]}"
	do
		busctl call $systemd1_service $systemd1_object_path $systemd1_manager_interface \
			$mask_method asbb 1 "$target" true true
	done
}

function unmask_off_targets()
{
	# Allow off targets work normal
	for target in "${power_off_targets[@]}"
	do
		busctl call $systemd1_service $systemd1_object_path $systemd1_manager_interface \
			$unmask_method asb 1 "$target" true
	done
}

function mask_on_targets()
{
	# To prevent on actions, this function will mask all on targets
	systemctl mask "${power_on_targets[@]}" --runtime
}

function unmask_on_targets()
{
	# Allow on targets work normal
	systemctl unmask "${power_on_targets[@]}" --runtime
}

purpose=$1
allow=$2

if [ "$purpose" == "reboot" ]; then
	if [ "$allow" == "false" ]
	then
		mask_reboot_targets
	else
		unmask_reboot_targets
	fi
elif [ "$purpose" == "off" ]; then
	if [ "$allow" == "false" ]
	then
		mask_off_targets
	else
		unmask_off_targets
	fi
elif [ "$purpose" == "on" ]; then
	if [ "$allow" == "false" ]
	then
		mask_on_targets
	else
		unmask_on_targets
	fi
fi
