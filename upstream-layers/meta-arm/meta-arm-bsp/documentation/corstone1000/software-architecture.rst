..
 # Copyright (c) 2022-2026, Arm Limited.
 #
 # SPDX-License-Identifier: MIT

######################
Software Architecture
######################


*****************
Arm Corstone-1000
*****************

Arm Corstone-1000 is a reference solution for IoT devices. It is part of
Total Solution for IoT which consists of hardware and software reference
implementation.

The combination of Corstone-1000 software and hardware reference solution is `PSA Level-2 ready
certified <psa_l2-ready_>`__ as well as `Arm SystemReady Devicetree certified <systemready-ir-certification_>`__.

More information on the Corstone-1000 subsystems product and design can be
found on `Arm Developer <arm-developer-cs1000-website_>`__.

This document explicitly focuses on the software part of the solution and
provides internal details on the software components. The reference
software package of the platform can be retrieved following instructions
present in the user guide document.

***************
Design Overview
***************

The software architecture of Corstone-1000 platform is a reference
implementation of `Platform Security Architecture <psa-certified-website_>`__ which provides
framework to build secure IoT devices.

The base system architecture of the platform is created from three different types of subsystems:

    - Secure Enclave
    - Host System
    - External System

Each subsystem provides different functionality to the overall system on a chip (SoC).


.. image:: images/CorstoneSubsystems.png
   :width: 720
   :alt: CorstoneSubsystems

Secure Enclave
==============

The Secure Enclave boots first on system power on, it provides `PSA Root of Trust (RoT) <psa-certified-website_>`__ and
cryptographic functions. It is based on a Cortex-M0+ processor, CC312 Cryptographic Accelerator and
peripherals such as watchdog and secure flash.

.. image:: images/Corstone1000SecureFlashMPS3.png
   :width: 400
   :alt: Corstone1000SecureFlashMPS3

.. image:: images/Corstone1000SecureFlashFVP.png
   :width: 400
   :alt: Corstone1000SecureFlashFVP

Software running on the Secure Enclave is isolated via hardware for enhanced security.
Communication with the Secure Enclave is achieved using `Message Handling Units (MHUs) <arm-developer-mhu-website_>`__
and shared memory.

Its software components comprises:

    - `Trusted Firmware-M (TF-M) BL1 <trusted-firmware-m-bl1-website_>`__
    - `MCUboot <mcuboot-website_>`__
    - `TrustedFirmware-M <trusted-firmware-m-website_>`__ 

The software design on the Secure Enclave follows `Arm Firmware Framework for M-Profile
processor <arm-fmw-framework-m-profile-pdf_>`__ (FF-M) specification.

Host System
===========

The Host System is based on ARM Cortex-A35 processor with standardized
peripherals to allow booting a Linux-based operating system (OS). The Cortex-A35 has
the `TrustZone <arm-trustzone-for-cortex-a-website_>`__ technology that allows Secure and Non-secure security
states in the processor. 

The boot process follows `Trusted Boot Base Requirements Client <trusted-board-boot-requirements-client-pdf_>`__.
The Host System is taken out of reset by the Secure Enclave system during its final stages of the
initialization.

In the Secure world, the Host System runs:

    - FF-A Secure Partitions (based on `Trusted Services <trusted-services-website_>`__)
    - `OP-TEE OS <op-tee-os-repository_>`__

In the Non-secure World, the Host System runs:

    - `U-Boot <das-u-boot-repository_>`__
    - `Linux kernel <linux-repository_>`__

The software design in the Host System follows `Arm Firmware Framework for Arm A-profile 
<arm-fmw-framework-a-profile-pdf_>`__ (FF-A) specification.
The communication between Non-secure and the Secure world is performed via FF-A messages.

External System
===============

The External System is intended to implement use-case specific functionality.

The system is based on Cortex-M3 and runs `Keil RTX5 <keil-rtx5-website_>`__. 

Communication between the external system and Host (Cortex-A35) can be performed using MHU as transport
mechanism. The current software release supports switching the External System ON and OFF.

The Corstone-1000 architecture is designed to cover a range of
`Power, Performance, and Area (PPA) <ppa-website_>`__ applications, and enable extension
for use-case specific applications, for example, sensors, cloud connectivity, and edge computing.

**************************************
Corstone-1000 with Cortex-A320 Variant
**************************************

