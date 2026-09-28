# Tests {.chapter permissions=non-confidential}

All the tests in this chapter assume you have already built the software stack at least once by following the build instructions in the Build, Flash and Run chapter.

## Reports {.reference}

Reports for the tests conducted on the [Corstone-1000 software (CORSTONE1000-2026.05)](https://git.yoctoproject.org/meta-arm/tag/?h=CORSTONE1000-2026.05) release are available for reference in the [GitLab repo](https://gitlab.arm.com/arm-reference-solutions/arm-reference-solutions-test-report/-/tree/CORSTONE1000-2026.05/embedded-a/corstone1000/CORSTONE1000-2026.05?ref_type=tags).

## SystemReady IR {.reference}

This section tells you how to run the Architecture Compliance Suite (ACS) tests.

:::note
In this release, SystemReady IR has been renamed SystemReady Devicetree Band.
:::

### Build an EFI system partition {.reference}

A storage with EFI System Partition (ESP) must exist in the system for the UEFI-SCT related tests to pass.

1. Build an ESP partition for your target

   ```
   kas build meta-arm/kas/corstone1000-a320-fvp.yml:meta-arm/ci/debug.yml --target corstone1000-esp-image
   ```

2. Locate the `corstone1000-esp-image-corstone1000-a320-fvp.wic` build artefact
   in `${WORKSPACE}/build/tmp/deploy/images/corstone1000-a320-fvp/`

### Use the EFI system partition {.reference}

The ESP disk image will automatically be used by the Corstone-1000 FVP as the 2nd MMC card image.
It will be used when the SystemReady-IR tests is performed on the FVP in the later section.

### Run SystemReady IR ACS tests {.reference}

ACS is used to ensure architectural compliance across different implementations of the architecture.
Arm Enterprise ACS includes a set of examples of the invariant behaviors that are provided by a
set of specifications for enterprise systems (i.e. SBSA, SBBR, etc.).
Implementers can verify if these behaviors have been interpreted correctly.

The following test suites and bootable applications are under the `BOOT` partition of the ACS image:

- SCT
- FWTS
- BSA UEFI
- BSA linux
- GRUB
- UEFI manual capsule application

See the directory structure of the ACS image `BOOT` partition below:

```

├── acs_results
├── EFI
│   └── BOOT
│       ├── app
│       ├── bbr
│       ├── bootaa64.efi
│       ├── bsa
│       ├── debug
│       ├── grub.cfg
│       ├── Shell.efi
│       ├── sie_startup.nsh
│       └── startup.nsh
├── Image
├── security-interface-extension-keys
│   ├── NullPK.auth
│   ├── TestDB1.auth
│   ├── TestDB1.der
│   ├── TestDBX1.auth
│   ├── TestDBX1.der
│   ├── TestKEK1.auth
│   ├── TestKEK1.der
│   ├── TestPK1.auth
│   └── TestPK1.der
├── startup.nsh
└── yocto_image.flag
```

The `BOOT` partition is also used to store test results in the `acs_results` folder.

:::note
Ensure that the `acs_results` folder is empty before starting the test.
:::

To build and run ACS tests on Corstone-1000:

1. On your host development machine, clone the [Arm SystemReady ACS repository](https://github.com/ARM-software/arm-systemready/).

   ```
   cd ${WORKSPACE}
   git clone https://github.com/ARM-software/arm-systemready.git -b v23.09_SR_REL2.0.0_ES_REL1.3.0_IR_REL2.1.0 --depth 1
   ```

   This repository contains the infrastructure to build the ACS and the bootable prebuilt images to be used for the
   certifications of SystemReady IR.

2. Find the pre-built ACS live image in `${WORKSPACE}/arm-systemready/IR/prebuilt_images/v23.09_2.1.0/ir-acs-live-image-generic-arm64.wic.xz`.

   :::note
   This prebuilt ACS image includes v5.13 kernel, which does not provide USB driver support for Corstone-1000.
   :::

3. Decompress the pre-built ACS live image.

   ```
   cd ${WORKSPACE}/arm-systemready/IR/prebuilt_images/v23.09_2.1.0
   unxz ir-acs-live-image-generic-arm64.wic.xz
   ```

4. Run `tmux`:

   ```
   cd ${WORKSPACE} && tmux
   ```

5. Run the commands below within `tmux` to run the ACS test on FVP using the built firmware image and the pre-built ACS image identified above:

   ```
   ./meta-arm/scripts/runfvp \
   --terminals=tmux \
   ./build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000-flash-firmware-image-corstone1000-a320-fvp.fvpconf \
   -- -C board.msd_mmc.p_mmc_file=${WORKSPACE}/arm-systemready/IR/prebuilt_images/v23.09_2.1.0/ir-acs-live-image-generic-arm64.wic
   ```

   :::note
   The FVP will reset multiple times during the test. The ACS tests might take up to 1 day to complete when run on FVP.
   :::

The message `ACS run is completed` will be displayed on the FVP host terminal when the test runs to completion.
You will be prompted to press the Enter key to access the Linux prompt.

#### Test sequence and results {.reference}

U-Boot should be able to boot the GRUB bootloader from the first partition.

If GRUB is not interrupted, the tests are executed automatically in the following order:

- SCT
- UEFI BSA
- FWTS

The results can be fetched from the `acs_results` folder in the `BOOT` partition of the SD Card image used by FVP.

Access the `acs_results` folder in FVP by running the following commands:

```
sudo mkdir /mnt/test
sudo mount -o rw,offset=1048576 \
${WORKSPACE}/arm-systemready/IR/prebuilt_images/v23.09_2.1.0/ir-acs-live-image-generic-arm64.wic \
/mnt/test
```

## Capsule update {.reference}

The following Payload GUIDs (`${BL2_GUID}`, `${TFM_S_GUID}`, `${FIP_GUID}`, and `${INITRAMFS_GUID}`) are for the `fvp`:

Table: Payload GUIDs

+-----------+--------------------------------------+
| Payloads  | FVP                                  |
+===========+======================================+
| BL2       | f1d883f9-dfeb-5363-98d8-686ee3b69f4f |
+-----------+--------------------------------------+
| TFM_S     | 7fad470e-5ec5-5c03-a2c1-4756b495de61 |
+-----------+--------------------------------------+
| FIP       | f1933675-5a8c-5b6d-9ef4-846739e89bc8 |
+-----------+--------------------------------------+
| INITRAMFS | f771aff9-c7e9-5f99-9eda-2369dd694f61 |
+-----------+--------------------------------------+

The following section describes the steps to update the firmware using Capsule Update
as the Corstone-1000 supports UEFI.

The firmware update process is tested with an invalid capsule and with valid capsules to validate the robustness and
error-handling capabilities of the firmware update mechanism.

- Positive full capsule update test: The Corstone-1000 is provided with a valid full capsule, which it applies successfully. The system then boots normally and reaches the Linux command prompt.
- Positive partial capsule update test: The Corstone-1000 is provided with a valid partial capsule that specifies an update for a single component only. The capsule is applied successfully, after which the system boots normally and reaches the Linux command prompt.
- Rollback protection capsule update test: The Corstone-1000 is provided with an outdated capsule containing lower version numbers for all payloads. The capsule is correctly rejected due to rollback protection, and the previously installed firmware is retained.

Three different capsules are therefore needed to perform the tests.

The following payloads can be individually updated:

- Boot Loader stage 2 (BL2)
- Trusted Firmware-M Secure partition (TFM_S)
- Firmware Image Package (FIP)
- Initial RAM Filesystem (INITRAMFS)

### Generate capsules {.reference}

[EDK II's]($edk2_repository) `GenerateCapsule` tool is used to generate capsules and is built automatically
for the host machine during the firmware image building process.
The tool can be found at `${WORKSPACE}/build/tmp/sysroots-components/aarch64/edk2-basetools-native/usr/bin/edk2-BaseTools/BinWrappers/PosixLike/GenerateCapsule`.

:::note
The `aarch64` part of this path depends on the build host architecture
and can be different on another host.
:::

A JSON file containing metadata about the capsule payloads needs to be created using the script
found at `${WORKSPACE}/meta-arm/meta-arm/scripts/generate_capsule_json_multiple.py`.
This JSON file is required by EDK II's `GenerateCapsule` tool to generate the capsule.

The capsule's default metadata passed can be found in the `${WORKSPACE}/meta-arm/meta-arm-bsp/recipes-bsp/images/corstone1000-flash-firmware-image.bb`
and `${WORKSPACE}/meta-arm/kas/corstone1000-image-configuration.yml` files.

#### Valid full capsule {.reference}

An automatically generated capsule can be found at `${WORKSPACE}/build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000-a320-fvp-v6.uefi.capsule` after running a firmware build.

The default metadata values are assumed to be correct to generate a valid capsule.

This capsule will be used for the positive capsule update test.

#### Valid partial capsule {.reference}

To generate a capsule that updates only a single component, explicitly set the firmware version for that component and mark it as the only payload to be updated.

The partial capsule is also valid, but sets the firmware version to 7 only for the BL2 component, indicating that no other components should be updated.

Use the following commands to generate the `capsule_config.json` file, which is required by the EDK2 tool for capsule creation:

```
cd ${WORKSPACE}

python3 meta-arm/meta-arm/scripts/generate_capsule_json_multiple.py \
--selected_components DUMMY_START  BL2  DUMMY_END \
--components DUMMY_START  BL2  TFM_S  FIP  INITRAMFS  DUMMY_END \
--fw_versions 0 7 0 0 0 0 \
--guids \
6f784cbf-7938-5c23-8d6e-24d2f1410fa9  \
${BL2_GUID} ${TFM_S_GUID}  ${FIP_GUID}  ${INITRAMFS_GUID} \
b57e432b-a250-5c73-93e3-90205e64baba \
--hardware_instances 1  1  1  1  1  1 \
--lowest_supported_versions 5 5 5 5 5 5 \
--monotonic_counts 1  1  1  1  1  1 \
--payloads \
build/tmp/work/corstone1000_a320_fvp-poky-linux-musl/corstone1000-flash-firmware-image/1.0/sources/corstone1000-flash-firmware-image-1.0/dummy.bin \
build/tmp/deploy/images/corstone1000-a320-fvp/trusted-firmware-m/bl2_signed.bin \
build/tmp/deploy/images/corstone1000-a320-fvp/trusted-firmware-m/tfm_s_signed.bin \
build/tmp/deploy/images/corstone1000-a320-fvp/signed_fip.bin \
build/tmp/deploy/images/corstone1000-a320-fvp/Image.gz-initramfs-corstone1000-a320-fvp.bin \
build/tmp/work/corstone1000_a320_fvp-poky-linux-musl/corstone1000-flash-firmware-image/1.0/sources/corstone1000-flash-firmware-image-1.0/dummy.bin \
--update_image_indexes 5  1  2  3  4  6 \
--private_keys \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_key.key \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_key.key \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_key.key \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_key.key \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_key.key \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_key.key \
--certificates \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_cert.crt \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_cert.crt \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_cert.crt \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_cert.crt \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_cert.crt \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_cert.crt \
--output capsule_config.json
```

Run the command below to generate the partial capsule:

```
./build/tmp/sysroots-components/aarch64/edk2-basetools-native/usr/bin/edk2-BaseTools/BinWrappers/PosixLike/GenerateCapsule \
-e \
-j capsule_config.json \
--capflag PersistAcrossReset \
-o corstone1000-a320-fvp-partial-v7.uefi.capsule
```

The partial capsule will be located in the `${WORKSPACE}` directory.

#### Invalid capsule {.reference}

Generate a capsule with firmware version metadata for all payloads set lower than that of a valid capsule.
The valid capsule has a default firmware version of 6 for all payloads, while the simulated invalid capsule has the firmware version set to 5 for all payloads.

Use the following commands to generate the `capsule_config.json` file, which is required by the EDK2 tool for capsule creation:

```
cd ${WORKSPACE}

python3 meta-arm/meta-arm/scripts/generate_capsule_json_multiple.py \
--selected_components DUMMY_START  BL2  TFM_S  FIP  INITRAMFS  DUMMY_END \
--components DUMMY_START  BL2  TFM_S  FIP  INITRAMFS  DUMMY_END \
--fw_versions 5 5 5 5 5 5 \
--guids \
6f784cbf-7938-5c23-8d6e-24d2f1410fa9  \
${BL2_GUID} ${TFM_S_GUID}  ${FIP_GUID}  ${INITRAMFS_GUID} \
b57e432b-a250-5c73-93e3-90205e64baba \
--hardware_instances 1  1  1  1  1  1 \
--lowest_supported_versions 5 5 5 5 5 5 \
--monotonic_counts 1  1  1  1  1  1 \
--payloads \
build/tmp/work/corstone1000_a320_fvp-poky-linux-musl/corstone1000-flash-firmware-image/1.0/sources/corstone1000-flash-firmware-image-1.0/dummy.bin \
build/tmp/deploy/images/corstone1000-a320-fvp/trusted-firmware-m/bl2_signed.bin \
build/tmp/deploy/images/corstone1000-a320-fvp/trusted-firmware-m/tfm_s_signed.bin \
build/tmp/deploy/images/corstone1000-a320-fvp/signed_fip.bin \
build/tmp/deploy/images/corstone1000-a320-fvp/Image.gz-initramfs-corstone1000-a320-fvp.bin \
build/tmp/work/corstone1000_a320_fvp-poky-linux-musl/corstone1000-flash-firmware-image/1.0/sources/corstone1000-flash-firmware-image-1.0/dummy.bin \
--update_image_indexes 5  1  2  3  4  6 \
--private_keys \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_key.key \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_key.key \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_key.key \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_key.key \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_key.key \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_key.key \
--certificates \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_cert.crt \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_cert.crt \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_cert.crt \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_cert.crt \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_cert.crt \
build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000_capsule_cert.crt \
--output capsule_config.json
```

Run the command below to generate the invalid capsule:

```
./build/tmp/sysroots-components/aarch64/edk2-basetools-native/usr/bin/edk2-BaseTools/BinWrappers/PosixLike/GenerateCapsule \
-e \
-j capsule_config.json \
--capflag PersistAcrossReset \
-o corstone1000-a320-fvp-v5.uefi.capsule
```

The invalid capsule will be located in the `${WORKSPACE}` directory.

### Transfer capsules to target {.reference}

The capsule delivery process described below is the direct method (usage of capsules from the ACS image)
as opposed to the on-disk method (delivery of capsules using a file on a mass storage device).

1. Download and extract the ACS image as described in the ACS image preparation steps above.
   The ACS image extraction location will be referred below as `${ACS_IMAGE_PATH}`.

   :::note
   Creating a USB drive with the ACS image is not required as the image will be mounted with the steps below.
   :::

2. Find the first partition's offset of the `ir-acs-live-image-generic-arm64.wic` image using the `fdisk` tool.
   The partition table can be listed using:

   ```
   fdisk -lu ${ACS_IMAGE_PATH}/ir-acs-live-image-generic-arm64.wic
   Device                                                 Start     End Sectors  Size Type
   ${ACS_IMAGE_PATH}/ir-acs-live-image-generic-arm64.wic1    2048  309247  307200  150M Microsoft basic data
   ${ACS_IMAGE_PATH}/ir-acs-live-image-generic-arm64.wic2  309248 1343339 1034092  505M Linux filesystem
   ```

   Given that the first partition starts at sector 2048 and each sector is 512 bytes in size,
   the first partition is at offset 1048576 (2048 x 512).

3. Mount the `ir-acs-live-image-generic-arm64.wic` image using the previously calculated offset:

   ```
   sudo mkdir /mnt/ir-acs-live-image-generic-arm64
   sudo mount -o rw,offset=<first_partition_offset> ${ACS_IMAGE_PATH}/ir-acs-live-image-generic-arm64.wic  /mnt/ir-acs-live-image-generic-arm64
   ```

4. Copy the capsules:

   ```
   sudo cp ${WORKSPACE}/build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000-a320-fvp-v6.uefi.capsule /mnt/ir-acs-live-image-generic-arm64/
   sudo cp ${WORKSPACE}/corstone1000-a320-fvp-v5.uefi.capsule /mnt/ir-acs-live-image-generic-arm64/
   sudo cp ${WORKSPACE}/corstone1000-a320-fvp-partial-v7.uefi.capsule /mnt/ir-acs-live-image-generic-arm64/
   sync
   ```

5. Unmount the IR image:

   ```
   sudo umount /mnt/ir-acs-live-image-generic-arm64
   ```

### Run capsule update tests {.reference}

The valid capsules will be used first to run the positive capsule update tests.
This will be followed by using the invalid capsule to run the rollback protection capsule update test.

:::note
This sequence order must be respected as the invalid capsule has a firmware version lower than the firmware version in the valid capsule. The rollback protection capsule update test effectively tests that firmware rollback is not permitted.
:::

#### Positive full capsule update {.reference}

To run the test:

1. Run Corstone-1000 with the ACS image containing the capsule files:

   1. Run `tmux`:

      ```
      cd ${WORKSPACE} && tmux
      ```

   2. Run the FVP within `tmux` with the IR prebuilt image which now also contains the two capsules:

      ```
      kas shell meta-arm/kas/corstone1000-a320-fvp.yml:meta-arm/ci/debug.yml \
      -c "../meta-arm/scripts/runfvp --terminals=tmux \
      -- -C board.msd_mmc.p_mmc_file=${ACS_IMAGE_PATH}/ir-acs-live-image-generic-arm64.wic"
      ```

      :::note
      `${ACS_IMAGE_PATH}` must be an absolute path. Ensure there are no spaces before or after of `=` of the `-C board.msd_mmc.p_mmc_file` option.
      :::

2. Wait until U-Boot loads EFI from the ACS image and interrupt the EFI shell by pressing the `Escape` key when the following prompt is displayed.

   ```
   Press ESC in 4 seconds to skip startup.nsh or any other key to continue.
   ```

3. The content of the first file system (`File System 0`), where the capsule files were copied, can be accessed by running the command `FS0:`.

4. Run the `CapsuleApp` application with the valid capsule file:

   ```
   EFI/BOOT/app/CapsuleApp.efi corstone1000-a320-fvp-v6.uefi.capsule
   ```

   The capsule update will be started.

   :::note
   The capsule update takes about 15-30 minutes to complete on FVP.

   The platform will reset after successfully applying the capsule.
   :::

   The software stack copies the capsule content to the external flash, which is shared between the Secure Enclave and the Host Processor
   before rebooting the system, and the following logs should be displayed on the Secure Enclave terminal:

   ```
   ...
   fwu_bootloader_install_image: enter
   metadata_read: success: active = 0, previous = 1
   fwu_update_metadata: enter
   metadata_write: success: active = 1, previous = 0
   fwu_update_metadata: exit: ret = 0
   fwu_bootloader_install_image: exit: ret = 0
   ...
   ```

   The above log snippet indicates that the new capsule image is successfully applied.

5. Interrupt the U-Boot shell.

   ```
   Hit any key to stop autoboot:
   ```

   After the first reboot, TrustedFirmware-M should display the following logs on the Secure Enclave terminal:

   ```
   ...
   [INF] Starting TF-M BL1_1
   metadata_read: success: active = 1, previous = 0
   get_fwu_agent_state: exit: FWU Agent PSA_FWU_TRIAL (index mismatch)
   bl1_get_active_bl2_image: booting from trial bank: 1
   bl1_get_active_bl2_image: exit: booting from bank = 1, offset = 0x1002000
   ...
   ```

6. Run the following commands in order to run the Corstone-1000 Linux kernel.

   :::note
   Otherwise, the execution ends up in the ACS live image.
   :::

   ```
   $ unzip $kernel_addr 0x90000000
   $ loadm 0x90000000 $kernel_addr_r $filesize
   $ bootefi $kernel_addr_r $fdtcontroladdr
   ```

   After executing the above set of commands, the following logs should be displayed on the Secure Enclave terminal:

   ```
   ...
   fwu_accept_image: success: fwu state is changed to regular
   update_nv_counters: success
   disable_host_ack_timer: timer to reset is disabled
   FMP image update: status = 0version=6 last_attempt_version=6.
   fwu_bootloader_mark_image_accepted: exit: ret = 0
   ...
   ```

7. The first boot after a capsule update is considered the trial stage, during which the FWU image is accepted.
   However, to view the updated contents of the EFI System Resource Table (ESRT), an additional reboot is required.

   ```
   # reboot
   ```

8. Interrupt the U-Boot shell when prompted.

   ```
   Hit any key to stop autoboot:
   ```

9. Run the following commands to boot the Corstone-1000 Linux kernel.

   :::note
   If these commands are not executed, the system will default to booting into the ACS live image.
   :::

   ```
   $ unzip $kernel_addr 0x90000000
   $ loadm 0x90000000 $kernel_addr_r $filesize
   $ bootefi $kernel_addr_r $fdtcontroladdr
   ```

10. Once the system has fully booted again, read [Verifying firmware versions with ESRT] to confirm that the firmware version reflects the updated capsule.

:::note
Do not terminate FVP between the positive full capsule update and partial capsule update tests.
:::

#### Positive partial capsule update {.reference}

Follow the steps for the [Positive full capsule update test], ensuring you use `corstone1000-a320-fvp-partial-v7.uefi.capsule` instead of `corstone1000-a320-fvp-v6.uefi.capsule`.

Once the system has fully booted again, read [Verifying firmware versions with ESRT] to confirm that the firmware version reflects the updated capsule.

:::note
Do not terminate FVP between the positive partial capsule update rollback protection capsule update tests.
:::

#### Rollback protection capsule update {.reference}

The [Positive partial capsule update test] must be run before running the rollback protection capsule update test.

To run the rollback protection capsule update test:

1. After running the positive capsule update test, reboot the system by typing the following command:

   ```
   reboot
   ```

2. Wait until U-Boot loads EFI from the ACS image and interrupt the EFI shell by pressing the `Escape` key when the following prompt is displayed.

   ```
   Press ESC in 4 seconds to skip startup.nsh or any other key to continue.
   ```

3. The content of the first file system (`File System 0`), where the capsule files were copied, can be accessed by running the following command:

   ```
   FS0:
   ```

4. Run the `CapsuleApp` application with the invalid capsule file:

   ```
   EFI/BOOT/app/CapsuleApp.efi corstone1000-a320-fvp-v5.uefi.capsule
   ```

5. TrustedFirmware-M should reject the capsule due to having a lower firmware version and display the following log on the Secure Enclave terminal:

   ```
   ...
   fwu_bootloader_load_image: enter: block_offset = 0
   FMP version: 0x5, metadata version : 0x7
   private_metadata_write: enter: boot_index = 0
   private_metadata_write: success
   fmp_set_image_info:160 Enter
   FMP image update: image id = 0
   FMP image update: status = 1version=7 last_attempt_version=5.
   fmp_set_image_info:184 Exit.
   ERROR: fwu_bootloader_load_image: version error
   remove_all_stale_partitions: Removed GPT partition 'bl2_secondary'
   remove_all_stale_partitions: Removed GPT partition 'tfm_secondary'
   remove_all_stale_partitions: Removed GPT partition 'FIP_B'
   remove_all_stale_partitions: Removed GPT partition 'kernel_secondary'
   fwu_bootloader_load_image: exit: ret = -248
   ...
   ```

   The Secure Enclave tries to load the new image a predetermined number of times
   if the capsule passes initial verification but fails verifications performed during
   boot time.

   ```
   ...
   metadata_write: success: active = 0, previous = 1
   fwu_select_previous: in regular state by choosing previous active bank
   ...
   ```

   The Secure Enclave eventually reverts back to the previously running image.

6. Reboot manually:

   ```
   Shell> reset
   ```

7. Interrupt the U-Boot shell.

   ```
   Hit any key to stop autoboot:
   ```

8. Run the following commands in order to run the Corstone-1000 Linux kernel, otherwise, the execution ends up in the ACS live image.

   ```
   $ unzip $kernel_addr 0x90000000
   $ loadm 0x90000000 $kernel_addr_r $filesize
   $ bootefi $kernel_addr_r $fdtcontroladdr
   ```

9. The first boot after a capsule update is considered the trial stage, during which the FWU image is rejected.
   However, to view the updated contents of the ESRT, an additional reboot is required.

   ```
   # reboot
   ```

10. Interrupt the U-Boot shell when prompted.

    ```
    Hit any key to stop autoboot:
    ```

11. Run the following commands to boot the Corstone-1000 Linux kernel. If these commands are not executed, the system will default to booting into the ACS live image.

    ```
    $ unzip $kernel_addr 0x90000000
    $ loadm 0x90000000 $kernel_addr_r $filesize
    $ bootefi $kernel_addr_r $fdtcontroladdr
    ```

12. Once the system has fully booted again, read [Verifying firmware versions with ESRT] to confirm that the firmware version reflects the updated capsule.

### Verifying firmware versions via ESRT {.reference}

After the system has fully booted, verify that the firmware versions of all applied capsule payloads
match those currently installed on the system. This can be done by inspecting the ESRT, which is exposed by the Linux kernel.

#### Reading ESRT entries {.reference}

To read each ESRT entry, use the following commands:

```
cat /sys/firmware/efi/esrt/entries/entry0/*
cat /sys/firmware/efi/esrt/entries/entry1/*
cat /sys/firmware/efi/esrt/entries/entry2/*
cat /sys/firmware/efi/esrt/entries/entry3/*
```

These entries typically correspond to:

- `entry0`: BL2
- `entry1`: TFM_S
- `entry2`: FIP
- `entry3`: INITRAMFS

:::note
Entry indices may vary depending on how your firmware capsules are structured. Adjust as needed.
:::

#### Structure of each ESRT entry {.reference}

Each directory under `/sys/firmware/efi/esrt/entries/entryX/` contains files representing the following fields:

Table: ESRT entry fields

+-------------------------------+------------------------------------------------------------+
| Field Name                    | Description                                                |
+===============================+============================================================+
| `capsule_flags`               | Attributes of the update capsule (e.g., persist, reset)    |
+-------------------------------+------------------------------------------------------------+
| `fw_class`                    | GUID identifying the firmware component                    |
+-------------------------------+------------------------------------------------------------+
| `fw_type`                     | Firmware type (e.g., system, device, peripheral)           |
+-------------------------------+------------------------------------------------------------+
| `fw_version`                  | Currently installed firmware version                       |
+-------------------------------+------------------------------------------------------------+
| `last_attempt_status`         | Status of the last update attempt (e.g., success, failure) |
+-------------------------------+------------------------------------------------------------+
| `last_attempt_version`        | Version that was last attempted to install                 |
+-------------------------------+------------------------------------------------------------+
| `lowest_supported_fw_version` | Minimum firmware version that is still supported           |
+-------------------------------+------------------------------------------------------------+

#### Verifying an ESRT entry {.reference}

To check the version and status of BL2 (`entry0`), run:

```
cat /sys/firmware/efi/esrt/entries/entry0/fw_version
cat /sys/firmware/efi/esrt/entries/entry0/last_attempt_version
cat /sys/firmware/efi/esrt/entries/entry0/last_attempt_status
```

#### Positive full capsule update test ESRT {.reference}

The following list shows the details of the first four ESRT entries for the positive capsule update test:

- BL2 (`${BL2_GUID}`):
  - FW type: 0
  - FW version: 6
  - Lowest supported version: 0
  - Capsule flags: 0
  - Last attempt status: 0
  - Last attempt version: 6
- TFM_S (`${TFM_S_GUID}`):
  - FW type: 0
  - FW version: 6
  - Lowest supported version: 0
  - Capsule flags: 0
  - Last attempt status: 0
  - Last attempt version: 6
- FIP (`${FIP_GUID}`):
  - FW type: 0
  - FW version: 6
  - Lowest supported version: 0
  - Capsule flags: 0
  - Last attempt status: 0
  - Last attempt version: 6
- INITRAMFS (`${INITRAMFS_GUID}`):
  - FW type: 0
  - FW version: 6
  - Lowest supported version: 0
  - Capsule flags: 0
  - Last attempt status: 0
  - Last attempt version: 6

#### Positive partial capsule update test ESRT {.reference}

The following list shows the details of the first four ESRT entries for the positive partial capsule update test:

- BL2 (`${BL2_GUID}`):
  - FW type: 0
  - FW version: 7
  - Lowest supported version: 0
  - Capsule flags: 0
  - Last attempt status: 0
  - Last attempt version: 7
- TFM_S (`${TFM_S_GUID}`):
  - FW type: 0
  - FW version: 6
  - Lowest supported version: 0
  - Capsule flags: 0
  - Last attempt status: 0
  - Last attempt version: 6
- FIP (`${FIP_GUID}`):
  - FW type: 0
  - FW version: 6
  - Lowest supported version: 0
  - Capsule flags: 0
  - Last attempt status: 0
  - Last attempt version: 6
- INITRAMFS (`${INITRAMFS_GUID}`):
  - FW type: 0
  - FW version: 6
  - Lowest supported version: 0
  - Capsule flags: 0
  - Last attempt status: 0
  - Last attempt version: 6

#### Rollback protection capsule update test ESRT {.reference}

The following list shows the details of the first four ESRT entries for the rollback protection capsule update test:

- BL2 (`${BL2_GUID}`):
  - FW type: 0
  - FW version: 7
  - Lowest supported version: 0
  - Capsule flags: 0
  - Last attempt status: 1
  - Last attempt version: 5
- TFM_S (`${TFM_S_GUID}`):
  - FW type: 0
  - FW version: 6
  - Lowest supported version: 0
  - Capsule flags: 0
  - Last attempt status: 0
  - Last attempt version: 6
- FIP (`${FIP_GUID}`):
  - FW type: 0
  - FW version: 6
  - Lowest supported version: 0
  - Capsule flags: 0
  - Last attempt status: 0
  - Last attempt version: 6
- INITRAMFS (`${INITRAMFS_GUID}`):
  - FW type: 0
  - FW version: 6
  - Lowest supported version: 0
  - Capsule flags: 0
  - Last attempt status: 0
  - Last attempt version: 6

See the [UEFI documentation](https://uefi.org/specs/UEFI/2.10/23_Firmware_Update_and_Reporting.html#id29) for more information on the significance of the table fields.

## Linux distributions {.reference}

This sections describes the steps to install major Linux distributions to the Corstone-1000 Host Processor.

The Linux distributions to be installed are:

- [Debian](https://www.debian.org/)
- [openSUSE](https://www.opensuse.org/)

Follow the instructions below to install the Linux distributions to the Corstone-1000 software stack.

### Prepare installation media {.reference}

The media containing the bootable files required to start the installation process needs to be prepared.

Follow the instructions below to create the installation media.

1. Using your development machine, download one of following Linux distribution images:

   - [Debian installer image](https://cdimage.debian.org/mirror/cdimage/archive/12.7.0/arm64/iso-dvd/)
   - [openSUSE Leap installer image](https://download.opensuse.org/distribution/leap/15.6/iso/openSUSE-Leap-15.6-DVD-aarch64-Current.iso)

   Note that the location of the ISO file on the development machine will be referred to as `${DISTRO_INSTALLER_ISO_PATH}`.

2. Create the installation media which will contain the necessary files to install the operation system.

   The distribution installer ISO file does not need to be burnt to a USB drive.
   It will be used as is when starting the FVP install the distribution.

### Prepare system drive {.reference}

A system (or boot) drive, to store all the operating system files and used to boot the distribution, is required as
Corstone-1000 on-board non-volatile storage size is insufficient for installing the distributions.

1. Create an 10 GB GUID Partition Table (GPT) formatted MultiMediaCard (MMC) image.

   ```
   dd if=/dev/zero of=${WORKSPACE}/fvp_distro_system_drive.img \
   bs=1 count=0 seek=10G; sync; \
   parted -s ${WORKSPACE}/fvp_distro_system_drive.img mklabel gpt
   ```

2. This MMC image will be used as the primary drive to boot the distribution.

### Installation {.reference}

To install:

1. Run the `tmux`:

   ```
   cd ${WORKSPACE} && tmux
   ```

2. Start the FVP within `tmux` with the system drive as the primary drive and the distro ISO file as the secondary drive:

   ```
   kas shell meta-arm/kas/corstone1000-a320-fvp.yml:meta-arm/ci/debug.yml \
   -c "../meta-arm/scripts/runfvp --terminals=tmux -- \
   -C board.msd_mmc.p_mmc_file=${WORKSPACE}/fvp_distro_system_drive.img \
   -C board.msd_mmc_2.p_mmc_file=${DISTRO_INSTALLER_ISO_PATH}"
   ```

   :::note
   The FVP distribution installation process can take 6-8 hours to complete.
   :::

   The Linux distribution will be installed on `fvp_distro_system_drive.img`.

#### Debian installation extra steps {.reference}

The Debian installation may need the following extra steps:

1. Answer `Yes` to the section `Install the GRUB boot loader`.

   If the GRUB installation fails, these are the steps to follow on the subsequent
   popups:

   1. Select `Continue`, then `Continue` again on the next popup.
   2. Scroll down and select `Execute a shell`.
   3. Select `Continue`.
   4. Enter the following command:

      ```
      in-target grub-install --no-nvram --force-extra-removable
      ```

   5. Enter the following command:

      ```
      in-target update-grub
      ```

   6. Enter the following command:

      ```
      exit
      ```

   7. Select `Continue without boot loader`, then select `Continue` on the next popup.
   8. At this stage, the installation should proceed as normal.

2. Answer `No` to the question `Update NVRAM variables to automatically boot into Debian?`.

### Boot distribution {.reference}

The platform should automatically boot into the installed operating system image.

Stop the FVP with `CTRL+C` and run `tmux`:

```
cd ${WORKSPACE} && tmux
```

Run the command below to simulate a cold boot:

```
kas shell meta-arm/kas/corstone1000-a320-fvp.yml:meta-arm/ci/debug.yml \
-c "../meta-arm/scripts/runfvp --terminals=tmux -- \
-C board.msd_mmc.p_mmc_file=${WORKSPACE}/fvp_distro_system_drive.img"
```

:::note
To manually enter recovery mode, once the FVP begins booting, you can quickly change the boot option in GRUB, to boot into recovery mode. This option will disappear quickly, so it is best to preempt it. Select `Advanced Options for <OS>` and then `<OS> (recovery mode)`.
:::

The platform will then enter recovery mode, from which the user can access a shell after entering the password for the `root` user.

#### Timeout optimizations {.reference}

Operating system timeouts are inconsistent across systems. Skip this section if the system boots to Debian or openSUSE without any issue.

Make the system modification below whilst in recovery mode to increase timeouts and boot to the installed distribution.

1. Remove the timeout limit for device operations.

   - Debian

     ```
     vi /etc/systemd/system.conf
     DefaultDeviceTimeoutSec=infinity
     ```

   - openSUSE

     ```
     vi /usr/lib/systemd/system.conf
     DefaultDeviceTimeoutSec=infinity
     ```

   :::note
   As modifying `system.conf` in `/usr/lib/systemd/` is not working as it is getting overwritten, copy `system.conf` from `/usr/lib/systemd/` to `/etc/systemd/system.conf.d/` after the above edit.
   :::

2. Set the maximum time that the system will wait for a user to successfully log in before timing out to 180 seconds.

   - Debian

     ```
     vi /etc/login.defs
     LOGIN_TIMEOUT   180
     ```

   - openSUSE

     ```
     vi /usr/etc/login.defs
     LOGIN_TIMEOUT   180
     ```

3. Ensure the changes are applied by running the command `systemctl daemon-reload`.

4. Perform a cold boot of the platform.

#### Log into the distribution {.reference}

Log in with the `root` username and its corresponding password (set during installation)
at the distribution login prompt after booting. For example for Debian, use the command `debian login:`.

## UEFI secure boot {.reference}

The UEFI Secure Boot test is designed to verify the integrity and authenticity of the system's boot process.
This test ensures that only trusted, signed images are executed, thereby preventing unauthorized or malicious code from running.
A successful test confirms that the signed image executes correctly, while any unsigned image is blocked from running.

### Generate keys, signed image and unsigned image {.reference}

To generate keys:

1. Build an EFI System Partition as described in [Building an EFI system partition].

2. Clone the `iot-platform-assets` repository to your workspace.

   ```
   cd ${WORKSPACE}

   git clone https://gitlab.arm.com/arm-reference-solutions/iot-platform-assets \
   -b CORSTONE1000-2026.05
   ```

3. Set the current working directory to build directory's subdirectory containing the software stack build images.

   ```
   cd ${WORKSPACE}/build/tmp/deploy/images/corstone1000-a320-fvp/
   ```

4. Run the image signing script (without changing the current working directory).

   ```
   ${WORKSPACE}/iot-platform-assets/corstone1000/secureboot/create_keys_and_sign.sh \
   -d fvp \
   -v ${CERTIFICATE_VALIDITY_DURATION_IN_DAYS}
   ```

   :::note
   The [efitools](https://github.com/vathpela/efitools/) package is required to execute the script. `${CERTIFICATE_VALIDITY_DURATION_IN_DAYS}` is an integer that specifies the certificate's validity period in days. Consult the image signing script help message (`-h`) for more information about other optional arguments. The script is interactive and contains commands that require `sudo` level permissions.
   :::

   The keys, signed kernel image, and unsigned kernel image will be copied to the exisiting ESP image. The modified ESP image can be found at `${WORKSPACE}/build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000-esp-image-corstone1000-a320-fvp.wic`.

### Run unsigned image boot test {.reference}

To run an unsigned image boot test:

1. Follow the instructions in [Use the EFI System Partition] to use the ESP.

2. Run the software stack as described in the Running the FVP model section of the Build, Flash and Run chapter.

3. On the Host Processor terminal host side, stop the execution of U-Boot when prompted to do so with the message `Hit any key to stop autoboot`.

   :::note
   There is a timeout of 3 seconds to stop the execution at the U-Boot prompt. The U-Boot  prompt looks as follows:

   ```
   corstone1000#
   ```

   :::

   The rest of the instructions below will be executed on the U-Boot terminal.

4. On the U-Boot , set the current MMC device.

   ```
   corstone1000# mmc dev 1
   ```

5. Enroll the four UEFI secure boot authenticated variables.

   ```
   corstone1000# \
   load mmc 1:1 $loadaddr corstone1000_secureboot_keys/PK.auth && setenv -e -nv -bs -rt -at -i $loadaddr:$filesize PK; \
   load mmc 1:1 $loadaddr corstone1000_secureboot_keys/KEK.auth && setenv -e -nv -bs -rt -at -i $loadaddr:$filesize KEK; \
   load mmc 1:1 $loadaddr corstone1000_secureboot_keys/db.auth && setenv -e -nv -bs -rt -at -i $loadaddr:$filesize db; \
   load mmc 1:1 $loadaddr corstone1000_secureboot_keys/dbx.auth && setenv -e -nv -bs -rt -at -i $loadaddr:$filesize dbx
   ```

6. Attempt to Load the unsigned kernel image.

   ```
   corstone1000# \
   load mmc 1:1 $loadaddr corstone1000_secureboot_fvp_images/Image_fvp; \
   loadm $loadaddr $kernel_addr_r $filesize; \
   bootefi $kernel_addr_r $fdtcontroladdr

   Booting /MemoryMapped(0x0,0x88200000,0x236aa00)
   Image not authenticated
   Loading image failed
   ```

The unsigned Linux kernel image should not be loaded.

### Run signed image boot test {.reference}

Before running this test, perform the unsigned image boot test first as described in [Running unsigned image boot tests].

Load the signed kernel image.

```
corstone1000# \
load mmc 1:1 $loadaddr corstone1000_secureboot_fvp_images/Image_fvp.signed; \
loadm $loadaddr $kernel_addr_r $filesize; \
bootefi $kernel_addr_r $fdtcontroladdr
```

The signed Linux kernel image should be booted successfully.

### Disable secure boot {.reference}

Running the UEFI Secure Boot Test steps stores UEFI authenticated variables in the secure flash.
As a result, U-Boot reads these variables and verifies the Linux kernel image before executing it at each reboot.

In a typical boot scenario, the Linux kernel image is not signed, which will prevent the system from booting due to failed image authentication.
To resolve this, the Platform Key (one of the UEFI authenticated variables for secure boot) needs to be deleted.

For FVP, continue in the same boot cycle in which the UEFI secure boot keys were enrolled.
Do not cold boot FVP before deleting the Platform Key, because the secure flash contents are not preserved across an FVP cold boot.

1. On the Host Processor terminal host side, stop the execution of U-Boot when prompted to do so with the message `Hit any key to stop autoboot`.

2. On the U-Boot , delete the Platform Key (PK).

   ```
   corstone1000# \
   mmc dev 1; \
   load mmc 1:1 $loadaddr corstone1000_secureboot_keys/PK_delete.auth && setenv -e -nv -bs -rt -at -i $loadaddr:$filesize PK; \
   boot
   ```

## PSA API {.reference}

The following tests the implementation of the Application Programming Interface (API)
of the Platform Security Architecture (PSA) certification scheme.

The test uses Arm Firmware Framework for Arm A-profile (FF-A)
to communicate between the normal world and the secure world to run the [Arm Platform Security Architecture Test Suite](https://github.com/ARM-software/psa-arch-tests).

The tests use the `arm_tstee` driver to access Trusted Services Secure Partitions from user space. The driver is included in the Linux Kernel, starting from v6.10.

1. Start the Corstone-1000 FVP and wait until it boots to Linux.

2. Run the PSA API tests by running the commands below in the order shown:

   ```
   psa-iat-api-test
   psa-crypto-api-test
   psa-its-api-test
   psa-ps-api-test
   ```

## Symmetric multiprocessing {.reference}

Symmetric multiprocessing (SMP) mode is supported on the Corstone-1000 with Cortex-A320 FVP, but is disabled by default.

1. Build the software stack with SMP mode enabled.

   ```
   kas build meta-arm/kas/corstone1000-a320-fvp.yml:meta-arm/ci/debug.yml:\
   meta-arm/kas/corstone1000-multicore.yml
   ```

2. Run the Corstone-1000 FVP.

   ```
   kas shell meta-arm/kas/corstone1000-a320-fvp.yml:meta-arm/ci/debug.yml:\
   meta-arm/kas/corstone1000-multicore.yml \
   -c "../meta-arm/scripts/runfvp"
   ```

3. Verify that the FVP is running the Host Processor with more than one CPU core:

   ```
   nproc
   4                  # number of processing units
   ```

## Ethos-U85 NPU {.reference}

To build on Ethos-U85 NPU:

1. Clone the `iot-platform-assets` repository to your `${WORKSPACE}`.

   ```
   cd ${WORKSPACE}
   git clone https://git.gitlab.arm.com/arm-reference-solutions/iot-platform-assets.git \
   -b CORSTONE1000-2026.05
   ```

2. Copy the additional kas configuration file to:

   ```
   cp ${WORKSPACE}/iot-platform-assets/corstone1000/ethos-u85_test/ethos-u85-test.yml \
   ${WORKSPACE}/meta-arm/kas/
   ```

3. Copy the mesa package Git patch file to your copy of meta-arm.

   ```
   cp -f ${WORKSPACE}/iot-platform-assets/corstone1000/ethos-u85_test/0001-arm-bsp-mesa-Package-Teflon-test-runner-and-models.patch \
   ${WORKSPACE}/meta-arm/
   ```

4. Apply the Git patch to meta-arm.

   ```
   cd ${WORKSPACE}/meta-arm/
   git apply 0001-arm-bsp-mesa-Package-Teflon-test-runner-and-models.patch
   cd ${WORKSPACE}
   ```

5. Re-build the Corstone-1000 FVP software stack as follows:

   ```
   kas build meta-arm/kas/corstone1000-a320-fvp.yml:meta-arm/ci/debug.yml:\
   meta-arm/kas/ethos-u85-test.yml
   ```

6. Run the Corstone-1000 FVP:

   ```
   kas shell meta-arm/kas/corstone1000-a320-fvp.yml:meta-arm/ci/debug.yml:\
   meta-arm/kas/ethos-u85-test.yml \
   -c "../meta-arm/scripts/runfvp"
   ```

7. To verify you are running the Corstone-1000 FVP (Cortex-A320), build and run the FVP and inspect the CPU model
   reported in `/proc/cpuinfo` as shown below. Inside the FVP shell, confirm the core type:

   ```
   grep -E 'CPU part|model name' /proc/cpuinfo
   # Expect: CPU part : 0xd8f  (which corresponds to Cortex-A320)
   ```

8. Run the `test_teflon` test application inside the FVP shell as follows:

   ```
   export TEFLON_TEST_DELEGATE=/usr/lib/libteflon.so
   export TEFLON_TEST_DATA=/usr/share/teflon/tests
   test_teflon --gtest_filter='Models.*'
   ```

   The test completes in approximately one minute.
