#!/bin/bash -e
#

script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
obmc_dir=${script_dir}/../../

# openbmc doesn't control what upstream poky, or any of the other layers do,
# which do use patches as part of their upstreaming process.
# meta-phosphor is also included such that patches that the community agrees to
# hold onto will be allowed in that layer.

patch_files_tmp=$(mktemp)
allowed_patches_tmp=$(mktemp)

git -C "$obmc_dir" ls-files -- \
  '*.patch' \
  ':!:poky/**' \
  ':!:meta-security/**' \
  ':!:meta-xilinx/**' \
  ':!:meta-raspberrypi/**' \
  ':!:meta-openembedded/**' \
  ':!:meta-phosphor/**' \
  | sort > $patch_files_tmp


# The following patches were present on master at the time this test was
# written.  Their presence in this list should not be acknowlegement that they
# are now allowed, but ignoring them is required in the intermediate time
# between when this test was created, and when the maintainers of these repos
# clean them up.
#
# https://github.com/openbmc/docs/blob/master/meta-layer-guidelines.md
echo "\
meta-amd/meta-ethanolx/recipes-x86/chassis/x86-power-control/0001-Amd-power-control-modifications-for-EthanolX.patch
meta-ampere/meta-common/recipes-devtools/mtd/mtd-utils/0001-flashcp-support-offset-option.patch
meta-ampere/meta-jade/recipes-bsp/u-boot/u-boot-aspeed/0001-aspeed-scu-Switch-PWM-pin-to-GPIO-input-mode.patch
meta-ampere/meta-jade/recipes-bsp/u-boot/u-boot-aspeed/0002-aspeed-Disable-internal-PD-resistors-for-GPIOs.patch
meta-ampere/meta-jade/recipes-bsp/u-boot/u-boot-aspeed/0003-aspeed-support-passing-system-reset-status-to-kernel.patch
meta-ampere/meta-jade/recipes-bsp/u-boot/u-boot-aspeed/0004-aspeed-add-gpio-support.patch
meta-ampere/meta-jade/recipes-bsp/u-boot/u-boot-aspeed/0005-aspeed-Enable-SPI-master-mode.patch
meta-ampere/meta-jade/recipes-bsp/u-boot/u-boot-aspeed/0006-aspeed-support-Mt.Jade-platform-init.patch
meta-aspeed/recipes-bsp/u-boot/files/default-gcc.patch
meta-bytedance/meta-g220a/recipes-kernel/linux/linux-aspeed/0001-bytedance-g220a-Enable-ipmb.patch
meta-bytedance/meta-g220a/recipes-kernel/linux/linux-aspeed/0003-misc-aspeed-Add-Aspeed-UART-routing-control-driver.patch
meta-bytedance/meta-g220a/recipes-kernel/linux/linux-aspeed/0004-ARM-dts-aspeed-Add-uart-routing-node.patch
meta-bytedance/meta-g220a/recipes-kernel/linux/linux-aspeed/0005-ARM-dts-aspeed-Enable-g220a-uart-route.patch
meta-bytedance/meta-g220a/recipes-phosphor/ipmi/phosphor-node-manager-proxy/0001-Remove-Total_Power-sensor.patch
meta-facebook/meta-bletchley/recipes-bsp/u-boot/u-boot-aspeed-sdk/0001-u-boot-ast2600-57600-baudrate-for-bletchley.patch
meta-facebook/meta-tiogapass/recipes-bsp/u-boot/u-boot-aspeed/0001-configs-ast-common-use-57600-baud-rate-to-match-Tiog.patch
meta-facebook/meta-yosemitev2/recipes-bsp/u-boot/u-boot-aspeed/0001-board-aspeed-Add-Mux-for-yosemitev2.patch
meta-facebook/meta-yosemitev2/recipes-bsp/u-boot/u-boot-aspeed/0002-spl-host-console-handle.patch
meta-google/dynamic-layers/nuvoton-layer/recipes-bsp/images/npcm7xx-igps/0001-Set-FIU0_DRD_CFG-and-FIU_Clk_divider-for-gbmc-hoth.patch
meta-google/recipes-extended/libconfig/files/0001-conf2struct-Use-the-right-perl.patch
meta-google/recipes-extended/libconfig/files/0001-makefile-Add-missing-LDFLAGS.patch
meta-google/recipes-phosphor/initrdscripts/obmc-phosphor-initfs/rwfs-clean-dev.patch
meta-ingrasys/meta-zaius/recipes-bsp/u-boot/u-boot-aspeed/0001-board-aspeed-Add-reset_phy-for-Zaius.patch
meta-nuvoton/recipes-bsp/images/npcm7xx-igps/0001-Adjust-paths-for-use-with-Bitbake.patch
meta-yadro/meta-nicole/recipes-bsp/u-boot/files/0001-Add-system-reset-status-support.patch
meta-yadro/meta-nicole/recipes-bsp/u-boot/files/0002-config-ast-common-set-fieldmode-to-true.patch
meta-yadro/meta-nicole/recipes-bsp/u-boot/files/0003-aspeed-add-gpio-support.patch
meta-yadro/meta-nicole/recipes-bsp/u-boot/files/0004-aspeed-add-bmc-position-support.patch
meta-yadro/meta-nicole/recipes-kernel/linux/linux-aspeed/0001-Add-NCSI-channel-selector.patch
meta-yadro/meta-nicole/recipes-phosphor/host/op-proc-control/0001-Stop-and-send-SRESET-for-one-thread-only.patch
meta-yadro/recipes-phosphor/dbus/phosphor-dbus-interfaces/0001-Add-boot-initiator-mailbox-interface.patch
meta-yadro/recipes-phosphor/ipmi/phosphor-ipmi-host/0001-Add-support-for-persistent-only-settings.patch
meta-yadro/recipes-phosphor/ipmi/phosphor-ipmi-host/0002-Add-support-for-boot-initiator-mailbox.patch
meta-yadro/recipes-phosphor/ipmi/phosphor-ipmi-host/0003-Fix-version-parsing-update-AUX-revision-info.patch
" | sort > $allowed_patches_tmp



files_diff=$(comm -23 $patch_files_tmp $allowed_patches_tmp)
rm $allowed_patches_tmp
rm $patch_files_tmp

files_count=$(echo -n "$files_diff" | grep -c '^' || true)
if [[ $files_count -ne 0 ]]; then
  echo "Patch files found not in allow list"
  echo "$files_diff"
  echo "Patches are not allowed on OpenBMC in these layers.  Please upstream your changes and see \
    https://github.com/openbmc/docs/blob/master/meta-layer-guidelines.md"
  exit 1
fi

echo "Repo test passed"

