# meta-fvp-base

This layer contains a reference implementation of OpenBMC for Armv-A Base RevC AEM FVP.

The image can be used for the following demonstration setup
where the Base FVP represents the management controller which is communicating
with the Manageability Control Processor (MCP) over PLDM over MCTP over UART.

## Diagram


```
                                                                           AP debug console
                                                                                 |
                                                                       +-----------------------+
         +--------------------------+                                  |  Neoverse RD-N2 FVP   |
         |         Base FVP         |             PLDM over            |                       |
         |                          |             MCTP over            +-------+       +-------+
redfish--|             /dev/ttyAMA1 |-------------- UART --------------|  MCP  |       |  SCP  |
         +--------------------------+ (terminal_1)        (terminal_0) +-------+-------+-------+
                      |                                                    |                |
               FVP debug console                                           |             debug console
                 (terminal_0)                                              |             (terminal_uart_scp)
                                                                     debug console
                                                                     (terminal_uart_mcp)
```

## Features

- The MCP exposes a temperature sensor which our image then exposes over redfish
    - pldmd should automatically pick up this sensor and expose it on dbus
- The MCP has a PLDM Event which can be retrieved by pldmd upon using ```pldm event``` command from MCP debug console

## Usage

1. Start Base FVP with OpenBMC Image
    - ```./meta-arm/scripts/runfvp build/fvp/tmp/deploy/images/fvp/obmc-phosphor-image-fvp.fvpconf```
    - The serial /dev/ttyAMA1 will be automatically configured
    - pldmd will find ```/usr/share/pldm/host_eid``` which is hardcoded as 18
    - pldmd will start communicating with the MCP once it's ready
2. Start Neoverse RD-N2 FVP
    - Can observe SCP FW logs on MCP debug console
    - Can enter MCP Debug Prompt by pressing Ctrl+e on MCP debug console
3. Connect the UART's of MCP to Base FVP with
   ```socat -x tcp:localhost:6005 tcp:localhost:5065```
   - The port numbers are just examples, they can be hardcoded in fvp config, otherwise the fvp's will assign them dynamically
   - ```-x``` tells socat to print the bytes being transferred
4. Query Redfish Sensor and Event
   - ```curl --insecure -u root:0penBmc -X GET https://127.0.0.1:4223/redfish/v1/Chassis/PLDM_Device_1/Thermal```
   - ```curl --insecure -u root:0penBmc -X GET https://127.0.0.1:4223/redfish/v1/Systems/system/LogServices/PldmEvent/Entries/```

## Known Issues
- Because both FVP are running independently, there can be an issue with timeout.
  That's why a large timeout was configured for pldmd.

## References

- SCP FW for MCP https://gitlab.arm.com/infra-solutions/reference-design/platsw/scp-firmware/-/tree/refinfra-satmc?ref_type=heads
- Base FVP Download https://developer.arm.com/Tools%20and%20Software/Fixed%20Virtual%20Platforms
- Neoverse FVP Download https://developer.arm.com/downloads/-/arm-ecosystem-fvps
- PLDM + MCTP Specifications https://www.dmtf.org/standards/pmci
- Neoverse System Architecture https://developer.arm.com/documentation/102099/0003/Technical-overview?lang=en
- Other FVP SW Docs https://neoverse-reference-design.docs.arm.com/en/latest/index.html
