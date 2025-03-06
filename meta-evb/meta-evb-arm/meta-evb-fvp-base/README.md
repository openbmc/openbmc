# meta-fvp-base

This layer contains a reference implementation of OpenBMC for Armv-A Base RevC AEM FVP.

The diagram below illustrates this setup. The Base FVP represents the management controller.
The Neoverse FVP represents the server host SoC.
The management controller communicates with the following components of the server SoC:

- With the Manageability Control Processor (MCP): Using PLDM over MCTP over UART.
- With the Application Processor (AP): Using IPMI In-band (UART) interface.

## Diagram


```
                                                                           AP debug console
                                                                                 |
         +--------------------------+                                  +-------------------------+
         |         Base FVP         |                                  |  Neoverse RD-V3-R1 FVP  |
         |                          |                                  |                         |
         |                          |                                  |     ______________      |
         |                          |                                  |    |              |     |
         |             /dev/ttyAMA2 |--------- IPMI over UART ---------|----|      AP      |     |
         |                          |                                  |    |______________|     |
         |                          |                                  |                         |
         |                          |                                  |                         |
         |                          |             PLDM over            |                         |
         |                          |             MCTP over            +-------+         +-------+
Redfish--|             /dev/ttyAMA1 |-------------- UART --------------|  MCP  |         |  SCP  |
         +--------------------------+ (terminal_1)        (terminal_0) +-------+---------+-------+
                      |                                                    |                |
               FVP debug console                                           |             debug console
                 (terminal_0)                                              |             (terminal_uart_scp)
                                                                     debug console
                                                                     (terminal_uart_mcp)
```

## Features

- The IPMI in-band interface runs over a UART connection between the AP and the BMC, and can be accessed in UEFI as well as the OS.
- The MCP exposes a temperature sensor which our image then exposes over redfish
    - pldmd should automatically pick up this sensor and expose it on dbus
- The MCP has a PLDM Event which can be retrieved by pldmd upon using ```pldm event``` command from MCP debug console

## Setup

1. Clone the OpenBMC repository and build the FVP
    ```sh
    git clone https://github.com/openbmc/openbmc.git
    cd openbmc

    source setup fvp
    bitbake obmc-phosphor-image
    ```
2. Source the Neoverse Reference Design FVP (RD-V3-R1) from the referenced link below.
    - Link: https://developer.arm.com/Tools%20and%20Software/Fixed%20Virtual%20Platforms/Infrastructure%20FVPs#v3-r1
3. Extract the ```.tgz``` and store in your local environment.
    - This binary will be used in step 3, make sure to export this as MODEL before launching the model
4. Navigate to the Neoverse Reference Design Docs and Follow the Getting Started user guide.
    - Use the platform ```rdv3r1```
    - Use the manifest titled ```pinned-rdv3r1-bmc.xml```
    - Use the latest release tag of ```RD-INFRA-2025.01.29```
    - Follow the Buildroot Boot steps in order to build the software stack and boot the FVP

## Usage

1. Start Base FVP with OpenBMC Image
    - ```./meta-arm/scripts/runfvp build/fvp/tmp/deploy/images/fvp/obmc-phosphor-image-fvp.fvpconf```
    - The serial /dev/ttyAMA1 will be automatically configured
    - pldmd will find ```/usr/share/pldm/host_eid``` which is hardcoded as 18
    - pldmd will start communicating with the MCP once it's ready
2. Start Neoverse RD-V3-R1 FVP
    - ```cd model-scripts/rdinfra; ./boot-buildroot.sh -p rdv3r1```
    - Can observe SCP FW logs on MCP debug console
    - Can enter MCP Debug Prompt by pressing Ctrl+e on MCP debug console
3. Connect the UART of MCP to Base FVP with
   ```socat -x tcp:localhost:5065 tcp:localhost:5165```
   - The port numbers are just examples. They can be hardcoded in the FVP config. Otherwise, the FVP will assign them dynamically
   - ```-x``` tells socat to print the bytes being transferred
4. Connect the UART of AP to Base FVP with
   ```socat -x tcp:localhost:5066 tcp:localhost:5166```
   - The port numbers are just examples. They can be hardcoded in the FVP config. Otherwise, the FVP will assign them dynamically
   - ```-x``` tells socat to print the bytes being transferred
   - The AP debug console will show the BMC IP address and subnet mask
      - During boot, the host sends IPMI commands to get the BMC IP address and subnet mask
5. Query Redfish Sensor and Event
   - ```curl --insecure -u root:0penBmc -X GET https://127.0.0.1:4223/redfish/v1/Chassis/PLDM_Device_1/Thermal```
   - ```curl --insecure -u root:0penBmc -X GET https://127.0.0.1:4223/redfish/v1/Systems/system/LogServices/PldmEvent/Entries/```

## Known Issues
- Because both FVP are running independently, there can be an issue with timeout.
  That's why a large timeout was configured for pldmd.

## References

- Neoverse System Architecture https://developer.arm.com/documentation/107734/0002/Technical-overview?lang=en
- Neoverse Reference SW https://neoverse-reference-design.docs.arm.com/en/latest/features/bmc.html
- Neoverse FVP Download https://developer.arm.com/Tools%20and%20Software/Fixed%20Virtual%20Platforms/Infrastructure%20FVPs#v3-r1
- Base FVP Download https://developer.arm.com/Tools%20and%20Software/Fixed%20Virtual%20Platforms
- PLDM + MCTP Specifications https://www.dmtf.org/standards/pmci
