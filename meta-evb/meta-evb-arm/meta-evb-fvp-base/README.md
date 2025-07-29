# meta-evb-fvp-base

This layer contains a reference implementation of OpenBMC for Armv-A Base RevC AEM FVP. FVPs (Fixed Virtual Platforms) are a complete simulations of an ARM system, including processor, memory and peripherals.

## Diagram

The diagram below illustrates this setup.
- The Neoverse FVP represents the server host SoC.
- The Base FVP represents the management controller.
- The management controller communicates with the following components of the server SoC:
    - With the Manageability Control Processor (MCP): Using PLDM over MCTP over UART.
    - With the Application Processor (AP): Using IPMI In-band (UART) interface.

```
                                                                           AP debug console
                                                                                 |
         +--------------------------+                                  +-------------------------+
         |         Base FVP         |                                  |  Neoverse RD-V3-R1 FVP  |
         |                          |                                  |     ______________      |
         |                          |                                  |    |              |     |
    SOL--|------------ /dev/ttyAMA3 |------- Host Serial Console ------|----|              |     |
         |                          | (terminal_3) (terminal_ns_uart0) |    |              |     |
         |                          |                                  |    |      AP      |     |
         |                          |                                  |    |              |     |
         |             /dev/ttyAMA2 |--------- IPMI over UART ---------|----|              |     |
         |                          | (terminal_2)        (terminal_3) |    |______________|     |
         |                          |                                  |                         |
         |                          |                                  |                         |
         |                          |             PLDM over            |                         |
         |                          |             MCTP over            +-------+         +-------+
Redfish--|             /dev/ttyAMA1 |-------------- UART --------------|       |         |       |
         |                          | (terminal_1)        (terminal_2) |  MCP  |         |  SCP  |
         | /dev/ttyAMA0             |                                  |       |         |       |
         +--------------------------+                                  +-------+---------+-------+
                 |                                                         |                |
          FVP debug console                                                |             debug console
          (terminal_0)                                                     |             (terminal_uart_scp)
                                                                     debug console
                                                                     (terminal_uart_mcp)
```

## Features

- The IPMI in-band interface runs over a UART connection between the AP and the BMC, and can be accessed in UEFI as well as the OS.
- The MCP exposes a temperature sensor which our image then exposes over redfish
    - pldmd should automatically pick up this sensor and expose it on dbus
- The MCP has a PLDM Event which can be retrieved by pldmd upon using ```pldm event``` command from MCP debug console
- Host serial console access in BMC (SOL).

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
    - Make sure to export this as MODEL before launching the model, ```export MODEL=<absolute path to the platform FVP binary>
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
3. Connect the UART of Neoverse FVP to Base FVP with `socat` command as required.
    - The port numbers mentioned in this document (for the `socat` command) are just examples.
    - They can be hardcoded in the FVP config. Otherwise, the FVP will assign them dynamically.
    - The `-x` option tells `socat` to print the bytes being transferred

### In-band Communication Channel (IPMI)

1. Connect the UART of AP (terminal_3) to Base FVP (terminal_2) with
    - ```socat -x tcp:localhost:5066 tcp:localhost:5166```
    - The AP debug console will show the BMC IP address and subnet mask
    - During boot, the host sends IPMI commands to get the BMC IP address and subnet mask

### Side-band Communication Channel (PLDM)

1. Connect the UART of MCP (terminal_2) to Base FVP (terminal_1) with
    - ```socat -x tcp:localhost:5065 tcp:localhost:5165```
2. In BMC debug console execute the following command to restart MCTP discovery process
    - ```systemctl restart mctpd.service```
3. Query Redfish Sensor and Event
    - ```curl --insecure -u root:0penBmc -X GET https://127.0.0.1:4223/redfish/v1/Chassis/SatMC/Thermal```
    - ```curl --insecure -u root:0penBmc -X GET https://127.0.0.1:4223/redfish/v1/Managers/bmc/LogServices/FaultLog/Entries/```

### Out-of-band Communication Channel (SOL)

1. Connect host console (terminal_ns_uart0) to BMC (terminal_3) with
    - ```socat -x tcp:localhost:5005 tcp:localhost:5067```
2. In BMC debug console execute following command to update host state as running.
    - ```busctl set-property xyz.openbmc_project.State.Host /xyz/openbmc_project/state/host0 xyz.openbmc_project.State.Host CurrentHostState s xyz.openbmc_project.State.Host.HostState.Running```
3. Log-in to BMC webui (Access via ```https://127.0.0.1:4223```).
4. In the Overview page click the ```SOL console``` button to access host serial console.

## Known Issues

- Because both FVP are running independently, there can be an issue with timeout. That's why a large timeout was configured for pldmd.

## References

- Neoverse System Architecture https://developer.arm.com/documentation/107734/0002/Technical-overview?lang=en
- Neoverse Reference SW https://neoverse-reference-design.docs.arm.com/en/latest/features/bmc.html
- Neoverse FVP Download https://developer.arm.com/Tools%20and%20Software/Fixed%20Virtual%20Platforms/Infrastructure%20FVPs#v3-r1
- Base FVP Download https://developer.arm.com/Tools%20and%20Software/Fixed%20Virtual%20Platforms
- PLDM + MCTP Specifications https://www.dmtf.org/standards/pmci