This variant of the Corstone-1000 platform replaces the Host System's Cortex-A35 processor
with a Cortex-A320. In this configuration, the optional External System (previously a Cortex-M3)
is replaced by an Arm Ethos-U85 Neural Processing Unit (NPU).
The Ethos-U85 runs in the direct drive configuration, where the Host System is responsible for managing the NPU directly.

.. image:: images/CorstoneA320Subsystems.png
   :width: 720
   :alt: CorstoneA320Subsystems

*****************
Secure Boot Chain
*****************

For the security of a device, it is essential that only authorized
software should run on the device.

The Corstone-1000 boot uses a `Secure boot <arm-developer-secureboot-website_>`__ chain process
where an already authenticated image verifies and loads the following software in the chain.

For the boot chain process to work, the start of the chain should be trusted, forming the
Root of Trust (RoT) of the device. The RoT of the device is immutable in
nature and encoded into the device by the device manufacturer before it
is deployed into the field. 
In Corstone-1000, the content of the ROM and CC312 One Time Programmable (OTP) memory forms the RoT.

Verification of an image can happen either by comparing the computed and stored hashes, or by
checking the signature of the image if the image is signed.

.. image:: images/SecureBootChain.png
   :width: 870
   :alt: SecureBootChain

It is a lengthy chain to boot the software on Corstone-1000.

TF-M BL1_1
==========

On power-up, the Secure Enclave begins execution from TF-M BL1_1, which resides in ROM and serves as
the Root of Trust (RoT) for the device.

TF-M BL1_1 is the immutable bootloader and is responsible for:

- Provisioning the device during the first boot
- Performing hardware initialization
- Verifying the integrity and authenticity of the next stage in the boot chain

At boot time, TF-M BL1_1:

- Copies the TF-M BL1_2 image from OTP to RAM.
- Verifies the integrity of BL1_2 by comparing its computed hash with the hash stored in OTP.

TF-M BL1_2
==========

During provisioning, the TF-M BL1_2 binary, along with its hashes and cryptographic keys, is stored
in One-Time Programmable (OTP) memory.

Once verified, TF-M BL1_2:

- Takes control and verifies the next stage in the boot chain, which is TF-M BL2.
- Computes the hash of the BL2 image and compares it with the BL2 hash stored in OTP to ensure
  integrity before transferring execution to BL2.

.. note::

    The TF-M BL1 design details can be found in the `TF-M design documents <trusted-firmware-m-bl1-website_>`_.

    .. important::

        Corstone-1000 has some differences compared to this design due to memory (OTP/ROM)
        limitations:

        - BL1_1 code size is larger than needed because it handles most of the hardware initialization instead of the BL1_2.
        - BL1_2 cannot be updated during provisioning time because the provisioning bundle that contains its code is located in the ROM.
        - BL1_2 does not use the post-quantum LMS verification.
        - BL2 cannot be updated because it is verified by comparing the computed hash to the hash stored in the OTP.

TF-M BL2
========

In this system, TF-M BL2 refers to MCUBoot.

On the first boot, MCUBoot can provision additional cryptographic keys. It is responsible for authenticating both:

- TF-M (Trusted Firmware-M), and
- The initial bootloader of the Host system, `Trusted Firmware-A (TF-A) BL2 <trusted-firmware-a-bl2-website_>`__

This authentication is done by verifying the digital signatures of the respective images.

MCUBoot performs image verification in the following steps:

#. Load the image from non-volatile memory into RAM.
#. Validate the image's signature using the corresponding public key.

.. note::

   The public key present in the image header is validated by comparing with the hash.
   Depending on the image, the hash of the public key is either stored in the OTP or part
   of the software which is being already verified in the previous stages.


The execution control is passed to TF-M after the verification.
As the runtime executable of the Secure Enclave, TF-M initializes itself before
bringing the Host system out of reset.



Host System Authentication
==========================

The Host system follows the boot standard defined in the `Trusted Board Boot Requirements Client <trusted-board-boot-requirements-client-pdf_>`__
to authenticate the Secure and Non-secure software.

The `Firmware Image Package (FIP) <trusted-firmware-a-fip-guide_>`__ packs bootloader images and
other payloads into a single archive.

.. image:: images/FIPDiagram.png
   :alt: FIPDiagram

The FIP for Corstone-1000 contains:

- Trusted firmware-A BL2
- AP EL3 Runtime firmware, BL31 image
- AP Secure Payload, BL32 image
- AP Normal world firmware -U-boot, BL33 image
- Trusted OS Firmware configuration file used by Trusted OS (BL32), TOS_FW_CONFIG
- Key certificates
- Content certificates


