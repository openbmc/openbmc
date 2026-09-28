# Build, flash and run {.chapter permissions=non-confidential}

The Arm Corstone-1000 with Cortex-A320 software stack uses the Yocto Project to build a tiny Linux distribution suitable for the Arm Corstone-1000 with Cortex-A320 platform (kernel and initramfs filesystem less than 6 MB on the flash).

The Corstone-1000 with Cortex-A320 software stack can be run on [Arm Corstone-1000 with Cortex-A320 FVP (Fixed Virtual Platform)](https://developer.arm.com/downloads/-/arm-ecosystem-fvps) and is built on top of Yocto Project's [Wrynose release]($meta_arm_repository_release_branch).

The Yocto Project relies on the [BitBake](https://docs.yoctoproject.org/bitbake.html#bitbake-documentation) tool as its build tool. Please see the [Yocto Project documentation](https://docs.yoctoproject.org/) for more information.

## Prerequisites {.reference}

This guide assumes that your host machine is running Ubuntu 24.04 LTS (with `sudo` rights), with at least
32GB of free disk space and 16GB of RAM as minimum requirement.

The following prerequisites must be available on the host system:

- Git 2.39.2 or greater.
- Python 3.11.2 or greater.
- GNU Tar 1.34 or greater.
- GNU Compiler Collection 12.2 or greater.
- GNU Make 4.3 or greater.
- tmux 3.3 or greater.

Please follow the steps described in the Yocto mega manual:

- [Compatible Linux Distribution](https://docs.yoctoproject.org/singleindex.html#compatible-linux-distribution)
- [Build Host Packages](https://docs.yoctoproject.org/singleindex.html#build-host-packages)

## Software components {.reference}

Within the Yocto Project, each component included in the Corstone-1000 with Cortex-A320 software stack is specified as
a [BitBake recipe](https://docs.yoctoproject.org/bitbake/2.2/bitbake-user-manual/bitbake-user-manual-intro.html#recipes).
The recipes specific to the Corstone-1000 with Cortex-A320 BSP are located at:
`${WORKSPACE}/meta-arm/meta-arm-bsp/`.

`${WORKSPACE}` refers to the absolute path to your workspace where the `meta-arm` repository will be cloned. Consider exporting it (e.g., `export WORKSPACE=$(realpath .)`) if you're already in the workspace directory,
so you can copy and paste the commands from this guide verbatim.

The Yocto machine config files are at:

- `${WORKSPACE}/meta-arm/meta-arm-bsp/conf/machine/include/corstone1000-a320.inc`
- `${WORKSPACE}/meta-arm/meta-arm-bsp/conf/machine/corstone1000-a320-fvp.conf`

:::note
All the paths stated in this document are absolute paths.
:::

### Host processor components {.reference}

This section describes the components used in the host processor.

#### Trusted Firmware-A {.reference}

The following [Trusted Firmware-A](https://git.trustedfirmware.org/TF-A/trusted-firmware-a.git) components are used:

Table: Trusted Firmware-A components

| Type      | Path                                                                                                             |
| --------- | ---------------------------------------------------------------------------------------------------------------- |
| bbappend  | `${WORKSPACE}/meta-arm/meta-arm-bsp/recipes-bsp/trusted-firmware-a/trusted-firmware-a_%.bbappend`                |
| Recipe    | `${WORKSPACE}/meta-arm/meta-arm/recipes-bsp/trusted-firmware-a/trusted-firmware-a_2.14.1.bb`                     |

#### Trusted Services {.reference}

The following [Trusted Services](https://trusted-services.readthedocs.io/en/latest/index.html) components are used:

Table: Trusted Services components

| Type      | Path                                                                                                             |
| --------- | ---------------------------------------------------------------------------------------------------------------- |
| bbappend  | `${WORKSPACE}/meta-arm/meta-arm-bsp/recipes-security/trusted-services/libts_%.bbappend`                          |
| bbappend  | `${WORKSPACE}/meta-arm/meta-arm-bsp/recipes-security/trusted-services/ts-psa-crypto-api-test_%.bbappend`        |
| bbappend  | `${WORKSPACE}/meta-arm/meta-arm-bsp/recipes-security/trusted-services/ts-psa-iat-api-test_%.bbappend`           |
| bbappend  | `${WORKSPACE}/meta-arm/meta-arm-bsp/recipes-security/trusted-services/ts-psa-its-api-test_%.bbappend`           |
| bbappend  | `${WORKSPACE}/meta-arm/meta-arm-bsp/recipes-security/trusted-services/ts-psa-ps-api-test_%.bbappend`            |
| bbappend  | `${WORKSPACE}/meta-arm/meta-arm-bsp/recipes-security/trusted-services/ts-sp-se-proxy_%.bbappend`                |
| bbappend  | `${WORKSPACE}/meta-arm/meta-arm-bsp/recipes-security/trusted-services/ts-sp-smm-gateway_%.bbappend`             |
| Recipe    | `${WORKSPACE}/meta-arm/meta-arm/recipes-security/trusted-services/libts_git.bb`                                  |
| Recipe    | `${WORKSPACE}/meta-arm/meta-arm/recipes-security/trusted-services/ts-psa-crypto-api-test_git.bb`                |
| Recipe    | `${WORKSPACE}/meta-arm/meta-arm/recipes-security/trusted-services/ts-psa-iat-api-test_git.bb`                   |
| Recipe    | `${WORKSPACE}/meta-arm/meta-arm/recipes-security/trusted-services/ts-psa-its-api-test_git.bb`                   |
| Recipe    | `${WORKSPACE}/meta-arm/meta-arm/recipes-security/trusted-services/ts-psa-ps-api-test_git.bb`                    |
| Recipe    | `${WORKSPACE}/meta-arm/meta-arm/recipes-security/trusted-services/ts-sp-smm-gateway_git.bb`                     |
| Recipe    | `${WORKSPACE}/meta-arm/meta-arm/recipes-security/trusted-services/ts-sp-se-proxy_git.bb`                        |

#### OP-TEE {.reference}

The following [OP-TEE](https://git.trustedfirmware.org/OP-TEE/optee_os.git) components are used:

Table: OP-TEE components

| Type      | Path                                                                                                             |
| --------- | ---------------------------------------------------------------------------------------------------------------- |
| bbappend  | `${WORKSPACE}/meta-arm/meta-arm-bsp/recipes-security/optee/optee-os_%.bbappend`                                 |
| Recipe    | `${WORKSPACE}/meta-arm/meta-arm/recipes-security/optee/optee-os_4.9.0.bb`                                       |

#### U-Boot {.reference}

The following [U-Boot](https://github.com/u-boot/u-boot.git) components are used:

Table: U-Boot components

| Type      | Path                                                                                                             |
| --------- | ---------------------------------------------------------------------------------------------------------------- |
| bbappend  | `${WORKSPACE}/meta-arm/meta-arm/recipes-bsp/u-boot/u-boot_%.bbappend`                                           |
| bbappend  | `${WORKSPACE}/meta-arm/meta-arm-bsp/recipes-bsp/u-boot/u-boot_%.bbappend`                                       |
| Recipe    | `${WORKSPACE}/meta-arm/meta-arm-bsp/recipes-bsp/u-boot/u-boot_2025.10.bb`                                       |

#### Linux {.reference}

The distribution is based on the [Poky](https://docs.yoctoproject.org/ref-manual/terms.html#term-Poky)
distribution which is a Linux distribution stripped down to a minimal configuration.

The provided distribution is based on [BusyBox](https://www.busybox.net/) and built using [musl libc](https://musl.libc.org/).

The following Linux components are used:

Table: Linux components

| Type      | Path                                                                                                             |
| --------- | ---------------------------------------------------------------------------------------------------------------- |
| bbappend  | `${WORKSPACE}/meta-arm/meta-arm-bsp/recipes-kernel/linux/linux-yocto_%.bbappend`                                |
| Recipe    | `${WORKSPACE}/meta-arm/meta-arm-bsp/recipes-kernel/linux/linux-yocto_6.19.bb`                                   |
| defconfig | `${WORKSPACE}/meta-arm/meta-arm-bsp/recipes-kernel/linux/files/corstone1000/defconfig`                          |

### Secure enclave components {.reference}

This section describes the secure enclave components.

#### Trusted Firmware-M {.reference}

The following [Trusted Firmware-M](https://git.trustedfirmware.org/TF-M/trusted-firmware-m.git) are used:

Table: Trusted Firmware-M secure enclave components

| Type      | Path                                                                                                             |
| --------- | ---------------------------------------------------------------------------------------------------------------- |
| bbappend  | `${WORKSPACE}/meta-arm/meta-arm-bsp/recipes-bsp/trusted-firmware-m/trusted-firmware-m_%.bbappend`                |
| Recipe    | `${WORKSPACE}/meta-arm/meta-arm/recipes-bsp/trusted-firmware-m/trusted-firmware-m_2.2.2.bb`                     |

## Build {.reference}

To build the software stack, do the following:

:::note
Building binaries natively on Windows and AArch64 Linux is not supported. Use an Intel or AMD 64-bit architecture Linux based development machine to build the software stack and transfer the binaries to run the software stack on an FVP in Windows or AArch64 Linux if required.
:::

1. Create a new folder that will be your workspace:

   ```
   mkdir ${WORKSPACE}
   cd ${WORKSPACE}
   ```

2. Install kas version 4.4 with `sudo` rights:

   ```
   sudo pip3 install kas==4.4
   ```

   Ensure the kas installation directory is visible on the `$PATH` environment variable.

3. Clone the `meta-arm` Yocto layer in the workspace `${WORKSPACE}`.

   ```
   cd ${WORKSPACE}
   git clone https://git.yoctoproject.org/git/meta-arm -b CORSTONE1000-2026.05
   ```

4. Accept the EULA on the [Arm Developer](https://developer.arm.com/downloads/-/arm-ecosystem-fvps/eula) site to build a Corstone-1000 with Cortex-A320 image for FVP as follows:

   ```
   export ARM_FVP_EULA_ACCEPT="True"
   ```

5. Build a Corstone-1000 with Cortex-A320 image:

   ```
   kas build meta-arm/kas/corstone1000-a320-fvp.yml:meta-arm/ci/debug.yml
   ```

A clean build takes a significant amount of time given that all of the development machine utilities are also
built along with the target images. Those development machine utilities include executables (Python,
CMake, etc.) and the required toolchains.

Once the build succeeds, all output binaries will be placed in `${WORKSPACE}/build/tmp/deploy/images/corstone1000-a320-fvp/`.

Everything apart from the Secure Enclave ROM firmware is bundled into a single binary, the
`corstone1000-flash-firmware-image-corstone1000-a320-fvp.wic` file.

The output binaries run in the Corstone-1000 with Cortex-A320 platform are the following:

- The Secure Enclave ROM firmware: `${WORKSPACE}/build/tmp/deploy/images/corstone1000-a320-fvp/trusted-firmware-m/bl1.bin`
- The internal firmware flash image: `${WORKSPACE}/build/tmp/deploy/images/corstone1000-a320-fvp/corstone1000-flash-firmware-image-corstone1000-a320-fvp.wic`

## Build with SSH {.reference}

The `meta-arm/kas/corstone1000-a320-fvp.yml` build produces an image for booting from flash.

To build a bootable mass storage OS image with Dropbear SSH enabled, run:

```
kas build meta-arm/ci/corstone1000-a320-fvp.yml:meta-arm/kas/corstone1000-ssh.yml
```

The mass storage OS image can be found at `${WORKSPACE}/build/tmp/deploy/images/corstone1000-a320-fvp/core-image-minimal-corstone1000-a320-fvp.wic`

:::note
The generated `core-image-minimal-corstone1000-a320-fvp.fvpconf` attaches the mass storage OS image to `board.msd_mmc.p_mmc_file`.
:::


## Run {.reference}

Once the platform is turned ON, the Secure Enclave will start to boot, wherein the relevant memory contents of the `*.wic`
file are copied to their respective memory locations. Firewall policies are enforced
on memories and peripherals before bringing the Host Processor out of reset.

The Host Processor will boot TrustedFirmware-A, OP-TEE, U-Boot and then Linux before presenting a login prompt.

A Fixed Virtual Platform (FVP) model of the Corstone-1000 with Cortex-A320 platform must be available to run the
Corstone-1000 with Cortex-A320 FVP software image.

A Yocto recipe is provided to download the latest supported FVP version.

The recipe is located at `${WORKSPACE}/meta-arm/meta-arm/recipes-devtools/fvp/fvp-corstone1000-a320.bb`.

The latest FVP version is `11.31.cs1000_a320_2` for Corstone-1000 with Cortex-A320, and the model is automatically downloaded and installed when using the `runfvp` command as follows:

```
kas shell meta-arm/kas/corstone1000-a320-fvp.yml:meta-arm/ci/debug.yml \
-c "../meta-arm/scripts/runfvp -- --version"
```

The FVP can also be manually downloaded from [Arm Developer](https://developer.arm.com/downloads/-/arm-ecosystem-fvps) to download the Corstone-1000 with Cortex-A320 FVP package.

To set up the FVP:

1. Run `tmux`:

   ```
   cd ${WORKSPACE} && tmux
   ```

2. Run the FVP within `tmux`:

   ```
   kas shell meta-arm/kas/corstone1000-a320-fvp.yml:meta-arm/ci/debug.yml \
   -c "../meta-arm/scripts/runfvp --terminals=tmux"
   ```

   When the script is executed, three terminal instances will be launched:

   - one for the Secure Enclave processing element
   - two for the Host processor processing element.

   ```
   corstone1000-a320-fvp login:
   ```

3. Log in using the `root` username.

## Security issue reporting {.reference}

To report any security issues identified with Corstone-1000 with Cortex-A320, please send an email to [psirt@arm.com](mailto:psirt@arm.com).
