# Release notes {.chapter permissions=non-confidential}

You expressly assume all liabilities and risks relating to your use or operation
of your software and hardware designed or modified using the Arm Tools,
including without limitation, your software or hardware designed or
intended for safety-critical applications. Should your software or hardware
prove defective, you assume the entire cost of all necessary servicing, repair
or correction.

## Release notes - 2025.12 {.reference}

The following knowns issues and limitations are present in this release:

- Corstone-1000 with Cortex-A320 FVP does not currently support Symmetric Multiprocessing
- Corstone-1000 with Cortex-A320 FVP becomes unresponsive when the Linux kernel driver for the Ethos-U85 NPU loads automatically after a software reboot.
- Crypto isolation is not supported in the Secure world of Corstone-1000. Additionally, clients in
  the Normal world are not isolated from one another. Therefore, if an end user wants to add a new
  Secure Partition (SP) (such as a software TPM) that accesses the Crypto service via the SE-Proxy,
  they are responsible for implementing their own isolation mechanisms to ensure proper security boundaries.
- DSTREAM debug probe may experience unreliable USB connectivity when used with Arm DS for secure debug.
  This issue is under active investigation, and we are working to identify and resolve compatibility issues in a future update.
  As a more stable alternative, the ULINKpro debug probe is recommended for use with Corstone-1000 in secure debug scenarios.

## Support {.reference}

For technical support, email [Arm subsystem support](mailto:support-subsystem-iot@arm.com).

For security issues, contact [Arm Security](mailto:psirt@arm.com).
