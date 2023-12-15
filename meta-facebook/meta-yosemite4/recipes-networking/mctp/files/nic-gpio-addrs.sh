#!/bin/sh
# shellcheck disable=SC2034
# shellcheck source=meta-facebook/meta-yosemite4/recipes-yosemite4/plat-tool/files/yosemite4-common-functions
. /usr/libexec/yosemite4-common-functions

is_nuvoton_board="$(check_nuvoton_board)"

if [ -n "$is_nuvoton_board" ]
then
	WIDTH=32
	# PRSNT_NIC0 is at the 23th bit (GPIO Bank5: GPIO183), 0 means NIC is present
	PRSNT_NIC0=0xf0015004
	BITMASK_NIC0=0x800000

	# PRSNT_NIC1 is at the 29th bit (GPIO Bank5: GPIO189), 0 means NIC is present
	PRSNT_NIC1=0xf0015004
	BITMASK_NIC1=0x20000000

	# PRSNT_NIC2 is at the 28th bit (GPIO Bank2: GPIO92), 0 means NIC is present
	PRSNT_NIC2=0xf0012004
	BITMASK_NIC2=0x10000000

	# PRSNT_NIC3 is at the 24th bit (GPIO Bank5: GPIO184), 0 means NIC is present
	PRSNT_NIC3=0xf0015004
	BITMASK_NIC3=0x400000
else
	WIDTH=8
	# PRSNT_NIC0 is at the 6th bit (GPIOU5), 0 means NIC is present
	PRSNT_NIC0=0x1e780088
	BITMASK_NIC0=0x20

	# PRSNT_NIC1 is at the 1th bit (GPIOE0), 0 means NIC is present
	PRSNT_NIC1=0x1e780020
	BITMASK_NIC1=0x01

	# PRSNT_NIC2 is at the 2th bit (GPIOE1), 0 means NIC is present
	PRSNT_NIC2=0x1e780020
	BITMASK_NIC2=0x02

	# PRSNT_NIC3 is at the 4th bit (GPIOM3), 0 means NIC is present
	PRSNT_NIC3=0x1e780078
	BITMASK_NIC3=0x08
fi