To load and validate TF-A BL2, TF-M BL2 first parses the GUID Partition Table (GPT)
to locate the FIP. It then determines the offset of TF-A BL2 within the FIP.

.. note::

    TF-M does not check the FIP signature, it only checks the TF-A BL2's signature in the FIP.


.. important::

    The implicitly trusted components are:

    - A SHA-256 hash of the Root of Trust Public Key (ROTPK) - 
      For development purposes, a development ROTPK is used and its hash embedded into the TF-A BL2 image.
      This public key is provided by the TF-A source code.
    - TF-A BL2 image - it can be trusted because it has been verified by TF-M BL2 before starting TF-A.


The remaining components in the Chain of Trust (CoT) are either certificates or bootloader images.

Bootloader Authentication
-------------------------

The FIP contains two types of certificates:

- **Content Certificates** - used to store the hash of a bootloader image.
- **Key Certificates** - used to verify public keys used to sign Content Certificates.

The Host system bootloader images are authenticated by computing their hash and comparing it to the corresponding hash found in the Content Certificate.

Certificates Verification
-------------------------

The public keys defined in the Trusted Key Certificate are used to verify the later certificates in
the CoT process. The Trusted Key Certificate is verified with the Root of Trust Public Key.


UEFI Authenticated Variables
----------------------------

For UEFI Secure Boot, authenticated variables can be accessed from the secure flash.
The feature has been integrated in U-Boot, which authenticates the images as per the UEFI
specification before executing them.

***************
Secure Services
***************

Corstone-1000 is unique in offering a secure environment for running trusted workloads.
While the Host system includes TrustZone technology, the platform also features a hardware-isolated
Secure Enclave, specifically designed to execute these secure workloads.

In Corstone-1000, essential Secure Services—such as Cryptography, Protected Storage,
Internal Trusted Storage, and Attestation—are provided through PSA Functional APIs implemented in TF-M.

From the user's perspective, there is no difference when communicating with these services,
whether they run in the Secure Enclave or in the Secure world of the Host system.
The diagram below illustrates the data flow for such calls.


.. image:: images/SecureServices.png
   :width: 930
   :alt: SecureServices

The Secure Enclave Proxy Secure Partition (SE Proxy SP) is a proxy managed by OP-TEE that forwards
Secure Service calls to the Secure Enclave. This communication uses the `RSE communication protocol <https://tf-m-user-guide.trustedfirmware.org/platform/arm/rse/rse_comms.html>`_.
While the protocol supports shared memory and MHU interrupts as a doorbell mechanism between cores,
in Corstone-1000, the entire message is currently transmitted through the MHU channels.
Corstone-1000 implements Isolation Level 2 using the Cortex-M0+ Memory Protection Unit (MPU).

Users can define their own secure services to run either in the Host system's Secure World or in
the Secure Enclave. This choice involves a trade-off between latency and security.
Services running in the Secure Enclave benefit from strong, hardware-enforced isolation,
offering higher security but at the cost of increased latency. In contrast, services running in the
Host Secure World experience lower latency, but rely on TrustZone technology for virtualized isolation,
which offers comparatively less robust security.


**************************
PSA Secure Firmware Update
**************************

The Arm Corstone-1000 platform necessitates a robust, secure, and flexible firmware update mechanism
including partial capsule update to ensure fielded devices can receive critical patches, feature enhancements,
and security fixes without compromising system integrity. To meet these requirements, we have implemented the
Platform Security Architecture (PSA) Firmware Update (FWU) framework on Corstone-1000, leveraging Trusted Firmware-M (TF-M)
for the Secure Enclave, U-Boot as the host-side client on Cortex-A, and the UEFI capsule update mechanism for payload
encapsulation. This design supports both the Fixed Virtual Platform (FVP) and the Field Programmable Gate Array (FPGA)
targets, providing consistent behavior across simulation and silicon-based deployments. The Corstone-1000 supports FWU
which complies with the `Platform Security Firmware Update for the A-profile Arm Architecture <platform-security-fwu-for-a-profile-pdf_>`__
and `PSA Firmware Update IHI 0093 <psa-firmware-update-ihi-0093-api-reference-website_>`__
specifications.

To standardize and streamline capsule creation with multiple FMP payloads, the `EDK2 capsule generation tool <edk2-capsule-generation-tool-repository_>`__
tool has been integrated into the meta-arm Yocto layer for Corstone‑1000. This integration involves defining
build rules for generating UEFI capsules as part of the firmware image build process. Configuration parameters
exposed in the recipe allow developers to specify the number of FMP payloads, target image GUIDs, version numbers etc.
This capsule ensures that all update payloads conform to the UEFI FMP specification and are ready for
validation and delivery by U‑Boot.

