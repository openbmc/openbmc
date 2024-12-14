# meta-arm-systemready Yocto Layer

This layer contains classes and recipes for building and running
[Arm SystemReady][] validation on the supported machines.

Information regarding contributing, reporting bugs, etc can be found in the
top-level meta-arm [README.md](../README.md) file.

## Introduction

This layer provides support for the following on supported machines:

* Building the firmware for the [Arm SystemReady][] certification program
* Running [Arm SystemReady ACS][] (Architecture Compliance Suite) tests
* Running Linux distributions installation tests

This layer is designed to work alongside with a BSP layer. For example, the
[`meta-arm-bsp`][] layer in the top-level meta-arm defines the
[`fvp-base`][] machine, which can be used with the recipes provided by this
layer to build the firmware, run the [Arm SystemReady IR][] ACS tests, and
run Linux distributions installation. **Note** that users can use this layer
with their BSP layer to perform the same build and tests.

### Firmware Build

The `arm-systemready-firmware.bb` recipe is to build the firmware. It requires
the `ARM_SYSTEMREADY_FIRMWARE` variable set at the MACHINE configuration level
to list the build dependencies.

### ACS Tests

The recipe to run the ACS tests fetches and deploys the prebuilt ACS test suite
disk image, and generates the necessary metadata to support executing the test
suite using the bitbake `testimage` task. A test case `SystemReadyACSTest` is
introduced in `lib/oeqa/runtime/cases` to monitor the ACS tests output from the
bitbake `testimage` task. The `ARM_SYSTEMREADY_ACS_CONSOLE` variable must be set
at the MACHINE configuration level for this test case.

There are two additional recipes for generating the Arm SystemReady ACS tests
report files through the use of the [EDK2 SCT Results Parser][] and the
[Arm SystemReady scripts][]. These packages are installed in the
`edk2-test-parser-native.bb` and `arm-systemready-scripts-native.bb` recipes
respectively.

The class `arm-systemready-acs.bbclass` implements the common logic to deploy
the Arm SystemReady ACS prebuilt image and set up the `testimage` environment.
This class also contains a `testimage` `"postfunc"` called `acs_logs_handle`
which generates report files and analyzes the test results.

The test result analysis is performed by first using the EDK2 SCT Results
Parser to create a results.md file, then running the Arm SystemReady result
check script to check the contents of the result partition as well as the
console log. The result check will fail if any of the expected files or
directories are missing, or if any file's contents do not pass its file-specific
checks.

The ACS test results which are checked by the script can be viewed in
`${TMPDIR}/work/aarch64-oe-linux/arm-systemready-ir-acs/2.0.0-r0/testimage/`. If
the check fails, the bitbake `testimage` task will fail.


### Linux Distributions Installation

Recipes for testing the installation of Linux distributions are provided under
`recipes-test/arm-systemready-linux-distros`. These recipes help to download the
installation CD for the Linux distribution and generate an empty disk as the
target disk for the installation.

## Supported Band and Machine

Arm SystemReady has four bands:
* [Arm SystemReady SR][]
* [Arm SystemReady ES][]
* [Arm SystemReady IR][]
* [Arm SystemReady LS][]

Currently, this layer only supports
[Arm SystemReady IR ACS version v23.03_2.0.0][], which is verified on the
[`fvp-base`][] machine.

## Build and Run

To build the firmware for Arm SystemReady on the supported machines (take the
`fvp-base` machine as an example):

    ARM_FVP_EULA_ACCEPT=1 kas build kas/fvp-base.yml:kas/arm-systemready-firmware.yml


To run the Arm SystemReady ACS tests on the supported machines (take running
Arm SystemReady IR on the `fvp-base` machine as an example):

    ARM_FVP_EULA_ACCEPT=1 kas build kas/fvp-base.yml:kas/arm-systemready-ir-acs.yml

To run the Linux distributions installation on the supported machines (take
installing openSUSE on the `fvp-base` machine as an example):

    ARM_FVP_EULA_ACCEPT=1 kas build kas/fvp-base.yml:kas/arm-systemready-linux-distros-opensuse.yml

    kas shell \
        kas/fvp-base.yml:kas/arm-systemready-linux-distros-opensuse.yml \
        -c "../scripts/runfvp --verbose --console"

## Guidelines for Reusing and Extending

Currently, this layer only supports the Arm SystemReady IR band running on the
`fvp-base` machine defined in the `meta-arm-bsp` layer. The supported Arm
SystemReady IR implementation can be reused on other machines. Furthermore, the
current implementation can be further extended to support SR, ES and LS bands.

### Reuse

To reuse the supported Arm SystemReady IR on other machines, you will need to:

1. Set the `ARM_SYSTEMREADY_FIRMWARE` variable at the MACHINE configuration
   level to list the build dependencies. The configuration file of the
   [`fvp-base`][] machine can be used as a reference.
2. Set the `ARM_SYSTEMREADY_ACS_CONSOLE` variable at the MACHINE configuration
   level for running the ACS tests in the bitbake `testimage` task. Also refer
   to the configuration file of the [`fvp-base`][] machine.

### Extend

To extend support for other bands, you will need to:

1. Add a new recipe to inherit `arm-systemready-acs.bbclass`. You can use
   [`arm-systemready-ir-acs.bb`][] as a reference.
2. Add a new `testimage` test case for the newly added band. Refer to
   [`arm_systemready_ir_acs.py`][].
3. Set the necessary variables and prepare the ACS baseline files (as listed in
   the above **Reuse** section) at the MACHINE configuration level from the BSP
   layer for the machine to be supported.

**Note**: When reusing and extending, the current classes and libs may need to
be modified or refactored as necessary.

[Arm SystemReady]: https://www.arm.com/architecture/system-architectures/systemready-certification-program
[Arm SystemReady ACS]: https://github.com/ARM-software/arm-systemready
[Arm SystemReady SR]: https://www.arm.com/architecture/system-architectures/systemready-certification-program/sr
[Arm SystemReady ES]: https://www.arm.com/architecture/system-architectures/systemready-certification-program/es
[Arm SystemReady IR]: https://www.arm.com/architecture/system-architectures/systemready-certification-program/ir
[Arm SystemReady LS]: https://www.arm.com/architecture/system-architectures/systemready-certification-program/ls
[Arm SystemReady IR ACS version v23.03_2.0.0]: https://github.com/ARM-software/arm-systemready/tree/main/IR/prebuilt_images/v23.03_2.0.0
[Arm SystemReady scripts]: https://gitlab.arm.com/systemready/systemready-scripts
[EDK2 SCT Results Parser]: https://gitlab.arm.com/systemready/edk2-test-parser
[`arm-systemready-ir-acs.bb`]: recipes-test/arm-systemready-acs/arm-systemready-ir-acs.bb
[`arm_systemready_ir_acs.py`]: lib/oeqa/runtime/cases/arm_systemready_ir_acs.py
[`meta-arm-bsp`]: ../meta-arm-bsp
[`fvp-base`]: ../meta-arm-bsp/conf/machine/fvp-base.conf
