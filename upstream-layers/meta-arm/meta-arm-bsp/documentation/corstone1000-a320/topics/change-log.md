# Change log {.chapter permissions=non-confidential}

This document contains a summary of the new features, changes and fixes in each release of the Corstone-1000 with Cortex-A320 software stack.

## Version 2026.05 {.reference}

The following changes are present in this release:

- Continued Corstone-1000 with Cortex-A320 enablement across U-Boot, TF-A, TF-M, OP-TEE, and Linux with the split A320 device tree, GIC-700 support, Ethos-U85 DT alignment, and NPU reset via the external-system controller.
- Enabled and documented FVP SMP builds and platform-agnostic multicore support, including the errata override fixes for Corstone-1000 with Cortex-A320 and removal of the reboot workaround note.
- Split Corstone-1000 with Cortex-A320 FVP support into a dedicated machine configuration and standalone documentation.

### Corstone-1000 with Cortex-A320 components versions {.reference}

The following component versions are available:

Table: Corstone-1000 with Cortex-A320 component versions

+----------------------------------------+-----------------------------------+
| Component                              | Version                           |
+========================================+===================================+
| linux-yocto                            | 6.19                              |
+----------------------------------------+-----------------------------------+
| u-boot                                 | 2025.10                           |
+----------------------------------------+-----------------------------------+
| external-system                        | 0.1.0                             |
+----------------------------------------+-----------------------------------+
| optee-client                           | 4.9.0                             |
+----------------------------------------+-----------------------------------+
| optee-os                               | 4.9.0                             |
+----------------------------------------+-----------------------------------+
| trusted-firmware-a                     | 2.14.1                            |
+----------------------------------------+-----------------------------------+
| trusted-firmware-m                     | 2.2.2                             |
+----------------------------------------+-----------------------------------+
| libts                                  | v1.3.0                            |
+----------------------------------------+-----------------------------------+
| ts-sp-{se-proxy, smm-gateway}          | v1.3.0                            |
+----------------------------------------+-----------------------------------+
| ts-psa-{crypto, iat, its. ps}-api-test | 74dc6646ff                        |
+----------------------------------------+-----------------------------------+

### Yocto distribution components versions {.reference}

The following Yocto distribution components versions are available:

Table: Yocto distribution component versions

+-------------------+------------+
| Component         | Version    |
+===================+============+
| meta-arm          | wrynose    |
+-------------------+------------+
| bitbake           | 22021758e6 |
+-------------------+------------+
| meta-openembedded | 9af4488d46 |
+-------------------+------------+
| openembedded-core | 06dd66e622 |
+-------------------+------------+
| meta-yocto        | 8251bdad5f |
+-------------------+------------+
| meta-secure-core  | 07a99ae241 |
+-------------------+------------+
| busybox           | 1.37.0     |
+-------------------+------------+
| musl              | 1.2.6      |
+-------------------+------------+
| gcc-arm-none-eabi | 15.2.rel1  |
+-------------------+------------+
| gcc-cross-aarch64 | 15.2.0     |
+-------------------+------------+
| openssl           | 3.5.6      |
+-------------------+------------+

## Version 2025.12 {.reference}

The following changes are present in this release:

- Delivered end-to-end Cortex-A320 enablement across U-Boot, TF-A, TF-M, OP-TEE, Yocto machine layers, and documentation, including device-tree updates, MPIDR handling, and FVP model renaming.
- Rolled out the PSA Firmware Update (DEN0118) pipeline: U-Boot capsule parsing, Bootloader Abstraction Layer in TF-M, ESRT exposure, and Trusted Services IPC bridges replacing legacy capsule code.
- Hardened the new firmware update flow with EFI self-tests, metadata restructuring for partial and multi-image acceptance, and RSE-COMMS gating refinements.
- Upgraded key firmware components (TF-A 2.13.0, TF-M 2.2.1, Trusted Services 1.2.0, OP-TEE OS 4.7.0) and introduced targeted test skips plus integer-only build modes to keep validation green.
- Cleaned and renumbered downstream patch series across Trusted Services and TF-M while removing obsolete integrations to align with upstream baselines.
- Refreshed release material and architecture guides to describe the A320 profile, PSA FWU behavior, and updated software stack.
- Added KAS profiles, machine includes, and automated FVP selection logic to streamline developer workflows for the refreshed platform configuration.

### Corstone-1000 with Cortex-A320 components versions {.reference}

The following component versions are available:

Table: Corstone-1000 with Cortex-A320 component versions

+----------------------------------------+-----------------------------------+
| Component                              | Version                           |
+========================================+===================================+
| linux-yocto                            | 6.12.60                           |
+----------------------------------------+-----------------------------------+
| u-boot                                 | 2025.04                           |
+----------------------------------------+-----------------------------------+
| optee-client                           | 4.7.0                             |
+----------------------------------------+-----------------------------------+
| optee-os                               | 4.7.0                             |
+----------------------------------------+-----------------------------------+
| trusted-firmware-a                     | 2.13.0                            |
+----------------------------------------+-----------------------------------+
| trusted-firmware-m                     | 2.2.1                             |
+----------------------------------------+-----------------------------------+
| libts                                  | v1.2.0                            |
+----------------------------------------+-----------------------------------+
| ts-sp-{se-proxy, smm-gateway}          | v1.2.0                            |
+----------------------------------------+-----------------------------------+
| ts-psa-{crypto, iat, its. ps}-api-test | 74dc6646ff                        |
+----------------------------------------+-----------------------------------+

### Yocto distribution components versions {.reference}

The following Yocto distribution components versions are available:


Table: Yocto distribution component versions

+-------------------+------------+
| Component         | Version    |
+===================+============+
| meta-arm          | whinlatter |
+-------------------+------------+
| bitbake           | 0dde1a3ff8 |
+-------------------+------------+
| meta-openembedded | fc0152e434 |
+-------------------+------------+
| openembedded-core | 4bd920ad7d |
+-------------------+------------+
| meta-yocto        | b3b6592635 |
+-------------------+------------+
| meta-secure-core  | 63209fb150 |
+-------------------+------------+
| meta-ethos        | aa2504a32f |
+-------------------+------------+
| meta-sca          | e68f1a9d17 |
+-------------------+------------+
| busybox           | 1.37.0     |
+-------------------+------------+
| musl              | 1.2.5      |
+-------------------+------------+
| gcc-arm-none-eabi | 13.3.rel1  |
+-------------------+------------+
| gcc-cross-aarch64 | 15.2.0     |
+-------------------+------------+
| openssl           | 3.5.4      |
+-------------------+------------+
