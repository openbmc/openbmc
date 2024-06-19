# The Trusted Services: framework for developing root-of-trust services

meta-arm layer includes recipes for [Trusted Services][^1] Secure Partitions and Normal World applications
in `meta-arm/recipes-security/trusted-services`

## Secure Partitions recipes

We define dedicated recipes for all supported Trusted Services (TS) Secure Partitions.
These recipes produce ELF and DTB files for SPs.
These files are automatically included into optee-os image accordingly to defined MACHINE_FEATURES.

### How to include TS SPs

To include TS SPs into optee-os image you need to add into MACHINE_FEATURES
features for each [Secure Partition][^2] you would like to include:

| Secure Partition  | MACHINE_FEATURE |
| ----------------- | --------------- |
| Attestation       | ts-attesation   |
| Crypto            | ts-crypto       |
| Firmware Update   | ts-fwu
| Internal Storage  | ts-its          |
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


## Normal World applications

Optionally for testing purposes you can add `packagegroup-ts-tests` into your image. It includes
[Trusted Services test and demo tools][^3] and [xtest][^4] configured to include the `ffa_spmc` tests.

## OEQA Trusted Services tests

  meta-arm also includes Trusted Service OEQA tests which can be used for automated testing.
See `ci/trusted-services.yml` for an example how to include them into an image.


------
[^1]: https://trusted-services.readthedocs.io/en/integration/overview/index.html

[^2]: https://trusted-services.readthedocs.io/en/integration/deployments/secure-partitions.html

[^3]: https://trusted-services.readthedocs.io/en/integration/deployments/test-executables.html

[^4]: https://optee.readthedocs.io/en/latest/building/gits/optee_test.html
