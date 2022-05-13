# IPMI message route in OpenBMC

<img align="right" width="40%" src="https://github.com/NTC-CCBG/snapshots/blob/master/openbmc/ipmid_paths.png" />

The IPMI command implementation in OpenBMC is base on D-Bus.
Daemon monitor each owned channel, and pass IPMI command data on D-Bus via 
call D-bus call "execute" provided by [phosphor ipmi host](https://github.com/openbmc/phosphor-host-ipmid).
Once ipmid (phosphor ipmi host) execute method be called,
it will get the registered function implemented in /usr/lib/ipmid-providers/, 
and return response data by execute mapping function.

Here are the interfaces to send IPMI command to BMC which Nuvoton implemented.
* IPMB: [ipmbbridged](https://github.com/openbmc/ipmbbridge)
  - The ipmbbridged not only pass message from IPMB, but also provide D-Bus method
  "sendRequest" for node manager send request to ME.
* SSIF: [ssifbridged](https://github.com/openbmc/ssifbridge)
* KCS:  [kcsbridged](https://github.com/openbmc/kcsbridge)
* Internet: [netipmid](https://github.com/openbmc/phosphor-net-ipmid)
* D-Bus: [ipmitool](http://ipmitool.sourceforge.net/)
  - The ipmitool integrated in OpenBMC does not need send IPMI command via other inteface,
  call ipmid D-Bus method directly.  

Users can choose any inteface to use, and enable all them on.
(Note. KCS and SSIF is conflit inteface, user need choose one of them)
There are few examples below to explain how the message route in OpenBMC implementation.
+ Watchdog in booting up
  - The BIOS can send IPMI command set/reset watchdog via KCS,
    and the daemon kcsbridged will receive the message from sysfs.
    Then by pass message by execute D-Bus method to ipmid. 
    Once the app handler in ipmid get the watchdog request,
    it will perform the IPMI command by setting phosphor watchdog D-Bus property.
+ Power cap
  - User can set a power cap to keep power consumption on BMC Web UI or send IPMI command by tools.
    In this case, dcmi handler in ipmid will find the service which contains
    object path /xyz/openbmc_project/control/host0/power_cap with interface
    xyz.openbmc_project.Control.Power.Cap and try to change the power cap property.
    Node manager handle the property change and make set power cap request to ipmbbridged,
    then ipmbbridged pass message via IPMB to ME.


# IPMI Commands Verified
| Command | KCS | RMCP+ | IPMB |
| :--- | :---: | :---: | :---: |
| **IPM Device Global Commands** |  |  |  |
| Device ID | V | V | V |
| Cold Reset | V | V | V |
| Warm Reset | - | - | - |
| Get Self Test Results | V | V | V |
| Manufacturing Test On | - | - | - |
| Set ACPI Power State | V | V | V |
| Get ACPI Power State | V | V | V |
| Get Device GUID | V | V | V |
| Get NetFn Support | - | - | - |
| Get Command Support | - | - | - |
| Get Command Sub-function Support | - | - | - |
| Get Configurable Commands | - | - | - |
| Get Configurable Command Sub-functions | - | - | - |
| Set Command Enables | - | - | - |
| Get Command Enables | - | - | - |
| Set Command Sub-function Enables | - | - | - |
| Get Command Sub-function Enables | - | - | - |
| Get OEM NetFn IANA Support | - | - | - |
| **BMC Watchdog Timer Commands** |  |  |  |
| Reset Watchdog Timer | V | V | V |
| Set Watchdog Timer | V | V | V |
| Get Watchdog Timer | V | V | V |
| **BMC Device and Messaging Commands** |  |  |  |
| Set BMC Global Enables | V | V | V |
| Get BMC Global Enables | V | V | V |
| Clear Message Flags | - | - | - |
| Get Message Flags | V | V | V |
| Enable Message Channel Receive | - | - | - |
| Get Message | - | - | - |
| Send Message | - | - | - |
| Read Event Message Buffer | V | V | V |
| Get BT Interface Capabilities | V | V | V |
| Get System GUID | V | V | V |
| Set System Info Parameters | V | V | V |
| Get System Info Parameters | V | V | V |
| Get Channel Authentication Capabilities | V | V | V |
| Get Session Challenge | - | - | - |
| Activate Session | - | - | - |
| Set Session Privilege Level | - | - | - |
| Close Session | - | - | - |
| Get Session Info | - | - | - |
| Get AuthCode | - | - | - |
| Set Channel Access | V | V | V |
| Get Channel Access | V | V | V |
| Get Channel Info Command | V | V | V |
| User Access Command | V | V | V |
| Get User Access Command | V | V | V |
| Set User Name | V | V | V |
| Get User Name Command | V | V | V |
| Set User Password Command | V | V | V |
| Activate Payload | - | V | - |
| Deactivate Payload | - | V | - |
| Get Payload Activation Status | - | V | - |
| Get Payload Instance Info | - | V | - |
| Set User Payload Access | V | V | V |
| Get User Payload Access | V | V | V |
| Get Channel Payload Support | V | V | V |
| Get Channel Payload Version | V | V | V |
| Get Channel OEM Payload Info | - | - | - |
| Master Write-Read | V | V | V |
| Get Channel Cipher Suites | V | V| V |
| Suspend/Resume Payload Encryption | - | - | - |
| Set Channel Security Keys | - | - | - |
| Get System Interface Capabilities | - | - | - |
| Firmware Firewall Configuration | - | - | - |
| **Chassis Device Commands** |  |  |  |
| Get Chassis Capabilities | V | V | V |
| Get Chassis Status | V | V | V |
| Chassis Control | V | V | V |
| Chassis Reset | - | - | - |
| Chassis Identify | V | V | V |
| Set Front Panel Button Enables | - | - | - |
| Set Chassis Capabilities | V | V | V |
| Set Power Restore Policy | V | V | V |
| Set Power Cycle Interval | - | - | - |
| Get System Restart Cause | V | V | V |
| Set System Boot Options | V | V | V |
| Get System Boot Options | V | V | V |
| Get POH Counter | V | V | V |
| **Event Commands** |  |  |  |
| Set Event Receiver | - | - | - |
| Get Event Receiver | - | - | - |
| Platform Event | V | V | V |
| **PEF and Alerting Commands** |  |  |  |
| Get PEF Capabilities | - | - | - |
| Arm PEF Postpone Timer | - | - | - |
| Set PEF Configuration Parameters | - | - | - |
| Get PEF Configuration Parameters | - | - | - |
| Set Last Processed Event ID | - | - | - |
| Get Last Processed Event ID | - | - | - |
| Alert Immediate | - | - | - |
| PET Acknowledge | - | - | - |
| **Sensor Device Commands** |  |  |  |
| Get Device SDR Info | V | V | V |
| Get Device SDR | V | V | V |
| Reserve Device SDR Repository | V | V | V |
| Get Sensor Reading Factors | - | - | - |
| Set Sensor Hysteresis | - | - | - |
| Get Sensor Hysteresis | - | - | - |
| Set Sensor Threshold | - | - | - |
| Get Sensor Threshold | V | V | V |
| Set Sensor Event Enable | - | - | - |
| Get Sensor Event Enable | - | - | - |
| Re-arm Sensor Events | - | - | - |
| Get Sensor Event Status | - | - | - |
| Get Sensor Reading | V | V | V |
| Set Sensor Type | - | - | - |
| Get Sensor Type | V | V | V |
| Set Sensor Reading And Event Status | V | V | V |
| **FRU Device Commands** |  |  |  |
| Get FRU Inventory Area Info | V | V | V |
| Read FRU Data | V | V | V |
| Write FRU Data | V | V | V |
| **SDR Device Commands** |  |  |  |
| Get SDR Repository Info | V | V | V |
| Get SDR Repository Allocation Info | - | - | - |
| Reserve SDR Repository | V | V | V |
| Get SDR | V | V | V |
| Add SDR | - | - | - |
| Partial Add SDR | - | - | - |
| Delete SDR | - | - | - |
| Clear SDR Repository | - | - | - |
| Get SDR Repository Time | - | - | - |
| Set SDR Repository Time | - | - | - |
| Enter SDR Repository Update Mode | - | - | - |
| Exit SDR Repository Update Mode | - | - | - |
| Run Initialization Agent | - | - | - |
| **SEL Device Commands** |  |  |  |
| Get SEL Info | V | V | V |
| Get SEL Allocation Info | - | - | - |
| Reserve SEL | V | V | V |
| Get SEL Entry | V | V | V |
| Add SEL Entry | V | V | V |
| Partial Add SEL Entry | - | - | - |
| Delete SEL Entry | V | V | V |
| Clear SEL | V | V | V |
| Get SEL Time | V | V | V |
| Set SEL Time | V | V | V |
| Get Auxiliary Log Status | - | - | - |
| Set Auxiliary Log Status | - | - | - |
| Get SEL Time UTC Offset | - | - | - |
| Set SEL Time UTC Offset | - | - | - |
| **LAN Device Commands** |  |  |  |
| Set LAN Configuration Parameters | V | V | V |
| Get LAN Configuration Parameters | V | V | V |
| Suspend BMC ARPs | - | - | - |
| Get IP/UDP/RMCP Statistics | - | - | - |
| **Serial/Modem Device Commands** |  |  |  |
| Set Serial/Modem Mux | - | - | - |
| Set Serial Routing Mux | - | - | - |
| SOL Activating | - | V | - |
| Set SOL Configuration Parameters | - | V | - |
| Get SOL Configuration Parameters | - | V | - |
| **Command Forwarding Commands** |  |  |  |
| Forwarded Command | - | - | - |
| Set Forwarded Commands | - | - | - |
| Get Forwarded Commands | - | - | - |
| Enable Forwarded Commands | - | - | - |
> _V: Verified_  
> _-: Unsupported_