The FWU solution for Corstone-1000 is composed of three primary domains:

- Host System
- Trusted Services intermediary
- Secure Enclave

Each domain has distinct responsibilities and communicates through standardized interfaces.


.. image:: images/SystemArchitecturePSAFirmwareUpdate.png
   :width: 690
   :alt: SystemArchitecturePSAFirmwareUpdate

On the host side, U-Boot functions as the FWU client and orchestrates the update process from capsule retrieval to
payload delivery based on `PSA FWU DEN0018 specification <psa-fwu-den0018-specification-website_>`__
via Arm FF-A framework. The Trusted-Services SE Proxy secure partition serves as a gateway between the non-secure host
environment and the Secure Enclave. The `PSA FWU service <ts-psa-fwu-service-website_>`__ running in the Trusted Services
implementation forwards the data to the Secure Enclave via MHU-based PSA calls. Within the Secure Enclave, the PSA FWU
Agent, conforming to `PSA Firmware Update IHI 0093 <psa-firmware-update-ihi-0093-api-reference-website_>`__ specification,
orchestrates the actual flash programming, metadata management, and rollback protection mechanisms. The agent relies on a
bespoke `shim layer <tfm-shim-layer-website_>`__ to abstract hardware‑specific flash operations and bootloader interactions.

As defined in the specification, the external flash is divided into two banks: one bank holds the
currently running images, while the other is used to stage new images.

There are four updatable components: **BL2**, **TF-M**, **the FIP** and **the Kernel Image** (the initramfs bundle).
New images are delivered and accepted in the form of UEFI capsules.


.. image:: images/ExternalFlash.png
   :width: 690
   :alt: ExternalFlash

When a FWU is initiated on Corstone-1000, the following sequence of operations takes place:

#. **Capsule Retrieval and Preparation**

   U-Boot on the host system retrieves the firmware capsule.
   It validates the capsule header and parses the FMP (Firmware Management Protocol) descriptor list to identify the payloads to be updated.

   For each FMP descriptor, U-Boot:

   Splits the firmware payload into 4 KiB chunks.
   Invokes the PSA_FWU_Update API for each chunk, transmitting the buffer address via the FF-A (Firmware Framework for Arm) shared memory interface.

#. **Secure Transmission and Forwarding**

   The PSA Firmware Update (FWU) service, running as part of Trusted Services, receives the chunks through Secure Partition Client (SPC) calls.
   It forwards these chunks to the Secure Enclave using MHU-based PSA calls.

#. **Flashing Within the Secure Enclave**

   Inside the Secure Enclave, the PSA FWU Agent dispatches each chunk to the shim layer.

   The shim layer:

   Erases the corresponding sectors in the non-active flash bank.
   Writes the received firmware chunks at the correct offsets.
   During partial updates, it also copies static partitions from the active bank to the non-active one to maintain consistency.

#. **Finalization and Boot Preparation**

   After all chunks are successfully written:

   The shim updates the firmware manifest and the EFI System Resource Table (ESRT) entries to reflect the new image version.
   This step enables the bootloader to recognize the new firmware for a trial boot.
   The platform then performs an automatic reset, booting into the non-active bank in trial mode.

#. **Trial Boot and Confirmation**

   In trial mode, U-Boot evaluates the new firmware and issues either an accept or reject command using the PSA FWU ABI.
   These commands are sent to the Secure Enclave, instructing the shim to update the firmware metadata accordingly.

#. **Recovery and Fallback Mechanism**

   If the trial boot is successful, the host sends an acknowledgment, transitioning the firmware state from 'trial' to 'regular'.

   If the system fails or becomes unresponsive:

   A watchdog timer triggers a system reset.
   The BL1 firmware in the Secure Enclave detects repeated failures and reverts to the previously known-good flash bank.
   This rollback mechanism ensures the device remains operational and recoverable, even after a failed update.


.. image:: images/SecureFirmwareUpdate.png
   :width: 430
   :alt: SecureFirmwareUpdate



******************************
UEFI Runtime Support in U-Boot
******************************

The implementation of UEFI boot-time and runtime APIs requires persistent variable storage. In
Corstone-1000, UEFI variables are stored using the Protected Storage (PS) service.

