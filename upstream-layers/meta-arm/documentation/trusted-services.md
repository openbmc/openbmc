# The Trusted Services: framework for developing root-of-trust services

meta-arm layer includes recipes for [Trusted Services][^1] Secure Partitions and Normal World applications
in `meta-arm/recipes-security/trusted-services`

## Secure Partitions recipes

We define dedicated recipes for all supported Trusted Services (TS) Secure Partitions, which produce executables and
manifest binaries (DT files) for SPs.

The Secure Partitions are compatible with any SPMC implementation that complies with the FF-A specification. Meta-arm
currently supports OP-TEE SPMC, and when enabled, the Secure Partition binaries are automatically included in the
optee-os image based on the defined MACHINE_FEATURES. For more details bout OP-TEE SPMC please refer to the
[OP-TEE documentation][^6].

### How to include TS SPs

To include TS SPs into the firmware image add the corresponding feature flags to the MACHINE_FEATURES variable for each
[Secure Partition][^2] you would like to include :

| Secure Partition  | MACHINE_FEATURE |
| ----------------- | --------------- |
| Attestation       | ts-attesation   |
| Crypto            | ts-crypto       |
| Firmware Update   | ts-fwu          |
| fTPM              | ts-ftpm         |
| Internal Storage  | ts-its          |
| Logging           | ts-logging      |
| Protected Storage | ts-storage      |
| se-proxy          | ts-se-proxy     |
| smm-gateway       | ts-smm-gateway  |
| spm-test[1-4]     | optee-spmc-test |

Other steps depend on your machine/platform definition:

1. For communications between Secure and Normal Words Linux kernel option `CONFIG_ARM_FFA_TRANSPORT=y`
   is required. If your platform doesn't include it already you can add `arm-ffa` into MACHINE_FEATURES.
   (Please see ` meta-arm/recipes-kernel/arm-tstee`.)

   For running the `uefi-test` or the `xtest -t ffa_spmc` tests under Linux the `arm-ffa-user` drivel is required. This is
   enabled if the `ts-smm-gateway` and/or the `optee-spmc-test` machine features are enabled.
   (Please see ` meta-arm/recipes-kernel/arm-ffa-user`.)

2. optee-os might require platform specific OP-TEE build parameters (for example what SEL the SPM Core is implemented at).
   You can find examples in `meta-arm/recipes-security/optee/optee-os_%.bbappend` for qemuarm64-secureboot machine
   and in `meta-arm-bsp/recipes-security/optee/optee-os-corstone1000-common.inc` for the Corstone1000 platform.

3. trusted-firmware-a might require platform specific TF-A build parameters (SPD and SPMC details on the platform).
   See `meta-arm/recipes-bsp/trusted-firmware-a/trusted-firmware-a_%.bbappend` for qemuarm64-secureboot machine
   and in `meta-arm-bsp/recipes-bsp/trusted-firmware-a/trusted-firmware-a-corstone1000.inc` for theCorstone1000 platform.

4. Trusted Services supports an SPMC agonistic binary format. To build SPs to this format the `TS_ENV` variable is to be
   set to `sp`. The resulting SP binaries should be able to boot under any FF-A v1.1 compliant SPMC implementation.

### Example configurations

The `meta-arm/ci` directory contains various TS focused [KAS][^7] configuration files:

| File name | Description |
|-----------|-------------|
| ci/fvp-base-ts-ftpm.yml        |Enabling the fTPM SP on the fvp-base machine|
| ci/fvp-base-ts.yml             |TS config for the fvp-base machine|
| ci/qemuarm64-secureboot-ts.yml |TS config for quemuarm64-secureboot machine|

## Normal World applications

Optionally for testing purposes you can add `packagegroup-ts-tests` into your image. It includes
[Trusted Services test and demo tools][^3] and [xtest][^4] configured to include the `ffa_spmc` tests.

## OEQA Trusted Services tests

meta-arm also includes Trusted Service OEQA tests which can be used for automated testing.
See `ci/trusted-services.yml` for an example how to include them into an image.

## Configuration options

Some TS recipes support yocto variables to set build configuration. These variables can be set in .conf files (machine
specific or local.conf), or .bbappend files. 

### SmmGW SP

The recipe supports the following configuration variables

| Variable name         | Type | Description                                                                                            |
|-----------------------|------|--------------------------------------------------------------------------------------------------------|
| SMMGW_AUTH_VAR        | Bool | Enable Authenticated variable support                                                                  |
| SMMGW_INTERNAL_CRYPTO | Bool | Use MbedTLS build into SmmGW for authentication related crypto operations. Depends on SMMGW_AUTH_VAR=1 |

fTPM tests are supported by OEQA but are disabled by default due to their lengthy execution time. To enable them, set the RUN_TPM2_TESTS
variable e.g. in local.conf.

The list of supported test cases can be found in the `tests` array in the `meta-arm/recipes-tpm/tpm2-tools/files/tpm2-test-all` script.
These can be ran one-by-one, but currently running all of them by calling `tpm2-test-all` results in a failure of the `tpm2-abmrd` tool.

The tests not supported are listed in the same script under the `Failing tests:` line.

This script was created to meet the needs of the Trusted Services project, but in the future it should be updated to be configurable to
support generic usage. The aforementioned issue shall also be solved, so the supported tests could run together.

### fTPM SP

The fTPM SP is an experimental feature. Please refer to the [TS documentation][^5] for details on limitations. 

The current integration enables the fTPM Secure Partition and supports running tpm2-tools tests to verify correct
functionality. Secure Boot and other features that leverage TPM capabilities are not enabled currently.

Configuration variables of the recipe:

| Variable name         | Type | Description                                                                                            |
|-----------------------|------|--------------------------------------------------------------------------------------------------------|
| RUN_TPM2_TESTS        | Bool | Enable automatic execution of TPM tests from OEQA to verify the TS fTPM SP                             |

The current integration targeting the fvp-base machine enables fTPM SP and allows running the tests. To reproduce the
build please use `ci/fvp-base-ts-ftpm.yml`. This configuration:

   - deploys the SP in the SWd
   - amends the Linux kernel configuration:
      - enables the tpm-crb driver
      - add a patch to allow DTB based discovery
   - deploys user-space components (tpm2-tss, tpm2-abrmd, tmp2-tools)
   - configures the initialization system to start tpm2-abrmd.

The configuration leverages tpm2 components form meta-secure-core layer. 

Validation can be performed by running the script located at `meta-arm/recipes-tpm/tpm2-tools/files/tpm2-test-all`. This
script runs a subset of tpm2 tests. While all tpm2 test pass when executed individually, executing the entire test suite
in a sequence leads to a failure of the `tpm2-abmrd` tool. As a workaround some test cases are disabled in the script.
You can find the list of disabled tests marked under the `Failing tests` section of the script.

Note: tpm2 tests was designed to validate the tpm2 reference stack. Its use for verifying the fTPM SP is not fully
aligned with this intent. As such, the current validation approach is considered “best effort” and is suitable for
development purposes. A more appropriate and comprehensive test suite should be selected for future validation.

------
[^1]: https://trusted-services.readthedocs.io/en/integration/overview/index.html

[^2]: https://trusted-services.readthedocs.io/en/integration/deployments/secure-partitions.html

[^3]: https://trusted-services.readthedocs.io/en/integration/deployments/test-executables.html

[^4]: https://optee.readthedocs.io/en/latest/building/gits/optee_test.html

[^5]: https://trusted-services.readthedocs.io/en/integration/services/tpm-service-description.html

[^6]: https://optee.readthedocs.io/en/latest/architecture/spmc.html

[^7]: https://kas.readthedocs.io