The diagram below illustrates the data flow for storing UEFI variables. U-Boot’s UEFI subsystem
communicates with the Secure World using the U-Boot FF-A driver, which interfaces with the `UEFI System Management Mode (SMM) service <trusted-services-uefi-smm-website_>`__.

The SMM service provides support for the UEFI System Management Mode. This support is implemented by the SMM Gateway secure partition.
The SMM service then uses the Proxy Protected Storage (PS) provided by the SE Proxy SP.
These PS calls are forwarded to the Secure Enclave, following the communication path described earlier.


.. image:: images/UEFISupport.png
   :width: 590
   :alt: UEFISupport


**********
References
**********
* `Arm Developer <arm-developer-cs1000-search_>`__
* `Arm Security Architectures <arm-architecture-security-features-platform-security_>`_

--------------

*Copyright (c) 2022-2026, Arm Limited. All rights reserved.*

.. _arm-developer-cs1000-website: https://developer.arm.com/Tools%20and%20Software/Corstone-1000%20Software
.. _arm-developer-cs1000-search: https://developer.arm.com/search#q=corstone-1000
.. _arm-developer-mhu-website: https://developer.arm.com/documentation/ka005129/latest/#:~:text=An%20MHU%20is%20a%20device,that%20a%20message%20is%20available
.. _arm-developer-secureboot-website: https://developer.arm.com/documentation/PRD29-GENC-009492/c/TrustZone-Software-Architecture/Booting-a-secure-system/Secure-boot
.. _arm-architecture-security-features-platform-security: https://www.arm.com/architecture/security-features/platform-security
.. _linux-repository: https://git.kernel.org/pub/scm/linux/kernel/git/stable/linux.git/
.. _arm-trustzone-for-cortex-a-website: https://www.arm.com/technologies/trustzone-for-cortex-a
.. _arm-fmw-framework-a-profile-pdf: https://developer.arm.com/documentation/den0077/latest
.. _arm-fmw-framework-m-profile-pdf: https://developer.arm.com/architectures/Firmware%20Framework%20for%20M-Profile
.. _platform-security-fwu-for-a-profile-pdf: https://developer.arm.com/documentation/den0118/a/
.. _psa-firmware-update-ihi-0093-api-reference-website: https://arm-software.github.io/psa-api/fwu/1.0/api/api.html
.. _edk2-capsule-generation-tool-repository: https://github.com/tianocore/edk2/blob/master/BaseTools/Source/Python/Capsule/GenerateCapsule.py
.. _psa-fwu-den0018-specification-website: https://developer.arm.com/documentation/den0118/latest/
.. _ts-psa-fwu-service-website: https://trusted-services.readthedocs.io/en/stable/services/fwu/psa-fwu-m.html
.. _tfm-shim-layer-website: https://trustedfirmware-m.readthedocs.io/en/latest/design_docs/services/tfm_fwu_service.html#shim-layer-between-fwu-and-bootloader
.. _op-tee-os-repository: https://github.com/OP-TEE/optee_os
.. _psa-certified-website: https://www.psacertified.org/
.. _psa_l2-ready: https://www.psacertified.org/products/corstone-1000/
.. _systemready-ir-certification: https://armkeil.blob.core.windows.net/developer/Files/pdf/certificate-list/arm-systemready-ve-arm-neoverse.pdf
.. _trusted-board-boot-requirements-client-pdf: https://developer.arm.com/documentation/den0006/latest
.. _trusted-firmware-m-website: https://www.trustedfirmware.org/projects/tf-m/
.. _trusted-firmware-m-bl1-website: https://trustedfirmware-m.readthedocs.io/en/latest/design_docs/booting/bl1.html
.. _trusted-firmware-a-bl2-website: https://developer.arm.com/documentation/108028/0000/RD-TC22-software/Software-components/AP-firmware/Trusted-firmware-A-BL2
.. _trusted-firmware-a-fip-guide: https://trustedfirmware-a.readthedocs.io/en/latest/design/firmware-design.html#firmware-image-package-fip
.. _trusted-services-website: https://www.trustedfirmware.org/projects/trusted-services/
.. _trusted-services-uefi-smm-website: https://trusted-services.readthedocs.io/en/integration/services/uefi-smm-services.html#
.. _das-u-boot-repository: https://github.com/u-boot/u-boot.git
.. _keil-rtx5-website: https://developer.arm.com/Tools%20and%20Software/Keil%20MDK/RTX5%20RTOS
.. _ppa-website: https://developer.arm.com/documentation/102738/0100/Power--performance--and-area-analysis
.. _mcuboot-website: https://docs.mcuboot.com/
