# NPCM750 RunBMC Olympus
This is the Nuvoton RunBMC Olympus System layer.
The NPCM750 is an ARM based SoC with external DDR RAM and
supports a large set of peripherals made by Nuvoton.
More information about the NPCM7XX can be found
[here](http://www.nuvoton.com/hq/products/cloud-computing/ibmc/?__locale=en).

- Working with [openbmc master branch](https://github.com/openbmc/openbmc/tree/master "openbmc master branch")

# Dependencies
![](https://cdn.rawgit.com/maxdog988/icons/61485d57/label_openbmc_ver_master.svg)

This layer depends on:

```
  URI: git@github.com:Nuvoton-Israel/openbmc
  branch: runbmc
```

# Contacts for Patches

Please submit any patches against the meta-runbmc-nuvoton layer to the maintainer of nuvoton:

* Joseph Liu, <KWLIU@nuvoton.com>
* Medad CChien, <CTCCHIEN@nuvoton.com>
* Tyrone Ting, <KFTING@nuvoton.com>
* Stanley Chu, <YSCHU@nuvoton.com>
* Tim Lee, <CHLI30@nuvoton.com>
* Brian Ma, <CHMA0@nuvoton.com>
* Jim Liu, <JJLIU0@nuvoton.com>

# Table of Contents

- [Dependencies](#dependencies)
- [Contacts for Patches](#contacts-for-patches)
- [Features of NPCM750 RunBMC Olympus](#features-of-npcm750-runbmc-olympus)
  * [WebUI](#webui)
    + [iKVM](#ikvm)
    + [Serial Over Lan](#serial-over-lan)
    + [Virtual Media](#virtual-media)
    + [BMC Firmware Update Over Lan](#bmc-firmware-update-over-lan)
    + [BIOS Firmware Update Over Lan](#bios-firmware-update-over-lan)
    + [MCU Firmware Update Over Lan](#mcu-firmware-update-over-lan)
    + [Server Power Operations](#server-power-operations)
    + [Certificate Management](#Certificate-Management)
  * [System](#system)
    + [Sensors](#sensors)
    + [LED Manager](#led-manager)
    + [Fan PID Control](#fan-pid-control)
    + [Event Policy](#event-policy)
    + [Time Sync](#time-sync)
      + [NTP Server](#ntp-server)
      + [Intel PCH Time Sync](#intel-pch-time-sync)
      + [Intel Node Manager Get SEL Time](#intel-node-manager-get-sel-time)
    + [Inventory](#inventory)
      + [IPMI FRU](#ipmi-fru)
      + [SMBIOS](#smbios)
      + [Power Supply Unit Inventory](#power-supply-unit-inventory)
    + [Network](#network)
      + [VLAN](#vlan)
    + [BIOS POST Code](#bios-post-code)
    + [IPMI SOL](#ipmi-sol)
    + [Host Power Budget Control](#host-power-budget-control)
  * [PECI](#peci)
    + [Intel Crash Dump](#intel-crash-dump)
  * [JTAG Master](#jtag-master)
    + [Intel At-Scale Debug](#intel-at-scale-debug)
    + [CPLD Programming](#cpld-programming)
  * [BMC / BIOS In-Band Firmware Update](#bmc--bios-in-band-firmware-update)
    + [HOST Tool](#host-tool)
    + [IPMI Library](#ipmi-library)
  * [LDAP for User Management](#ldap-for-user-management)
    + [LDAP Server Setup](#ldap-server-setup)
  * [MCTP](#mctp)
    + [MCTP over PCIe VDM](#mctp-over-pcie-vdm)
    + [MCTP over SMBUS](#mctp-over-smbus)
  * [PLDM](#pldm)
    + [PLDM Sensors](#pldm-sensors)
  * [BMC Reboot Cause](#bmc-reboot-cause)
    + [Last BMC Reboot Cause](#last-bmc-reboot-cause)
- [OpenBMC Test Automation](#openbmc-test-automation)
- [Features In Progressing](#features-in-progressing)
- [Features Planned](#features-planned)
- [IPMI Commands](#ipmi-commands)
- [DCMI Commands](#dcmi-commands)
- [Redfish APIs](#redfish-apis)
- [Image Size](#image-size)
- [Modifications](#modifications)

# Features of NPCM750 RunBMC Olympus

## WebUI

### iKVM
<img align="right" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/ipkvm.PNG">

This is a Virtual Network Computing (VNC) server programm using [LibVNCServer](https://github.com/LibVNC/libvncserver).
1. Support Video Capture and Differentiation(VCD), compares frame by hardware.
2. Support Encoding Compression Engine(ECE), 16-bit hextile compression hardware encoding.
3. Support USB HID, support Keyboard and Mouse.

**Source URL**

* [https://github.com/Nuvoton-Israel/obmc-ikvm](https://github.com/Nuvoton-Israel/obmc-ikvm)

**How to use**

1. Power on the Nuvoton RunBMC Olympus
2. Make sure the network is connected with your workstation.
3. Launch a browser in your workstation and you will see the entry page.
    ```
    /* BMCWeb Server */
    https://<poelg ip>
    ```
4. Login to OpenBMC home page
    ```
    Username: root
    Password: 0penBmc
    ```
5. Navigate to OpenBMC WebUI viewer
    ```
    https://<poelg ip>/#/server-control/kvm
    ```
**Performance**

* Host OS: Windows Server 2012 R2

|Playing video: [AQUAMAN](https://www.youtube.com/watch?v=2wcj6SrX4zw)|[Real VNC viewer](https://www.realvnc.com/en/connect/download/viewer/) | noVNC viewer
:-------------|:--------|:-----------|
Host Resolution    | FPS    | FPS |
1024x768  |  25    | 8 |
1280x1024   |  20  | 4 |
1600x1200   |  14   | 3 |

|Scrolling bar: [Demo video](https://drive.google.com/file/d/1H71_H6yjO8NU4Qu_ZL4F59FQ0PQmEo2n/view)|[Real VNC viewer](https://www.realvnc.com/en/connect/download/viewer/) | noVNC viewer
:-------------|:--------|:-----------|
Host Resolution    | FPS    | FPS |
1024x768  |  31    | 15 |
1280x1024   |  24  | 12 |
1600x1200   |  20   | 7 |

**The preferred settings of RealVNC Viewer**
```
Picture quality: Custom
ColorLevel: rgb565
PreferredEncoding: Hextile
```

**Maintainer**

* Joseph Liu

### Serial Over Lan
<img align="right" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/SOL.PNG">

The Serial over LAN (SoL) console redirects the output of the server’s serial port to a browser window on your workstation.

**Source URL**

* [https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/console](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/console)

**How to use**

1. Setup serial console in Olympus host (Ubuntu as example)
    * reference: [Ubuntu 16.04: GRUB2 and Linux with serial console](https://www.hiroom2.com/2016/06/06/ubuntu-16-04-grub2-and-linux-with-serial-console/)
    * Set value as following in /etc/default/grub
    ```
    GRUB_CMDLINE_LINUX="console=tty console=ttyS1,57600"
    ```
    * Then make grub.cfg
    ```
    sudo grub-mkconfig -o /boot/grub/grub.cfg
    ```

2. Run SOL:

    * Launch a browser in your workstation and navigate to https://${BMC_IP}
    * Bypass the secure warning and continue to the website.
    * Enter the BMC Username and Password (defaults: **root/0penBmc**).
    * You will see the OpenBMC management screen.
    * Click `Server control` at the left side of the OpenBMC management screen.
    * A `Serial over LAN console` menu item prompts then and click it.
    * Power on host server.
    * A specific area will display the host ttyS1 that user can operate host OS.

**Maintainer**

* Tyrone Ting

### Virtual Media
<img align="right" width="20%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/3e65e7a/openbmc/vm_app_win.png">
<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/cab7306/openbmc/vm.png">

Virtual Media (VM) is to emulate an USB drive on remote host PC via Network Block Device(NBD) and Mass Storage(MSTG).

**Source URL**

* [https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-phosphor/nuvoton-layer/recipes-connectivity/jsnbd](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-phosphor/nuvoton-layer/recipes-connectivity/jsnbd)
* [https://github.com/Nuvoton-Israel/openbmc-util/tree/master/virtual_media_openbmc2.6](https://github.com/Nuvoton-Israel/openbmc-util/tree/master/virtual_media_openbmc2.6)

**How to use**

1. Clone a physical USB drive to an image file
    * For Linux - use tool like **dd**
      ```
      dd if=/dev/sda of=usb.img bs=1M count=100
      ```
      > _**bs** here is block size and **count** is block count._
      >
      > _For example, if the size of your USB drive is 1GB, then you could set "bs=1M" and "count=1024"_

    * For Windows - use tool like **Win32DiskImager.exe**

      > _NOTICE : A simple *.iso file cannot work for this._

2. Enable Virtual Media

    2.1 VM-WEB
    1. Login and switch to webpage of VM on your browser
        ```
        https://XXX.XXX.XXX.XXX/#/server-control/virtual-media
        ```

    2. Operations of Virtual Media
        * After `Choose File`, click `Start` to start VM network service
        * After clicking `Start`, you will see a new USB device on HOST OS
        * If you want to stop this service, just click `Stop` to stop VM network service.

    2.2 VM standalone application
    * Download [application source code](https://github.com/Nuvoton-Israel/openbmc-util/tree/master/virtual_media_openbmc2.6)
    * Follow the [readme](https://github.com/Nuvoton-Israel/openbmc-util/blob/master/virtual_media_openbmc2.6/NBDServerWSWindows/README) instructions install QT and Openssl
    * Start QT creator, open project **VirtualMedia.pro**, then build all
    * Launch windows/linux application
        > _NOTICE : use `sudo` to launch app in linux and install `nmap` first_
    *  Operations
        + After `Chose an Image File` or `Select an USB Drive`, click `Search` to check which BMCs are on line
        + Select any on line BMC and key in `Account/Password`, choose the `Export Type` to Image, and click `Start VM` to start VM network service (still not hook USB disk to host platform)
        + After `Start VM`, click `Mount USB` to hook the emulated USB disk to host platform, or click `Stop VM` to stop VM network service.
        + After `Mount USB`, click `UnMount USB` to emulate unplugging the USB disk from host platform
        + After `UnMount USB`, click `Stop VM` to stop VM network service, or click `Mount USB` to hook USB disk to host platform.

3. Performance

|Client |MountType |Encryption |Speed KB/s
:---------------------|:----------------------------------------------|:---|:---------|
Client WebUI(HTML5)  | CentOS-8.2.2004-x86_64-dvd1.iso (8GB Size)    |Yes| ~7800KB/s |

**Maintainer**
* Medad CChien

### BMC Firmware Update Over Lan
<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/cab7306/openbmc/firmware-update.png">

This is a secure flash update mechanism to update BMC firmware via WebUI.

**Source URL**

* [https://github.com/Nuvoton-Israel/phosphor-bmc-code-mgmt](https://github.com/Nuvoton-Israel/phosphor-bmc-code-mgmt)

**How to use**

* Update firmware via WebUI
    1. Upload update package from web UI, then you will see
        ```
        Activate
        ```
        > If you select activate, then you will see activation dialog at item 2

        ```
        Delete
        ```
        > If you select delete, then the package will be deleted right now

    2. Confirm BMC firmware file activation
        ```
        ACTIVATE FIRMWARE FILE WITHOUT REBOOTING BMC
        ```
        > If you select this, you need to reboot BMC manually, and shutdown application will run update script to flash image into SPI flash

        ```
        ACTIVATE FIRMWARE FILE AND AUTOMATICALLY REBOOT BMC
        ```
        > if you select this, BMC will shutdown right now, and shutdown application will run update script to flash image into SPI flash

* Update firmware via Redfish

    We can update BMC firmware via REST API provided by Redfish. The firmware will apply immediately after uploaded without any confirmation by default.
    The following command shows how to using curl command upload BMC firmware.
    ```
    curl -k -H "X-Auth-Token: $token" -H "Content-Type: application/octet-stream" -X POST -T obmc-phosphor-image-olympus-nuvoton.static.mtd.tar https://${BMC_IP}/redfish/v1/UpdateService    
    ```
    >_${token} is the token value come from login API, read more information from [REST README](https://github.com/openbmc/docs/blob/master/REST-cheatsheet.md)_

**Maintainer**

* Brian Ma

### BIOS Firmware Update Over Lan
<img align="right" width="40%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/0771b2f/openbmc/bios_update_demo.PNG">
  This BIOS update function is implemented according to Openbmc firmware update mechanism. Users can update BIOS just like they update BMC firmware via web UI.


**Source URL**

* [https://github.com/Nuvoton-Israel/phosphor-bmc-code-mgmt](https://github.com/Nuvoton-Israel/phosphor-bmc-code-mgmt)
* [https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-olympus-nuvoton/ipmi-bios-update](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-olympus-nuvoton/ipmi-bios-update)

**How to use**

* Prepare the BIOS image for update

  1. MANIFEST

      The manifest for BIOS must set *purpose=xyz.openbmc_project.Software.Version.VersionPurpose.Host*, and provide version information.
      Others information just set as BMC manifest.
      ```
      purpose=xyz.openbmc_project.Software.Version.VersionPurpose.Host
      version=F0B_1B01.07
      KeyType=OpenBMC
      HashType=RSA-SHA256
      ```

  2. BIOS firmware

      rename BIOS image to image-bios
      ```
      mv BIOS image-bios
      ```

  3. public key

      Prepare your own key pair for sign BIOS firmware, and put the public key in image. Then sign the image-bios as following command:
      ```
      openssl dgst -sha256 -sign userkey.priv -out image-bios.sig image-bios
      ```
  4. Sign MANIFEST and public key

      Follow the BMC update signature verify flow, we also need sign MANIFEST and user public key by BMC system key.
      ```
      openssl dgst -sha256 -sign OpenBMC.priv -out MANIFEST.sig MANIFEST
      openssl dgst -sha256 -sign OpenBMC.priv -out publickey.sig publickey
      ```

  5. Collect data and tar image

      Now we have six files, and we just need tar them to a file.
      ```
      tar -cf image-bios.tar image-bios image-bios.sig MANIFEST MANIFEST.sig publickey publickey.sig
      ```

* Update BIOS

  There are two way updating BIOS image just like BMC firmware update.

  1. WebUI

      Upload tar image, and active it.
      ```
      Server configuration
       -> Firmware
         -> Choose file (select image-bios.tar)
         -> Click "Upload firmware" button
         -> Click "Active" link
             -> Select "ACTIVATE FIRMWARE FILE AND AUTOMATICALLY REBOOT SERVER"
             -> Click Continue
      ```

  2. Redfish

      We can update BIOS image via REST API just like BMC firmware. And the image will apply immediately after uploaded by default.
      ```
      curl -k -H "X-Auth-Token: $token" -H "Content-Type: application/octet-stream" -X POST -T image-bios.tar https://${BMC_IP}/redfish/v1/UpdateService
      ```
      >_${token} is the token value come from login API, read more information from [REST README](https://github.com/openbmc/docs/blob/master/REST-cheatsheet.md)_

**Maintainer**

* Brian Ma

### MCU Firmware Update Over Lan
<img align="right" width="40%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/50a8c7e/openbmc/mcu.png">
  This MCU update function is implemented according to Openbmc firmware update mechanism. Users can update MCU just like they update BMC firmware via web UI.


**Source URL**

* [https://github.com/Nuvoton-Israel/phosphor-bmc-code-mgmt](https://github.com/Nuvoton-Israel/phosphor-bmc-code-mgmt)
* [https://github.com/Nuvoton-Israel/loadmcu](https://github.com/Nuvoton-Israel/loadmcu)

**How to use**

* Prepare the MCU image for update

  1. MANIFEST

      The manifest for MCU must set *purpose=xyz.openbmc_project.Software.Version.VersionPurpose.MCU*, and provide version information.
      Others information just set as BMC manifest.
      ```
      purpose=xyz.openbmc_project.Software.Version.VersionPurpose.MCU
      version=V0.4
      KeyType=OpenBMC
      HashType=RSA-SHA256
      MachineName=olympus-nuvoton
      ```

  2. MCU firmware

      rename MCU image to image-mcu
      ```
      mv ATtiny1634_FW.bin image-mcu
      ```

  3. public key

      Prepare your own key pair for sign MCU firmware, and put the public key in image. Then sign the image-mcu as following command:
      ```
      openssl dgst -sha256 -sign userkey.priv -out image-mcu.sig image-mcu
      ```
  4. Sign MANIFEST and public key

      Follow the BMC update signature verify flow, we also need sign MANIFEST and user public key by BMC system key.
      ```
      openssl dgst -sha256 -sign OpenBMC.priv -out MANIFEST.sig MANIFEST
      openssl dgst -sha256 -sign OpenBMC.priv -out publickey.sig publickey
      ```

  5. Collect data and tar image

      Now we have six files, and we just need tar them to a file.
      ```
      tar -cf image-mcu.tar image-mcu image-mcu.sig MANIFEST MANIFEST.sig publickey publickey.sig
      ```

* Update MCU

  There are two way updating MCU image just like BMC firmware update.

  1. WebUI

      Upload tar image, and active it.
      ```
      Server configuration
       -> Firmware
         -> Choose file (select image-mcu.tar)
         -> Click "Upload firmware" button
         -> Click "Active" link
             -> Select "ACTIVATE FIRMWARE FILE AND AUTOMATICALLY REBOOT SERVER"
             -> Click Continue
      ```

  2. Redfish

      We can update MCU image via REST API just like BMC firmware. And the image will apply immediately after uploaded by default.
      ```
      curl -k -H "X-Auth-Token: $token" -H "Content-Type: application/octet-stream" -X POST -T image-mcu.tar https://${BMC_IP}/redfish/v1/UpdateService
      ```
      >_${token} is the token value come from login API, read more information from [REST README](https://github.com/openbmc/docs/blob/master/REST-cheatsheet.md)_

**Maintainer**

* Tim Lee

### Server Power Operations
<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/8fc19a1/openbmc/power_ops.png">

Server Power Operations are using to Power on/Warm reboot/Cold reboot/Orderly shutdown/Immediate shutdown remote host PC.

**Source URL**

* [https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-x86/chassis](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-x86/chassis)

**How to use**

1. Configure reaction of power button on generic host OS
    * When host OS is running **Linux** and you press **PWRON** button on motherboard, you're prompted with a list of options - this is the **interactive** shutdown. The OS will go **Orderly shutdown** for a while if you didn't select any action from it. If you don't want this interactive shutdown pop up and hope OS go **Orderly shutdown** directly, you can enter below command in terminal before testing:
      ```
      gsettings set org.gnome.settings-daemon.plugins.power button-power 'shutdown'
      ```
    * When host OS is running **Windows** and you press **PWRON** button on motherboard, the default reaction is **Orderly shutdown**. Thus, you didn't need to configure reaction of power button in Windows. But, if you find the default reaction is not **Orderly shutdown**, please check `Control Panel`->`Power Options`->`System Settings` in Windows OS.

    * About Watchdog patch

      There is a package **phosphor-watchdog** included in OpenBMC now. The watchdog daemon is started on host's power on, which is used to monitor if host is alive. In normal case, when host starts, it will send IPMI commands to kick watchdog and so everything would work fine. If host fails to start, the watchdog eventually timeout. However, the default watchdog timeout action is **HardReset** which is defined at [Watchdog.interface.yaml](https://github.com/openbmc/phosphor-dbus-interfaces/blob/master/xyz/openbmc_project/State/Watchdog.interface.yaml) in **phosphor-dbus-interfaces** that will cause host rebooted after power on.

2. Configure GPIO pin definitions for **POWER_OUT**, **RESET_OUT**, **PS_PWROK**, **POWER_BUTTON**, **RESET_BUTTON** and **RESET_BUTTON** on BMC

    * Pin **POWER_OUT** is use to do all server power operations, pin **RESET_OUT** is reserve for reset operations, and **PS_PWROK** is use to monitor DC real status that indicate `Server power` in WebUI.

    * If other GPIO pins are preferred, please modify the file [power-config-host0.json](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-x86/chassis/x86-power-control/power-config-host0.json).

    * Content below is a part of **power-config-host0.json** for this sample:
      ```
      {
          "PostComplete": "POST_COMPLETE",
          "PwrButton": "POWER_BUTTON",
          "PwrOK": "PS_PWROK",
          "PwrOut": "POWER_OUT",
          "RstButton": "RESET_BUTTON",
          "RstOut": "RESET_OUT"
      }
      ```
      > _**PostComplete**, **PwrButton**, **PwrOK**, **PwrOut**, **RstButton** and **RstOut** here are referred in code and fixed, please don't modify it. However, **POST_COMPLETE**, **POWER_BUTTON**, **PS_PWROK**, **POWER_OUT**, **RESET_BUTTON** and **RESET_OUT** all need to match **gpio-line-names** naming in your project **DTS** file._

3. Server Power on
    * Press `Power on` button from `Control`->`Server power operations` of WebUI.

      > _curl -k -H "X-Auth-Token: $token" -X POST https://${bmc}/redfish/v1/Systems/system/Actions/ComputerSystem.Reset -d '{"ResetType": "ForceOn"}'_

4. Server Power off (Soft)
    * Select `Orderly` and press `Shut down` button from `Control`->`Server power operations` of WebUI.

      > _curl -k -H "X-Auth-Token: $token" -X POST https://${bmc}/redfish/v1/Systems/system/Actions/ComputerSystem.Reset -d '{"ResetType": "GracefulShutdown"}'_

5. Server Power off (Hard)
    * Select `Immediate` and press `Shut down` button from `Control`->`Server power operations` of WebUI.

      > _curl -k -H "X-Auth-Token: $token" -X POST https://${bmc}/redfish/v1/Systems/system/Actions/ComputerSystem.Reset -d '{"ResetType": "ForceOff"}'_

6. Server Reboot (Warm)
    * Select `Orderly` and press `Reboot` button from `Control`->`Server power operations` of WebUI.

      > _curl -k -H "X-Auth-Token: $token" -X POST https://${bmc}/redfish/v1/Systems/system/Actions/ComputerSystem.Reset -d '{"ResetType": "GracefulRestart"}'_

7. Server Reboot (Cold)
    * Select `Immediate` and press `Reboot` button from `Control`->`Server power operations` of WebUI.

      > _curl -k -H "X-Auth-Token: $token" -X POST https://${bmc}/redfish/v1/Systems/system/Actions/ComputerSystem.Reset -d '{"ResetType": "PowerCycle"}'_

**Maintainer**
* Tim Lee

### Certificate Management
<img align="right" width="50%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/ssl_certificate.png">  

Manage the certificates installed on BMC.  
* Web Server certificate
* LDAP Client certificate
* CA certificate  

Management operations:
* Install a new certificate.  
* Replace an existing certificate. 
* Delete an existing certificate. 

**Source URL**
* [https://github.com/openbmc/phosphor-certificate-manager](https://github.com/openbmc/phosphor-certificate-manager)

**How to use**  
1. Web server certificate  
   * Web server certificate is used to initiate a SSL session with browsers.
   * bmcweb will generate a self-signed certificate if no server certificate exists.
   * certificate file path
     > /etc/ssl/certs/https/server.pem
   * replace certificate with new one.
     * generate a new self-signed certificate using openssl. 
     ```
     openssl req -x509 -sha256 -newkey rsa:2048  -nodes -days 365  -keyout cert.pem -out cert.pem  -subj "/O=XYZ Corporation /CN=www.xyz.com"
     ```
     * replace certificate by the new cert "cert.pem" via WebUI.  
       <img align="top" width="70%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/replace_cert.png">
     * restart bmcweb service on BMC.  
       > systemctl restart bmcweb  
       
     * connect to BMC web server and check the web certificate issuer from browser, it should be "www.xyz.com".      
       <img align="top" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/new_certificate.png">
2. CA certificate
   * if server authentication is required in a TLS session, LDAP client will use CA certificate (issuer of server certificate) to verify server's identity when initiating a TLS session.
   * refer to [LDAP server setup](#ldap-server-setup) to generate a CA certificate and configure LDAP server.
     * Note: CN in LDAP server certificate must match server's URI (i.e. IP address or hostname).
       ```
       #example ssl command to generate server certificate
       openssl genrsa -out ldap_server.key 2048
       openssl req -new -key ldap_server.key -out ldap_server.csr -subj '/C=OO/ST=OO/L=OO/O=OO/OU=OO/CN=10.103.152.11'
       openssl x509 -req -days 365 -CA ca_cert.pem -CAkey ca_key.pem -CAcreateserial -CAserial serial -in ldap_server.csr -out ldap_server.pem
       ```
   * install CA certificate onto BMC via WebUI

3. LDAP client certificate
   * if client authentication is required in a TLS session, LDAP server will use CA certificate (issuer of client certificate) to verify client's identity when initiating a TLS session. BMC needs to install a client certificate signed with CA certificate.
   * generate a client certificate with signature signed with CA certificate.
     ```
     openssl genrsa -out ldap_client.key 2048
     openssl req -new -key ldap_client.key -out ldap_client.csr -subj '/C=TW/ST=Taiwan/L=Tainan/O=Nuvoton Ltd/OU=CS20/CN=ldap client'
     openssl x509 -req -days 365 -CA ca_cert.pem -CAkey ca_key.pem -CAcreateserial -CAserial serial -in ldap_client.csr -out ldap_client.cert
     ```
   * Because the LDAP certificate installed on BMC also requires a private key, combine ssl certificate and private key into one pem file
     ```
     cat ldap_client.key ldap_client.cert > ldap_client.pem
     ```
   * install ldap_client.pem onto BMC via WebUI
4. Enable LDAP over SSL  
   * The enable action on WebUI will enable LDAP client to perform server authentication when initiating a TLS session.    
      <img align="top" width="70%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/enable_ldap_over_ssl.png">
    * To enable client authentication, add following config to ldap server configuration file (slapd.conf in this case).  
      > TLSVerifyClient demand
    
5. Test LDAP server.
   * Log in BMC WebUI using user1/123

**Maintainer**
* Stanley Chu

## System

### Sensors
[phosphor-hwmon](https://github.com/openbmc/phosphor-hwmon) daemon will periodically check the sensor reading to see if it exceeds lower bound or upper bound . If alarm condition is hit, the [phosphor-sel-logger](https://github.com/openbmc/phosphor-sel-logger) handles all sensor events to add new IPMI SEL records to the journal, [phosphor-host-ipmid](https://github.com/Nuvoton-Israel/phosphor-host-ipmid) will convert the journal SEL records to IPMI SEL record format and reply to host.

**Source URL**
* [https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/configuration](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/configuration)
* [https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/ipmi](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/ipmi)
* [https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/sensors](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/sensors)

**How to use**

* **Configure sensor**
    
  * Add Sensor Configuration File
  
    Each sensor **temperature**, **adc**, **fan**, **peci** and **power** has a [hwmon config file](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/sensors/phosphor-hwmon/obmc/hwmon/ahb/apb) and [ipmi sdr config file](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/configuration/olympus-nuvoton-yaml-config/olympus-nuvoton-ipmi-sensors.yaml) that defines the sensor name and its warning or critical thresholds. These files are located under **recipes-phosphor/sensors/phosphor-hwmon%/obmc/hwmon/apb/** and **recipes-phosphor/configuration/olympus-nuvoton-yaml-config/olympus-nuvoton-ipmi-sensors.yaml**.  

    Below is hwmon config for a LM75 sensor on BMC. The sensor type is **temperature** and its name is **bmc_card**. It has warning and critical thresholds for **upper** and **lower** bound.
      ```
      LABEL_temp1 = "bmc_card"
      WARNHI_temp1 = "110000"
      WARNLO_temp1 = "5000"
      CRITHI_temp1 = "115000"
      CRITLO_temp1 = "0"
      ```
    Below is ipmi sdr config for a LM75 sensor on BMC.
      ```
      1:
        entityID: 0x07
        entityInstance: 1
        sensorType: 0x01
        path: /xyz/openbmc_project/sensors/temperature/bmc_card
        sensorReadingType: 0x01
        multiplierM: 1
        offsetB: 0
        bExp: 0
        rExp: 0
        scale: -3
        unit: xyz.openbmc_project.Sensor.Value.Unit.DegreesC
        mutability: Mutability::Write|Mutability::Read
        serviceInterface: org.freedesktop.DBus.Properties
        readingType: readingData
        sensorNamePattern: nameLeaf
        interfaces:
          xyz.openbmc_project.Sensor.Value:
            Value:
              Offsets:
                0xFF:
                  type: int64_t

      ```

* **Monitor sensor and events**

  * Using WebUI  

    In `Sensors` page of **WebUI**, the sensors reading will show as below.

    <img align="bottomleft" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/sensor_reading.PNG">  


    In `System log` page of **WebUI**, the sensors event will show as below.

    <img align="bottomleft" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/sensor_event.PNG">  


  * Using IPMI

    Use IPMI utilities like **ipmitool** to send command for getting SDR records.  
    ```
    $ sudo ipmitool sdr elist
      bmc_card         | 01h | ok  |  7.1 | 36 degrees C
      inlet            | 02h | ok  |  7.2 | 27 degrees C
      outlet           | 03h | ok  |  7.3 | 27 degrees C
      hotswap_vin          | 05h | ok  |  7.5 | 12.32 Volts
      hotswap_vout         | 06h | ok  |  7.6 | 12.32 Volts
      hotswap_pout          | 07h | ok  |  7.7 | 4 Watts
      hotswap_iout         | 08h | ok  |  7.8 | 0.09 Amps
      p0_dimm_vr0_temp | 09h | ok  | 32.1 | 0 degrees C
      p0_dimm_vr1_temp | 0Ah | ok  | 32.2 | 0 degrees C
      p1_dimm_vr0_temp | 0Bh | ok  | 32.3 | 0 degrees C
      p1_dimm_vr1_temp | 0Ch | ok  | 32.4 | 0 degrees C
      p0_dimm_vr0_volt | 0Dh | ok  | 32.5 | 12.40 Volts
      p0_dimm_vr1_volt | 0Eh | ok  | 32.6 | 12.40 Volts
      p1_dimm_vr0_volt | 0Fh | ok  | 32.7 | 12.40 Volts
      p1_dimm_vr1_volt | 10h | ok  | 32.8 | 12.40 Volts
    ```
    Use IPMI utilities like **ipmitool** to send command for getting SEL records.  
    ```
    $ sudo ipmitool sel list
    
       1 | 10/04/2018 | 07:08:54 | Temperature #0x03 | Lower Critical going low  | Asserted
       2 | 10/04/2018 | 07:10:39 | Temperature #0x03 | Lower Critical going low  | Asserted
       3 | 10/04/2018 | 07:28:04 | Temperature #0x03 | Upper Critical going high | Asserted
       4 | 10/04/2018 | 07:28:11 | Temperature #0x03 | Upper Critical going high | Asserted
       5 | 10/04/2018 | 07:28:13 | Temperature #0x03 | Upper Critical going high | Asserted
       6 | 10/04/2018 | 07:46:34 | Temperature #0x03 | Upper Critical going high | Asserted
       7 | 10/04/2018 | 07:46:38 | Temperature #0x03 | Upper Critical going high | Asserted
       8 | 10/04/2018 | 07:46:43 | Temperature #0x03 | Upper Critical going high | Asserted
       9 | 10/04/2018 | 07:46:59 | Temperature #0x03 | Upper Critical going high | Asserted
       a | 10/04/2018 | 07:47:24 | Temperature #0x03 | Upper Critical going high | Asserted
       b | 10/04/2018 | 07:47:29 | Temperature #0x03 | Upper Critical going high | Asserted
       c | 10/04/2018 | 07:47:42 | Temperature #0x03 | Upper Critical going high | Asserted
       d | 10/04/2018 | 07:48:37 | Temperature #0x03 | Upper Critical going high | Asserted
       e | 10/04/2018 | 07:48:39 | Temperature #0x03 | Upper Critical going high | Asserted
       f | 10/04/2018 | 07:48:53 | Temperature #0x03 | Upper Critical going high | Asserted
      10 | 10/04/2018 | 09:19:11 | Temperature #0x03 | Lower Critical going low  | Asserted
      11 | 10/04/2018 | 09:20:22 | Temperature #0x03 | Lower Critical going low  | Asserted
      12 | 10/04/2018 | 09:20:24 | Temperature #0x03 | Lower Critical going low  | Asserted
      13 | 10/04/2018 | 09:33:24 | Temperature #0x03 | Upper Critical going high | Asserted
      14 | 10/04/2018 | 09:33:31 | Temperature #0x03 | Upper Critical going high | Asserted
    ```

**Maintainer**

* Stanley Chu

### LED Manager
<img align="right" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/ServerLed.PNG">  

Turning on ServerLED via WebUI will make **identify** leds on BMC start blinking,
 and **heartbeat** will start blinkling after BMC booted.

**Source URL**
* [https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/leds](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/leds)

**How to use**
* Add EnclosureIdentify in LED [config file](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/leds/olympus-nuvoton-led-manager-config/led.yaml)
  ```
  BmcBooted:
    heartbeat:
        Action: 'Blink'
        DutyOn: 50
        Period: 1000
  EnclosureIdentify:
    identify:
        Action: 'Blink'
        DutyOn: 50
        Period: 1000
  ```

* Modify BSP layer [config](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/conf/machine/olympus-nuvoton.conf) to select NPCM750 LED config file
  ```
  PREFERRED_PROVIDER_virtual/phosphor-led-manager-config-native = "npcm750-led-manager-config-native"
  ```

**Maintainer**

* Stanley Chu


### Fan PID Control
<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/e12e9dd/openbmc/fan_stepwise_pwm.png">
<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/8fc19a1/openbmc/fan_rpms.png">

In NPCM750, we have two PWM modules and support eight PWM signals to control fans for dynamic adjustment according temperature variation.

**Source URL**

* [https://github.com/openbmc/phosphor-pid-control](https://github.com/openbmc/phosphor-pid-control)
* [https://github.com/openbmc/phosphor-hwmon](https://github.com/openbmc/phosphor-hwmon)
* [https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/fans/phosphor-pid-control](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/fans/phosphor-pid-control)

**How to use**

In order to automatically apply accurate and responsive correction to a fan control function, we use the `swampd` to handle output PWM signal. For enable this daemon, basically we need configuring the swampd configuration file and deploy a system service for start this daemon as below steps.

>_NOTICE: In current solution, we only use stepwise mechanism to control fans. But the swampd also can controls fan with PID by tuning parameters according to customer platform._

* The swampd(PID control daemon) is a Margin-based daemon running within the OpenBMC environment. It uses a well-defined [configuration file](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/fans/phosphor-pid-control/config-olympus-nuvoton.json) to control the temperature of the tray components to keep them within operating conditions.

    Here is a configuraion example shows how to using one PWM control more than one fans on system.
    ```
    "sensors" : [
        {
            "name": "fan1",
            "type": "fan",
            "readPath": "/xyz/openbmc_project/sensors/fan_tach/fan1",
            "writePath": "/sys/devices/platform/ahb/ahb:apb/f0103000.pwm-fan-controller/hwmon/**/pwm1",
            "min": 0,
            "max": 255
        },
        {
            "name": "fan2",
            "type": "fan",
            "readPath": "/xyz/openbmc_project/sensors/fan_tach/fan2",
            "writePath": "/sys/devices/platform/ahb/ahb:apb/f0103000.pwm-fan-controller/hwmon/**/pwm1",
            "min": 0,
            "max": 255
        },
        {
            "name": "cpu0_core0_temp",
            "type": "temp",
            "readPath": "/xyz/openbmc_project/sensors/temperature/cpu0_core0_temp",
            "writePath": "",
            "min": 0,
            "max": 0,
            "timeout": 0
        },
    ],
    "zones" : [
        {
            "id": 0,
            "minThermalOutput": 0.0,
            "failsafePercent": 100.0,
            "pids": [
                {
                    "name": "fan1",
                    "type": "fan",
                    "inputs": ["fan1"],
                    "setpoint": 40.0,
                    "pid": {
                        "samplePeriod": 1.0,
                        "proportionalCoeff": 0.0,
                        "integralCoeff": 0.0,
                        "feedFwdOffsetCoeff": 0.0,
                        "feedFwdGainCoeff": 1.0,
                        "integralLimit_min": 0.0,
                        "integralLimit_max": 0.0,
                        "outLim_min": 3.0,
                        "outLim_max": 100.0,
                        "slewNeg": 0.0,
                        "slewPos": 0.0
                    }
                },
                {
                    "name": "cpu0_core0_temp",
                    "type": "stepwise",
                    "inputs": ["cpu0_core0_temp"],
                    "setpoint": 30.0,
                    "pid": {
                        "samplePeriod": 1.0,
                        "positiveHysteresis": 0.0,
                        "negativeHysteresis": 0.0,
                        "isCeiling": false,
                        "reading": {
                            "0": 25,
                            "1": 26,
                            "2": 27,
                            "3": 28,
                            "4": 29,
                            "5": 30,
                            "6": 31,
                            "7": 32,
                            "8": 33,
                            "9": 34,
                            ...
                        },
                        "output": {
                            "0": 10,
                            "1": 10,
                            "2": 10,
                            "3": 10,
                            "4": 10,
                            "5": 10,
                            "6": 20,
                            "7": 30,
                            "8": 40,
                            "9": 50,
                            ...
                        }
                    },
                },
            ],
        },
    ]
    ```
    The [PID README](https://github.com/openbmc/phosphor-pid-control/blob/master/configure.md) provide more detail about the meaning for each parameter.

    Roughly speaking, there are two main section in configuration file: **sensor** and **zones**.
    **Sensors** defined the all component which are involved PID like temperature sensors, or fans.
    **Zones** defined the mechanism how the swampd control each fans by setting parameters.

    The most important in **sensors** section is the settings of `readPath` and `writePath`.
    Sensors like temperature sensor or margin sensor must only set readPath, and fill up empty string to writePath.
    Fans could set the D-Bus path to readPath, also set the pwm system path to writePath.
    More detail about readPath and writePath please refer README that mentioned above.

    There are four PID controller types user can use: `fan`, `temp`, `margin`, and `stepwise` in **zones**.
    User can tune the PID parameters following the [tuning README](https://github.com/openbmc/phosphor-pid-control/blob/master/tuning.md). 

    In above example case, the fan PID controller has a lot of PID parameters. And we only use the stepwise controller to control whole zone, so the PID parameters in fan controller like `integralCoeff` or `outLim_max` would not work.
    And the parameter `inputs` for stepwise controller must be thermal sensor.
    Please note the parameter `setpoint` is no meaning for type `fan` and `stepwise` currently, and cannot be left out.

    If user want to control whole zone by stepwise controller like example configuration, the key point is set reading and output.
    The `stepwise` PID use the mapping of reading and output value instead of set RPM setpoint.
    The reading and output value should be a pair, and user can set 20 pairs in maximum, one more pairs at least.
    And the `stepwise` will return output setpoint if temperature **is larger** than reading value.
    For example, assume here are pairs of `stepwise` reading and output:
    ```
    {
      "reading": {25, 26, 27},
      "output": {10, 20, 30}
    }
    ```

    If the temperature reading is 25.5°C, the return value will be 10.
    And if the reading value is 26.5°C, the stepwise controller will set 20% RPM to fan(s).


* OpenBMC will run swampd through [phosphor-pid-control.service](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/fans/phosphor-pid-control/phosphor-pid-control.service) that controls the fans by pre-defined zones. Here is a example service.
    ```
    [Service]
    Type=simple
    ExecStart=/usr/bin/swampd
    Restart=always
    RestartSec=5
    StartLimitInterval=0
    ExecStopPost=/usr/bin/fan-default-speed.sh
    ```
    + **ExecStopPost** that means an additional commands that are executed after the service is stopped.

**Maintainer**
* Tim Lee

### Event Policy

phosphor-dbus-monitor service can watch on specific dbus objects/properties and perform predefined event callbacks when the property state has changed or meets the condition defined in config file.  

**Source URL**
* [phosphor-dbus-monitor](https://github.com/openbmc/phosphor-dbus-monitor)
* [phosphor-snmp](https://github.com/openbmc/phosphor-snmp)

**Event Callbacks**

The event callbacks can be the following actions. Logging to journal or elog, calling d-bus methods, or sending snmp traps. Here is an example that log to journal and send d-bus message to shutdown host when the specific temperature sensor reaches the critical high threshold.

* [Example config](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-runbmc-nuvoton/recipes-phosphor/configuration/runbmc-nuvoton-yaml-config/runbmc-nuvoton-dbus-monitor-config.yaml)

**SNMP trap**

**How to use** 
1. Install snmp trap receiver on the management server. Refer to the [link](https://blog.cadena-it.com/linux-tips-how-to/snmp-trap-receiver-with-ubuntu/) for the installation.
    * Copy [BMC notification MIB](https://github.com/openbmc/phosphor-snmp/blob/master/mibs/NotificationMIB.txt) to `/usr/share/snmp/mibs`.
    * Run snmptrapd with -m option to load custom MIB
    ```
    snmptrapd -m OPENBMC-NOTIFICATION-MIB -Lf /var/log/snmptrap.log -f
    ```
2. Specify snmp manager ip/port in Openbmc WebUI
   ```
    Server configuration
     -> SNMP settings
       -> Add Managers
          -> enter the snmp manger ip and port(default is 162)
    ```
3. Check snmp trap log in snmp manager.
   * /var/log/snmptrap.log
   * the OIDs in log file will be translated to human readable string defined in MIB
   * Example:
   ```
   TRAP2, SNMP v2c, community publicDU Attribute/Value Pair Array:
   SNMPv2-SMI::snmpModules.1.1.4.1.0 = OID: OPENBMC-NOTIFICATION-MIB::obmcErrorNotification
   OPENBMC-NOTIFICATION-MIB::obmcErrorID = Gauge32: 135
   OPENBMC-NOTIFICATION-MIB::obmcErrorTimestamp = Opaque: UInt64: 780329535445925888
   OPENBMC-NOTIFICATION-MIB::obmcErrorSeverity = INTEGER: 3
   OPENBMC-NOTIFICATION-MIB::obmcErrorMessage = STRING:   "xyz.openbmc_project.Sensor.Threshold.Error.WarningLow"
   ```

**Maintainer**
* Stanley Chu

### Time Sync
  We provided three ways that user can synchronize the BMC time from network or host.

### NTP Server
  Network Time Protocol (NTP) is a networking protocol for clock synchronization between computer systems over packet-switched, variable-latency data networks.

  **systemd-timesyncd** is a daemon that has been added for synchronizing the system clock across the network. It implements an **SNTP (Simple NTP)** client. This daemon runs with minimal privileges, and has been hooked up with **systemd-networkd** to only operate when network connectivity is available.
        
  The modification time of the file **/var/lib/systemd/timesync/clock** indicates the timestamp of the last successful synchronization (or at least the **systemd build date**, in case synchronization was not possible).
    
  **Source URL**
  * [https://github.com/openbmc/phosphor-time-manager](https://github.com/openbmc/phosphor-time-manager)
  * [https://github.com/systemd/systemd/tree/master/src/timesync](https://github.com/systemd/systemd/tree/master/src/timesync)
    
  **How to use**
  <img align="right" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/Time.PNG">
  * Enable NTP by **Web-UI** `Server configuration`
      ->`Date and time settings`
  * Enable NTP by command
      ```
      timedatectl set-ntp true  
      ```
      >_timedatectl result will show **systemd-timesyncd.service active: yes**_ 
    
      If NTP is Enabled and network is Connected (Using **eth1** connect to router), we will see the item **systemd-timesyncd.service active** is **yes** and **System clock synchronized** is **yes**. Thus, system time will sync from NTP server to get current time.
  * Get NTP status  
      ```
      timedatectl  
      ```
      >_Local time: Mon 2018-08-27 09:24:51 UTC  
      >Universal time: Mon 2018-08-27 09:24:51 UTC  
      >RTC time: n/a  
      >Time zone: n/a (UTC, +0000)  
      >**System clock synchronized: yes**  
      >**systemd-timesyncd.service active: yes**  
      >RTC in local TZ: no_  
      
  * Disable NTP  
      ```
      timedatectl set-ntp false  
      ```
      >_timedatectl result will show **systemd-timesyncd.service active: no**_  
      
  * Using Local NTP server Configuration  
    When starting, systemd-timesyncd will read the configuration file from **/etc/systemd/timesyncd.conf**, which looks like as below: 
        >_[Time]  
        >\#NTP=  
        >\#FallbackNTP=time1.google.com time2.google.com time3.google.com time4.google.com_  
    
    	By default, systemd-timesyncd uses the Google Public NTP servers **time[1-4].google.com**, if no other NTP configuration is available. To add time servers or change the provided ones, **uncomment** the relevant line and list their host name or IP separated by a space. For example, we setup NB windows 10 system as NTP server with IP **192.168.1.128**  
        >_[Time]  
        >**NTP=192.168.1.128**  
    	>\#FallbackNTP=time1.google.com time2.google.com time3.google.com time4.google.com_
    
  * BMC connect to local NTP server of windows 10 system  
      Connect to NB through **eth1** EMAC interface, and set static IP **192.168.1.15**  
    
      ```
      ifconfig eth1 up
      ifconfig eth1 192.168.1.15
      ```  
      >_Note: Before that you need to setup your NTP server (192.168.1.128) on Windows 10 system first_  
      
      Modify **/etc/systemd/timesyncd.conf** file on BMC as we mentioned
        >_[Time]  
        >**NTP=192.168.1.128**_  
      
      Re-start NTP to make effect about our configuration change  
      ```
      systemctl restart systemd-timesyncd.service
      ```  
      Check status of NTP that show already synced to our local time server 
      ```
      systemctl status systemd-timesyncd.service -l --no-pager
      ```  
      >_Status: "Synchronized to time server 192.168.1.128:123 (192.168.1.128)."_  
      
      Verify **Web-UI** `Server overview`->`BMC time` whether sync from NTP server as same as **timedatectl**. (Note: timedatectl time zone default is UTC, thus you will find the BMC time is UTC+8)
      ```
      timedatectl  
      ```
      >_Local time: Thu 2018-09-06 07:24:16 UTC  
      >Universal time: Thu 2018-09-06 07:24:16 UTC  
      >RTC time: n/a  
      >Time zone: n/a (UTC, +0000)  
      >System clock synchronized: yes  
      >systemd-timesyncd.service active: yes  
      >RTC in local TZ: no_

  * TimeZone  
      According OpenBMC current design that only support UTC TimeZone now, we can use below command to get current support TimeZone on BMC
      ```
      timedatectl list-timezones
      ```
  * Maintainer  
    * Tim Lee

### Intel PCH Time Sync

  Intel PCH provides read commands that BMC get PCH time over i2c.   
  If BMC is connected to PCH, you can use this feature to hanle BMC time.

  **Source URL**
  * [https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/datetime/pch-time-sync](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/datetime/pch-time-sync)

  **How to use**

  1. Please check the [i2c bus and slave address](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/datetime/pch-time-sync/pch-time-sync.cpp#L94)
  2. The serivce will auto sync pch time during openbmc startup.

  **Maintainer**
  * Brain Ma

### Intel Node Manager Get SEL Time

  Intel Node Manager is synchronizing periodically its internal clock with system RTC.  
  It also provides a get sel command that BMC can get time over IPMB interface.   

  **Source URL**
  * [https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/datetime/bmc-time-sync](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/datetime/bmc-time-sync)

  **How to use**
  1. Please follow [Host Power Budget Control](#host-power-budget-control) to setup IPMB interface.
  2. The serivce will auto get time via get sel time command during openbmc startup.

  **Maintainer**
  * Brain Ma

### Inventory

### IPMI FRU
<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/cab7306/openbmc/fru.png">

Field Replaceable Unit. The FRU Information is used to primarily to provide “inventory” information about the boards that the FRU Information Device is located on. In NPCM750, we connect EEPROM component as FRU Information Device to support this feature. Typically, this feature is used by the BMC to "monitor" host server health about H/W copmonents status.

**Source URL**

* [https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/ipmi/phosphor-ipmi-fru_%25.bbappend](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/ipmi/phosphor-ipmi-fru_%25.bbappend)

**How to use**
* Check DTS settings of EEPROM for FRU.
  For example about DTS **nuvoton-npcm750-runbmc-olympus.dts**:
  ```
  i2c4: i2c@84000 {
      #address-cells = <1>;
      #size-cells = <0>;
      bus-frequency = <100000>;
      status = "okay";
      eeprom@54 {
          compatible = "atmel,24c64";
          reg = <0x54>;
      };
  };
  ```

  According DTS modification, you also need to remember modify your EEPROM file description content about **SYSFS_PATH** and **FRUID**. Below is example for our EEPROM file description **[motherboard](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/ipmi/phosphor-ipmi-fru/obmc/eeproms/system/chassis/motherboard)**:
  ```
  SYSFS_PATH=/sys/bus/i2c/devices/4-0054/eeprom
  FRUID=1
  ```
  **SYSFS_PATH** is the path according your DTS setting and **FRUID** is arbitrary number but need to match **Fruid** in **[config.yaml](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/configuration/olympus-nuvoton-yaml-config/olympus-nuvoton-ipmi-fru.yaml)** file. Below is example for when **Fruid** set as 1:
  ```
  1: #Fruid
    /system/chassis/motherboard:
      entityID: 7
      entityInstance: 1
      interfaces:
        xyz.openbmc_project.Inventory.Decorator.Asset:
          BuildDate:
            IPMIFruProperty: Mfg Date
            IPMIFruSection: Board
          PartNumber:
            IPMIFruProperty: Part Number
            IPMIFruSection: Board
          Manufacturer:
            IPMIFruProperty: Manufacturer
            IPMIFruSection: Board
          SerialNumber:
            IPMIFruProperty: Serial Number
            IPMIFruSection: Board
        xyz.openbmc_project.Inventory.Item:
          PrettyName:
            IPMIFruProperty: Name
            IPMIFruSection: Board
        xyz.openbmc_project.Inventory.Decorator.Revision:
          Version:
            IPMIFruProperty: FRU File ID
            IPMIFruSection: Board
  ```

* Server health

  Select `Server health` -> `Hardware status` on **Web-UI** will show FRU Board Info/Chassis Info/Product Info area.

**Maintainer**
* Tim Lee

### SMBIOS
<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/c240503/openbmc/smbios.png">

SMBIOS stands for System Management BIOS while DMI stands for Desktop Management Interface. Both standards are tightly related and developed by the DMTF. The BIOS should provide support according to SMBIOS Reference Specification.

In Nuvoton RunBMC Olympus, the raw DMI table are presented as binary attributes at `/sys/firmware/dmi/tables/DMI`. The format of DMI structures can be read in SMBIOS specification.

  ```
{
    "@odata.id": "/redfish/v1/Systems/system/Processors/cpu0",
    "@odata.type": "#Processor.v1_7_0.Processor",
    "Id": "cpu0",
    "InstructionSet": "x86-64",
    "Manufacturer": "Intel",
    "MaxSpeedMHz": 3600,
    "Model": "",
    "Name": "Central Processor",
    "PartNumber": "",
    "ProcessorArchitecture": "x86",
    "ProcessorType": "CPU",
    "SerialNumber": "",
    "Status": {
        "Health": "OK",
        "State": "Enabled"
    },
    "TotalCores": 6,
    "TotalThreads": 6
}

{
    "@odata.id": "/redfish/v1/Systems/system/Memory/dimm4",
    "@odata.type": "#Memory.v1_6_0.Memory",
    "CapacityMiB": 8192,
    "DataWidthBits": 64,
    "Id": "dimm4",
    "Manufacturer": "Samsung",
    "MemoryDeviceType": "xyz.openbmc_project.Inventory.Item.Dimm.DeviceType.Other",
    "Name": "DIMM Slot",
    "PartNumber": "M393A1G43EB1-CRC    ",
    "SerialNumber": "3961A231",
    "Status": {
        "Health": "OK",
        "HealthRollup": "OK",
        "State": "Enabled"
    }
}
  ```
**Source URL**

This source code is implemented SMBIOS MDR version 2 service [Intel-BMC/mdrv2](https://github.com/Intel-BMC/mdrv2) for Intel based platform that gets SMBIOS binary file `/var/lib/smbios/smbios2` in Nuvoton RunBMC then parses SMBIOS table to update BIOS/CPU/DIMM informations through xyz.openbmc_project.Smbios.MDR_V2 dbus interface.

**How to use**

* Execute `xfer_mbox_mem host` tool in Nuvoton RunBMC Olympus side

  This is a host tool [Nuvoton-BMC/xfer_mbox_mem](https://github.com/Nuvoton-Israel/openbmc-util/tree/master/xfer_mbox_mem) which is used to get SMBIOS table from host, it will copy SMBIOS table to mailbox shared memory for BMC to parse SMBIOS table. The tool usage you can refer to [xfer_mbox_mem](https://github.com/Nuvoton-Israel/openbmc-util/blob/master/xfer_mbox_mem/README.md).

* Refresh `WebUI`

  After service started, you can see the new items `BIOS`, `CPU` and `DIMM` in `WebUI`->`Health`->`Hardware status` page.  
  And you can get `CPU` and `DIMM` from /redfish/v1/Systems/system/Processors/ and /redfish/v1/Systems/system/Memory.

**Maintainer**
* Tim Lee

### Power Supply Unit Inventory

The PMBus protocol provides commands for the storage and retrieval of the device
manufacturer’s inventory information.  
Nuvoton provides a shell script based deaemon that can create `PSU` inventory during OpenBMC startup.

```
"PowerSupplies": [
    {
        "@odata.id": "/redfish/v1/Chassis/chassis/Power#/PowerSupplies/0",
        "EfficiencyPercent": 90,
        "Manufacturer": "FlexPower",
        "MemberId": "powersupply0",
        "Model": "MIS-S-1020",
        "Name": "powersupply0",
        "PartNumber": "FPS-213-D0000293-101",
        "PowerInputWatts": 59.0,
        "PowerOutputWatts": 44.0,
        "SerialNumber": "",
        "Status": {
            "Health": "OK",
            "State": "Enabled"
        }
    }
],
```
**Source URL**

[https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/power/first-boot-set-psu](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/power/first-boot-set-psu)

**How to use**

After service started, you can see a new item `Powersupply 0` in `WebUI`->`Health`->`Hardware status` page.  
And you can get `Powersupply 0` inventory information from /redfish/v1/Chassis/chassis/Power#/PowerSupplies/0.


**Maintainer**
* Joseph Liu

### Network

### VLAN

VLAN (Virtual Local Area Networks) is any broadcast domain that is partitioned and isolated in a computer network at the data link layer (OSI layer 2). VLANs work by applying tags to network frames and handling these tags in networking systems – creating the appearance and functionality of network traffic that is physically on a single network but acts as if it is split between separate networks.

VLANs allow network administrators to group hosts together even if the hosts are not directly connected to the same network switch. Because VLAN membership can be configured through software, this can greatly simplify network design and deployment.

To subdivide a network into VLANs, one configures network equipment. Simpler equipment can partition only per physical port (if at all), in which case each VLAN is connected with a dedicated network cable. More sophisticated devices can mark frames through VLAN tagging, so that a single interconnect (trunk) may be used to transport data for multiple VLANs.

For more information, please refer to [https://en.wikipedia.org/wiki/Virtual_LAN](https://en.wikipedia.org/wiki/Virtual_LAN).

**Source URL**

* [https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-kernel/linux](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-kernel/linux)
* [https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/network](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/network)

For users who prefer ipmi network utilization via VLAN, please refer to the following section **How to use**.

**How to use**

1. Configure VLAN setting of the Olympus platform by inputting the following commands on the Olympus debug console. Please replace the IP addresses mentioned below according to your configuration.
    ```
    ip link add link eth1 name eth1.1 type vlan id 1
    ip addr add 192.168.1.4/24 dev eth1.1
    ip link set up eth1.1
    ```
    If the user prefers the REST method via a remote PC, the commands are provided below as an example.
    ```
    export bmc=192.168.0.3
    export token=`curl -k -H "Content-Type: application/json" -X POST https://${bmc}/login -d '{"username" :  "root", "password" :  "0penBmc"}' | grep token | awk '{print $2;}' | tr -d '"'`
    curl -k -H "X-Auth-Token: $token" -H "Content-Type: application/json" -X POST -d '{"data":["eth1",1] }' https://${bmc}/xyz/openbmc_project/network/action/VLAN
    curl -k -H "X-Auth-Token: $token" -H "Content-Type: application/json" -X POST -d '{"data": ["xyz.openbmc_project.Network.IP.Protocol.IPv4", "192.168.1.4",24,"192.168.1.1"] }' https://${bmc}/xyz/openbmc_project/network/eth1_1/action/IP
    ```

    The expected response is listed below as an example.
    ```
    {
      "data": "/xyz/openbmc_project/network/eth1_1/ipv4/a3f185f1",
      "message": "200 OK",
      "status": "ok"
    }
    ```

    For more information, please refer to [https://github.com/openbmc/phosphor-networkd/blob/master/docs/Network-Configuration.md](https://github.com/openbmc/phosphor-networkd/blob/master/docs/Network-Configuration.md).

2. Prepare a PC which runs Ubuntu (for example) or any OS which supports VLAN. The Olympus device and the PC are connected to an unmanaged network switch.  

   Please input the following commands in a opened terminal emulator with a privileged user account to configure the VLAN setting of the Ubuntu PC.  

   Here the network interface enp24s0f3 of the Ubuntu PC is used as an example. 
    ```
    sudo modprobe 8021q
    sudo ip link add link enp24s0f3 enp24s.1 type vlan id 1
    sudo ip addr add 192.168.1.5/24 dev enp24s.1
    sudo ip link set up enp24s.1
    ```

3. Ping either from Olympus or the PC to the other device.

**Maintainer**
* Tyrone Ting

### BIOS POST Code
In NPCM750, we support a FIFO for monitoring BIOS POST Code. Typically, this feature is used by the BMC to "watch" host boot progress via port 0x80 writes made by the BIOS during the boot process.

**Source URL**

This is a patch for enabling BIOS POST Code feature in [phosphor-host-postd](https://github.com/openbmc/phosphor-host-postd) on Nuvoton's NPCM750.

* [https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/host/phosphor-host-postd_%25.bbappend](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/host/phosphor-host-postd_%25.bbappend)

**How to use**

* Execute BIOS POST Code test program by command in BMC
  ```
  snooper
  ```

  This command will trigger snooper test program to record BIOS POST Code from port 0x80 of host and save to file with timestamp filename in BMC for each host power on or reset.
  > _Saved filename format example: 2019_4_30_11_52_35_ON_

* Server Power on

  Press `Power on` button from `Server control` ->`Server power operations` of WebUI.
  During server power on, snooper test program will print received BIOS POST Code on screen and record to file in BMC at the same time.
  > _Snooper test program print received BIOS POST Code example:_
    > _recv: 0x3
        recv: 0x2
        recv: 0x7_

**phosphor-post-code-manager**

* This daemon [phosphor-post-code-manager](https://github.com/openbmc/phosphor-post-code-manager) will monitors post code posted on dbus interface /xyz/openbmc_project/state/boot/raw by snoopd daemon [phosphor-host-postd](https://github.com/openbmc/phosphor-host-postd).

* Every cycle post codes are saved as file in **/var/lib/phosphor-post-code-manager** on BMC.
  ```
  "1" file is saved all post codes for cycle 1
  "2" file is saved all post codes for cycle 2
  "CurrentBootCycleIndex" file is saved the current boot cycle number
  "CurrentBootCycleCount" file is saved the current boot cycle count
  ```

* GetPostCodes

  Method to get the cached post codes of the indicated boot cycle.
  Return an array of post codes of one boot cycle.  
  Get boot cycle 1 post codes by busctl:
  > _busctl call xyz.openbmc_project.State.Boot.PostCode /xyz/openbmc_project/State/Boot/PostCode xyz.openbmc_project.State.Boot.PostCode GetPostCodes q 1_

* GetPostCodesWithTimeStamp

  Method to get the cached post codes of the indicated boot cycle with timestamp.
  Return an array of post codes and timestamp in microseconds since epoch.  
  Get boot cycle 1 post codes with timestamp by busctl:
  > _busctl call xyz.openbmc_project.State.Boot.PostCode /xyz/openbmc_project/State/Boot/PostCode xyz.openbmc_project.State.Boot.PostCode GetPostCodesWithTimeStamp q 1_

* Get POST codes through Redfish

  BIOS Power-On Self-Test (POST) codes are exposed on DBUS but not currently over Redfish. This describes a method to expose the BIOS POST codes over the Redfish interface using the logging service.  
  Get boot cycle 1 post codes by Redfish URL for example:
  > _[https://{BMC_IP}/redfish/v1/Systems/system/LogServices/PostCodes/Entries](https://{BMC_IP}/redfish/v1/Systems/system/LogServices/PostCodes/Entries)_

**Maintainer**
* Tim Lee


### IPMI SOL
<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/8afa8a2/openbmc/sol_ipmi_win10.PNG">

The Serial over LAN (SoL) via IPMI redirects the output of the server’s serial port to a command/terminal window on your workstation.

The user uses the ipmi tool like [ipmiutil](http://ipmiutil.sourceforge.net/) to interact with SOL via IPMI. Here the ipmiutil is used as an example.

The patch integrates [phosphor-net-ipmid](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/ipmi/phosphor-ipmi-net_%25.bbappend) into Nuvoton's NPCM750 solution for OpenBMC.

**Source URL**

* [https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/ipmi/phosphor-ipmi-net_%25.bbappend](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/ipmi/phosphor-ipmi-net_%25.bbappend)

**How to use**

1. Download the [ipmiutil](http://ipmiutil.sourceforge.net/) according to the host OS in your workstation.

   > _Here it's assumed that the host OS is Windows 10 and ipmiutil for Windows is downloaded and used accordingly._

2. Run SOL:

    * Extract or install the ipmiutil package to a folder in your workstation in advance.
    * Launch a command window and navigate to that folder.
    * Input the following command in the command window.
      ```
      ipmiutil sol -N 192.168.0.2 -U root -P 0penBmc -J 3 -V 4 -a
      ```
    * (Optional) Configure the `Properties` of the command window to see the entire output of SOL.
      > _Screen Buffer Size Width: 200_
        _Screen Buffer Size Height: 400_
        _Window Size Width: 100_
        _Window Size Height: 40_

4. End SOL session:

    * Press the "~" key (using the shift key + "`" key) and "." key at the same time in the command window.
    * Input the following command in the command window.
      ```
      ipmiutil sol -N 192.168.0.2 -U root -P 0penBmc -J 3 -V 4 -d
      ```

**Maintainer**

* Tyrone Ting
* Stanley Chu

### Host Power Budget Control

Host Power Budget Control provides a mechanism for implement IPMI DCMI messages by using D-Bus and IPMB.

Please refer to [IPMI Website](https://www.intel.com/content/www/us/en/products/docs/servers/ipmi/ipmi-home.html) for details about Message Bridging.

<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/dfdfd04/openbmc/message_bridge.png">

IPMI daemon would set or get D-Bus property to/from path like: `/xyz/openbmc_project/control/host0/power_cap` when get DCMI command from IPMI tool via network or LPC.
[Phosphor-node-manager-proxy](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-x86/node-manager) will handle the property change and present the property value.
Once the **phosphor-node-manager-proxy** property changed, it will prepare IPMB package data and call D-Bus method "sendRequest" to D-Bus path: `/xyz/openbmc_project/Ipmi/Channel/Ipmb`.
Finally, IPMB gets the request and constructs I2C command from the request, then sends the I2C command to ME(Intel Management Engine) for getting information which controlled by ME.

The patch integrates the [ipmid](https://github.com/openbmc/phosphor-host-ipmid), [phosphor-node-manager-proxy](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-x86/node-manager) and [ipmbbridge](https://github.com/openbmc/ipmbbridge) projects.


**Source URL**

* [https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/ipmi](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/ipmi)

**How to use**

1. Configure [ipmb-channel.json](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/ipmi/phosphor-ipmi-ipmb/ipmb-olympus-channels.json) as below sample. Check schematic to fill up BMC and ME I2C address, and each sys path.
    ```
    "channels": [
      {
        "type": "me",
        "master-path": "/dev/i2c-5",
        "slave-path": "/dev/ipmb-5",
        "bmc-addr": 32,
        "remote-addr": 44
      }
    ]
    ```

2. Deploy **phosphor-node-manager-proxy** from Intel-BMC project [node-manager](https://github.com/Intel-BMC/node-manager), because the package still not merge in OpenBMC project. And modify the interface define if need. There are few patches [Patch](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/ipmi/phosphor-node-manager-proxy) for node manager that makes bmcweb can get the sensor value.

3. Test power budget control.

    * Send DCMI commnad via LPC
      ```
      sudo ipmitool dcmi get_mc_id_string
      ```

      This command would return result as below:

      ```
        Get Management Controller Indentifier String: olympus-nuvoton
      ```

    * Send DCMI command via network
      ```
      sudo ipmitool -I lanplus -H 10.103.152.12 -U root -P 0penBmc dcmi power get_limit
      ```

      The result will show like below if everything is OK.

      ```
      Current Limit State: Power Limit Active
      Exception actions:   Hard Power Off & Log Event to SEL
      Power Limit:         500 Watts
      Correction time:     0 milliseconds
      Sampling period:     0 seconds
      ```

**Maintainer**

* Tyrone Ting
* Stanley Chu

### PECI

#### Intel Crash Dump
<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/5f39468/openbmc/crashdump.png">

The Intel Crash Dump feature facilitates debug of an IERR triggered and leverages uncore hardware features accessible via the PECI interface to provide a scheme to dump the Caching agent (Cbo) Table of Requests (TOR) structure.

**Source URL**

This source code is implements CrashDump for the Whitley platform and supports the Ice Lake Server and Cooper Lake Server processors in private github [Intel-BMC/crashdump](https://github.com/Intel-BMC/crashdump/) that collects debug data from the processors over the PECI bus when executed.

**How to use**

* There are two ways to execute Crash Dump
  ```
  On-Demand (Trigger by Redfish)
  ```

  You can use below Redfish curl to trigger Crash Dump by CollectDiagnosticData POST action.  
  This action is used to trigger a new On-Demand Crash Dump is returned immediately.  
  Currently, **DiagnosticDataType** only support **OEM** this parameter for Crashdump.  
  However, **OEMDiagnosticDataType** support **OnDemand** and **OnTelemetry** these parameters.
  > _curl -k -H "X-Auth-Token: $token" -X POST https://${bmc}/redfish/v1/Systems/system/LogServices/Crashdump/Actions/LogService.CollectDiagnosticData -d '{"DiagnosticDataType":"OEM", "OEMDiagnosticDataType": "OnDemand"}'_

  Eventually, there is crashdump file will exist in /tmp/crashdump/output folder on BMC.  
  And **trigger_type** content in crashdump file is **On-Demand** that means the Crash Dump daemon be called by On-Demand.

  You can use below busctl command to verify Crash Dump by On-Demand.
  > _busctl call com.intel.crashdump /com/intel/crashdump/OnDemand org.freedesktop.DBus.Properties Get ss "com.intel.crashdump" "Log"_

  ```
  Direct call upon detection of an Error Event (Trigger by Intel Host Error Monitor)
  ```

  The Host Error Monitor [OpenBMC/host-error-monitor](https://github.com/openbmc/host-error-monitor) daemon is use to detect an host error event then trigger Crash Dump dameon be executed. And **trigger_type** content in crashdump file is **IERR** that means the Crash Dump daemon be called upon the BMC detecting an host error event.

* There are two ways to get crashdump file
  ```
  Redish
  ```

  Redfish url to get crashdump entries.  
  You can get crashdump file **crashdump_xxx.json** by clicking **Message** link directly.
  > _https://{bmc_ip}/redfish/v1/Systems/system/LogServices/Crashdump/Entries_

  ```
  WebUI
  ```

  Select `Event log` and select `Oem` in `Select system log type` on **WebUI**.  
  You can get crashdump file by clicking **Export** button.

**CScripts Crash Dump**

  CScripts (Customer Scripts) are a collection of scripts provided by Intel to assist customers with platform debug and validation. This is another method use CScripts function crashdump() to collect crash records manually. Use RunBMC Olympus as example:

**How to use**

* Configuring ASD on BMC and OpenIPC on Host (refer [Intel At-Scale Debug](#intel-at-scale-debug))

  Make sure Host CPU can be halted and resumed by executing itp.halt and itp.go command in cscripts folder.
    > _>>> itp.halt_  
    > _>>> itp.go_

* Inject 3Strike Error

  Injects a Three Strike Counter timeout, resulting in Machine Check Exception (MCE) and IERR.  
  Execute ei.injectThreeStrike command in cscripts folder
    > _>>> ei.injectThreeStrike_

* Collect crash records

  The crashdump feature allows the user to retrieve internal state of the cores after a 3Strike failure.  
  Collected data will be stored in JSON format name as **crashdump.json**  
  Execute crashdump() command in cscripts folder
    > _>>> crashdump()_

  The crashdump_decode_analyze feature allows the user to analyze any crashdump JSON file.  
  Analysis data will be stored in JSON format name as **crasdump_decoded.json**  
  Execute crashdump() command in cscripts folder
    > _>>> crashdump_decode_analyze(crashdump.json)_

* Host BIOS configurations

  If you got unexpected command error from **ei.injectThreeStrike**, please check your BIOS settings.  
  Processor config space there is a setting to lock or unlock the chipset.  
  For error injection to work properly this setting must be set to unlocked or lock disabled.  
  Please see your BIOS vendor of choice for where this setting.  
  Below are Olympus SKX model Host BIOS settings as example:

    > BIOS -> Platform Configuration -> PCH Configuration -> PCH DFX  
    > **Enable Unlock All PCH registers**

    > BIOS -> Socket Configuration -> Processor Configuration  
    > **Enable 3StrikeTimerSocket**  
    > **Disable Lock Chipset**  
    > **Disable MSR Lock Control**

**Maintainer**
* Tim Lee

# LDAP for User Management
<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/b6fdec0d/openbmc/ldap-login-via-ssh.png">
<img align="right" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/access_ldap_via_bmc.png">

The Lightweight Directory Access Protocol (LDAP) is an open, vendor-neutral, industry standard application protocol for accessing and maintaining distributed directory information services over an Internet Protocol (IP) network.

LDAP is specified in a series of Internet Engineering Task Force (IETF) Standard Track publications called Request for Comments (RFCs), using the description language ASN.1.

A common use of LDAP is to provide a central place to store usernames and passwords. This allows many different applications and services to connect to the LDAP server to validate users.

**Source URL**

* [https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-phosphor/nuvoton-layer/dlc/ldap-support-user-management](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-phosphor/nuvoton-layer/dlc/ldap-support-user-management)
* [https://github.com/Nuvoton-Israel/openbmc-util/tree/master/ldap_server](https://github.com/Nuvoton-Israel/openbmc-util/tree/master/ldap_server)

### LDAP Server Setup

**How to use**

1. The user is expected to know how to follow the instructions in the section **Setting up your OpenBMC project** in [Nuvoton-Israel/openbmc](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc) to build and program an OpenBMC image into NPCM750 platforms.
    > _Prepare a PC which builds OpenBMC. (called the build machine hereafter)_  
    > _The user is also expected to have knowledge of LDAP and its operations._

2. Install Ubuntu 16.04 64 bit (called Ubuntu hereafter) on a PC which is used as a LDAP server and log in it with an account with root privilege.

3. Set up the LDAP server and its configurations in Ubuntu. 

    * Open a terminal and input the following commands to install required software packages in advance.

      ```
      sudo apt-get install git
      sudo apt-get install libsasl2-dev
      sudo apt-get install g++
      wget http://download.oracle.com/berkeley-db/db-4.8.30.zip
      unzip db-4.8.30.zip
      cd db-4.8.30
      cd build_unix/
      ../dist/configure --prefix=/usr/local --enable-cxxmake
      sudo make install
      ```
    * Install OpenSSL
      + Download [openssl-1.0.2j.tar.gz](https://ftp.openssl.org/source/old/1.0.2/openssl-1.0.2j.tar.gz).
      + Extract openssl-1.0.2j.tar.gz.
      + Open a terminal, navigate to the extracted folder and input the following commands to install OpenSSL.
        ```
        ./config shared --prefix=/usr/local
        make
        make test
        sudo make install
        ```
    * Install OpenLDAP
      + Download OpenLDAP from [https://github.com/openldap/openldap](https://github.com/openldap/openldap)

        > _git clone https://github.com/openldap/openldap_

      + Open a terminal and input the following command to build and install OpenLDAP.
        ```
        ./configure CPPFLAGS="-I/usr/local/include -I/usr/local/include/openssl" LDFLAGS="-L/usr/local/lib -Wl,-rpath,/usr/local/lib" --prefix=/usr/local  --enable-syncprov=yes --enable-crypt=yes --enable-accesslog=yes --enable-auditlog=yes --enable-constraint=yes --enable-ppolicy=yes --enable-modules --enable-mdb --enable-spasswd --enable-debug=yes --enable-syslog --enable-slapd --enable-cleartext --enable-monitor --enable-overlays -with-threads --enable-rewrite --enable-syncprov=yes --with-tls=openssl 
        ```
          > _The description above is one line only._

        ```
        make depend 
        make
        sudo make install
        ```
    * Execute LDAP server
      + Open a terminal and input the following command.

        ```
        sudo /usr/local/libexec/slapd -d 1 -h 'ldaps:/// ldap:/// ldapi:///'
        ```
          > _To stop LDAP server execution, press Ctrl key and C key at the same time in the terminal._  
          > _Now please stop the LDAP server execution._

    * Generate security configurations for the LDAP server running in Ubuntu.
      > _Here a two-stage signing process is applied._  
      > _You could also use the self-signed CA and cert for the configuration if your company uses them._

      + Generate the CA key and cert. Open a terminal and input the following commands.
        ```
        openssl genrsa -out ca_server.key 2048  
        openssl req -x509 -new -key ca_server.key -days 3650 -out ca_server.pem -subj '/C=OO/ST=OO/L=OO/O= OO/OU= OO /CN= OO'
        ```

        > _Define these **OO** for the arguments **C**, **ST**, etc. according to your configurations._  
        > _Please refer to the following link for explanations of the arguments **C**, **ST**, etc._  
        > _[https://www.shellhacks.com/create-csr-openssl-without-prompt-non-interactive/](https://www.shellhacks.com/create-csr-openssl-without-prompt-non-interactive/)._

      + Generate the LDAP key and CSR. In the same terminal, input the following commands.
        ```
        openssl genrsa -out ldap_server.key 2048  
        openssl req -new -key ldap_server.key -out ldap_server.csr -subj '/C=OO /ST=OO /L=OO/O=OO/OU=OO/CN=ldap.example.com'
        ```

        > _Define these **OO** for the arguments **C**, **ST**, etc. according to your configurations._  
        > _Note that the field **CN** in ldap_server.csr must be set to the fully qualified domain name of the LDAP server._

      + Generate ldap cert signed with CA cert. In the same terminal, input the following command.
        ```
        openssl x509 -req -days 365 -CA ca_server.pem -CAkey ca_server.key -CAcreateserial -CAserial serial -in ldap_server.csr -out ldap_server.pem
        ```

    * Store and specify locations of keys and certs.
      + Edit /usr/local/etc/openldap/slapd.conf in Ubuntu with root privilege to update fields as examples shown below.
        > _TLSCACertificateFile /etc/ldap/ca_server.pem_  
        > _TLSCertificateFile /etc/ssl/certs/ldap_server.pem_  
        > _TLSCertificateKeyFile /etc/ssl/private/ldap_server.key_  
      
      + Copy ca_server.pem, ldap_server.pem and ldap_server.key into locations specified above with root privilege.

    * Add LDAP schema and LDIF (LDAP Data Interchange Format).
      + Download [user_exp.schema](https://github.com/Nuvoton-Israel/openbmc-util/blob/master/ldap_server/schema/user_exp.schema) and save it at /usr/local/etc/openldap/schema with root privilege in Ubuntu.
      + Modify /usr/local/etc/openldap/slapd.conf in Ubuntu with root privilege to specify the schema just saved.
        > _include /usr/local/etc/openldap/schema/user_exp.schema_

      + Download [bdn.ldif](https://github.com/Nuvoton-Israel/openbmc-util/blob/master/ldap_server/ldif/bdn.ldif), [ap_group.ldif](https://github.com/Nuvoton-Israel/openbmc-util/blob/master/ldap_server/ldif/ap_group.ldif), [bmc.ldif](https://github.com/Nuvoton-Israel/openbmc-util/blob/master/ldap_server/ldif/bmc.ldif), [group.ldif](https://github.com/Nuvoton-Israel/openbmc-util/blob/master/ldap_server/ldif/group.ldif), [people.ldif](https://github.com/Nuvoton-Israel/openbmc-util/blob/master/ldap_server/ldif/people.ldif) and [privRole.ldif](https://github.com/Nuvoton-Israel/openbmc-util/blob/master/ldap_server/ldif/privRole.ldif) to a temporary folder in Ubuntu.
      + Open a terminal, navigate to the temporary folder for storing LDIF and input the following commands to add these LDIF into the LDAD server in Ubuntu.
        ```
        sudo slapadd -l ./bdn.ldif
        sudo slapadd -l ./ap_group.ldif
        sudo slapadd -l ./bmc.ldif
        sudo slapadd -l ./group.ldif
        sudo slapadd -l ./people.ldif
        sudo slapadd -l ./privRole.ldif
        ```

    * Execute LDAP server.
      + Open a terminal and input the following command in the terminal.
      ```
      sudo /usr/local/libexec/slapd -d 1 -h 'ldaps:/// ldap:/// ldapi:///'
      ```

4. Setup LDAP client configuration on BMC.

    * Open a terminal in the build machine and navigate to the directory which contains OpenBMC source codes. The directory is called **OPENBMCDIR** hereafter.
      + Copy all directories and their containing files from [https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-phosphor/nuvoton-layer/dlc/ldap-support-user-management](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-phosphor/nuvoton-layer/dlc/ldap-support-user-management) under OPENBMCDIR/meta-quanta/meta-runbmc-nuvoton directory according to their default hierarchy.
        > _Nuvoton RunBMC Olympus is used for the LDAP demonstration so the corresponding runbmc software layer is applied here._

    * Update OPENBMCDIR/meta-quanta/meta-runbmc-nuvoton/recipes-support/nss-pam-ldapd/files/nslcd.conf. (optional)
      + The IP address for the LDAP server in Ubuntu is configured as **10.103.152.11**. Modify the field **uri ldap** in nslcd.conf according to your network configuration.
        > _uri ldap://10.103.152.11/_

      + The modification above is done in OpenBmc build time. If you would like to modify **uri** in OpenBmc run time, follow the instructions below after logging into BMC in the console program (like Tera Term) with the root account (root/0penBmc).
        > _The console program is used to display a debug console provided by BMC._

        ```
        vi /etc/nslcd.conf
        ```

        > _Locate the line **uri ldap://10.103.152.11/**. Modify the field **uri ldap** according to your network configuration._

        ```
        systemctl stop nslcd
        systemctl start nslcd
        ```

    * In the build machine, open a terminal window (build environment is configured in advance and the working directory is at OPENBMCDIR/build) to input the following commands to build the OpenBMC image.
      ```
      bitbake -C fetch nss-pam-ldapd
      bitbake -C fetch dropbear
      bitbake -C fetch libpam
      bitbake -C fetch bmcweb
      bitbake -C fetch phosphor-ipmi-net
      bitbake -C fetch openldap
      bitbake obmc-phosphor-image
      ```

      > _Without these patches provided by Nuvoton, the privilege mapping and authentication procedure applied are predefined by OpenBMC._

    * Program the updated image into BMC.

5. Test LDAP server.

    * Connect BMC and LDAP server in same local network and power on it.
    * Log in BMC from the console program (like Tera Term) with the root account (root/0penBmc).
      > _The console program is used to display a debug console provided by BMC._

    * The IP address for the LDAP server is 192.168.0.101 for now.
    * Set up IP addresses for BMC and LDAP server so that they can ping each other.
      + For example, set BMC's IP address to 192.168.0.2. Input the following command in the console program.
        ```
        ifconfig eth2 192.168.0.2
        ```

        > _Please replace **192.168.0.2** with your IP configuration for BMC._

    * Execute the following command in the console program.
      ```
      ldapsearch -ZZ -h 192.168.0.101 -D "cn=admin,dc=ldap,dc=example,dc=com" -b "dc=ldap,dc=example,dc=com" -w secret
      ```
      > _Please replace **192.168.0.101** with your IP configuration for LDAP server._
      > _The ldapsearch example is to display all the data stored in the LDAP server using a TLS connection._

    * You could use the account **user1** stored in the LDAP server to log in WebUI running on BMC.

      + Some descriptions about the LDIF used by the LDAP server and authentication process are provided here. Please refer to the six snapshots in the following description.
        > _To login an account, the authentication logic has to check the following criteria._
        > _**bmc-uid**: It stands for the BMC machine that the account is used to login. The BMC machines are grouped by DN **ou=ap_group,dc=ldap,dc=example,dc=com**. One BMC machine can be in multiple groups at the same time. (see **ap_group** below)_
        <img width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/c8c60b29/openbmc/bmc1_info.png">
        <img width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/c8c60b29/openbmc/bmc2_info.png">
        
        > _**ap_group**: Applications like web server, email, ftp and so on are deployed on the servers attched by BMC machines. Therefore, grouping by applications is taken into the authentication process. The authentication refuses an account to log in some BMC machine if that machine is not deployed under the certain **ap_group**_
        <img width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/c8c60b29/openbmc/email_info.png">
        <img width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/c8c60b29/openbmc/webserver_info.png">
        
        > _**people**: It contains the account information (login/privileges) stored in the LDAP server. An account can join multiple **ap_group** simutaneously._
        <img width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/c8c60b29/openbmc/user1_logininfo.png">
        <img width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/c8c60b29/openbmc/user2_logininfo.png">
        
        > _**user-login-disabled**: While this attribute's value is 1, it is not allowed to login with the account's membership of the specific **ap_group**_.
        > _**user-login-interface**: It's used as a channel via that the account logins for an **ap_group**. For example, **web** stands for logging in a BMC machine via WebUI. If **web** does not exist in any **user-login-interface** attributes an account owns under a certain ap_group, it means that the user cannot use this account to login as a member of the preferred ap_group via WebUI._

      + Use an LDAP tool to modify the field **macAddress** of the DN **bmc-uid=bmc1,ou=bmc,dc=ldap,dc=example,dc=com** stored in the LDAP server.
        > _The mac address modification is for the ethernet module on BMC you currently test with._

      + To get the mac address desired, input the following command in the console program.
        ```
        ifconfig eth0
        ```
        > _Locate the keyword **HWaddr** displayed in the console program._
        > _Copy the value next to HWaddr to override the value of the field **macAddress** of the DN **bmc-uid=bmc1,ou=bmc,dc=ldap,dc=example,dc=com**._

      + Launch a browser and navigate to the BMC's IP address.
        > _Bypass the secure warning and continue to the website._

      + Use user1/123 to log in WebUI.
        > _user1 is the login ID._
        > _123 is the login password._  
        > _The **bmc-uid** for the BMC machine used for this test is bmc1. According to the LDIF provided, the BMC machine bmc1 is deployed under the **ap_group** email and the the BMC machine bmc2 is deployed under **ap_group** webserver._  
        > _Also one can tell from the snapshots, user1 and user2 have different **user-login-interface** settings for the **ap_group** email and **ap_group** webserver respectively._  
        > _User1 is able to log on bmc1 via WebUI since the following conditions are met: the BMC machine bmc1 is deployed under **ap_group** email.; user1 is a member of the **ap_group** email.; user1 has an **user-login-interface** setting as **web** for that group and value of user1's **user-login-disabled** attribute is not set._  
        > _Although user2 is also a member of the **ap_group** email, it does not have an **user-login-interface** setting as **web** for that group. Under such conditions, user2 is not allowed to log on bmc1._  
        > _User2 does have an **user-login-interface** setting as **web** for the **ap_group** webserver but bmc1 is not deployed under the **ap_group** webserver._  
        > _The description above explains why user1 is used for this test._

    * Password modification is also available to LDAP accounts via WebUI.
      + Log in WebUI using user1/123 as mentioned in previous section.
      + Navigate to `Access control` menu item on the left panel and select it.
      + A sub menu item `Local users` pops up and select it.
      + Modify the password value for user1 by selecting the icon at just the right side of the text area "Account status".

      + Input the same new password twice.
        > _The input locations are right below **USER PASSWORD** and **CONFIRM USER PASSWORD** text area._

      + Press the `Save` button.
        > _A message **Success! User has been updated successfully.** is expected to show then._

      + Log out WebUI and log in again with the new password for user1.

    * Log in BMC via SSH using an LDAP account.
      + Make sure that configurations stated in Step 5 for BMC and LDAP server are set accordingly and ping between LDAP server and BMC is okay.
      + Install **ssh** in Ubuntu with root privilege if ssh client is not available. Open a terminal and input the following command.
        ```
        sudo apt-get install ssh
        ```

      + Open a terminal in Ubuntu to log in BMC using the LDAP account **user1** and its password via SSH. Input the following command in the terminal.
        ```
        ssh user1@192.168.0.2
        ```
        > _Please replace **192.168.0.2** with your IP configuration for BMC._
        > _It requires the account to be in the priv-admin group in the LDAP group database for accessing SSH._  
        > _Please refer to [group.ldif](https://github.com/Nuvoton-Israel/openbmc-util/blob/master/ldap_server/ldif/group.ldif) for more details._

    * Execute ipmi commands using an LDAP account.
      + Make sure that configurations stated in Step 5 for BMC and LDAP server are set accordingly and ping between LDAP server and BMC is okay.
      + Install **ipmitool** in Ubuntu with root privilege for the demonstration purpose. Open a terminal and input the following command.
        ```
        sudo apt-get install ipmitool
        ```

      + Open a terminal in Ubuntu to execute ipmi commands to BMC using the LDAP account **user2** and its password **123** via ipmitool. Input the following command in the terminal.
        ```
        sudo ipmitool -H 192.168.0.2 -U user2 -P 123 -I lanplus user list
        ```
        > _Please replace **192.168.0.2** with your IP configuration for BMC._
        > _Only limited ipmi commands are supported._


**LDAP user authentication and privilege mapping in OpenBMC**

The **How to use** part above is to introduce the Nuvoton customized authentication mechanism in addition to the LDAP server setup procedure.

In OpenBMC, PAM based authentication is implemented, so for both LDAP users and local users, the authentication flow is the same. For the LDAP user accounts, there is no LDAP attribute type that corresponds to the OpenBMC privilege roles. The preferred way is to group LDAP user accounts into LDAP groups. D-Bus API is provided for the user to assign privilege role to the LDAP group.

This section is to brief some characteristics about the LDAP user authentication and privilege mapping which are currently implmented in OpenBMC. For more information, please refer to [user_management.md](https://github.com/openbmc/docs/blob/master/architecture/user_management.md#ldap).

1. Openbmc's LDAP user authentication is fulfilled by [bmcweb](https://github.com/openbmc/bmcweb), [libpam](https://github.com/openbmc/openbmc/tree/master/meta-phosphor/recipes-extended/pam) and [nss-pam-ldapd](https://github.com/openbmc/meta-phosphor/tree/master/recipes-support/nss-pam-ldapd). 

2. By calling the function **pamAuthenticateUser**, bmcweb is able to authenticate the user from the ldap server via the pam configuration file **webserver** , the libpam module and nss-pam-ldapd.

3. The pam configuration file **webserver** requires the user to pass the predefined authentication criteria as described below.
    > _auth     include  common-auth_.  
    > _auth     required pam_succeed_if.so user ingroup redfish_.

4. The pam_succeed_if pam modules requires the login user to be a member of the **redfish** group.
    > _For more information, please refer to https://linux.die.net/man/8/pam_succeed_if_.  
    > _The snapshot below shows how the users are added in the **redfish** group via the **memberUid** column_.
    <img width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/redfish_group_user.PNG">

5. The patch [pam_succeed_if_support_ldap_user_login.patch](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-extended/pam/libpam/pam_succeed_if_support_ldap_user_login.patch) provided by Nuvoton is to deal with the group name/id conflict when there are same group name/id between the **/etc/group** and ldap server for passing the pam_succeed_if module check.

6. The privilege role is a property of the user D-Bus object for the local users. For the LDAP user accounts, the privilege role will be based on the LDAP group. The LDAP group to privilege role mapping needs to be configured prior to authenticating with the LDAP user accounts. The following two example images show how to configure privilege role mapping and the result and the configuration steps are provided as well.

    <img width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/create_mapping.PNG">
    <img width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/mapping_example.PNG">

    > _Log in WebUI using root/0penBmc and navigate to `Access` menu item on the left panel_.  
    > _A sub menu item `LDAP` pops up and select it_.  
    > _Enable the checkbox `Enable LDAP authentication` on the right panel and set up the LDAP configuration in OpenBMC according to your LDAP server setting_.  
    > _Press `Save` button to enable the LDAP service_.  
    > _The `Add role group` button is available then and select it_.  
    > _A dialogue window `Add new role group` pops up, fill the group name and select the corresponding privilege according to your requirement_.  
    > _Press `Save` button of the dialogue window to create the privilege role mapping for the LDAP group just filled_.  
    > _Check if the privilege for a LDAP user is mapped properly by logging into WebUI with the account in the LDAP group just filled_.  
    > _If the account doesn't act as a Administrator, some pages inside the WebUI are not available_.

    > _To get privilege mapping via dbus, the user can input the following command in the debug console and get the output as shown by the following example image. The object path **/xyz/openbmc_project/user/ldap/openldap/role_map/1** is an example here_.  
        ```
        busctl introspect xyz.openbmc_project.Ldap.Config /xyz/openbmc_project/user/ldap/openldap/role_map/1
        ```

    <img width="50%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/dbus_mapping_example.PNG">

7. An excerpt of [user_management.md](https://github.com/openbmc/docs/blob/master/architecture/user_management.md#authorization-flow) about the ldap user's privilege is provided here, explains how the privilege roles of the user accounts are consumed by the webserver interface and some description is added.

    * Invoke PAM API for authenticating with user credentials. Proceed, if the authentication succeeds.
    * Check if the user is a local user account. If the user account is local, fetch the privilege role from the D-Bus object and update the session information.
    * If the user account is not local, read the group name for the user.
    * Fetch the privilege role corresponding to the group name, update the session information with the privilege role.
      + To deal with the group name/id conflict between **/etc/group** and the ldap server, a patch [0001-meta-quanta-meta-olympus-nuvoton-phosphor-user-manag.patch](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/users/phosphor-user-manager/0001-meta-quanta-meta-olympus-nuvoton-phosphor-user-manag.patch) is provided by Nuvoton.
    * If there is no mapping for group name to privilege role, default to user privilege role for the session.
    * The following images display the LDAP settings for a LDAP user (e.g.: user1) to act as a role of Administrator.
      + The **gidNumber** attribute for the account **user1** and the group **priv-admin** has to be identical.
      + Along with the patch [0001-meta-quanta-meta-olympus-nuvoton-phosphor-user-manag.patch](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/users/phosphor-user-manager/0001-meta-quanta-meta-olympus-nuvoton-phosphor-user-manag.patch), the user could avoid the conflict of the same group name like **priv-admin** between /etc/group and the ldap server.

    <img width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/priv_admin_group_user.PNG">
    <img width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/user_priv_mapping_example.PNG">
    

**Maintainer**

* Tyrone Ting


## JTAG Master
JTAG master is implemented on BMC to facilitate debugging host CPU or programming CPLD device.  

**Source URL**

* [https://github.com/Intel-BMC/asd](https://github.com/Intel-BMC/asd)
* [https://github.com/Nuvoton-Israel/openbmc-util/tree/master/loadsvf](https://github.com/Nuvoton-Israel/openbmc-util/tree/master/loadsvf)

### Intel At-Scale Debug
<img align="right" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/870d09e/openbmc/asd.png">
The Intel® At-Scale Debug feature allows for using any host system to run the Debug tool stack while connecting to the target system across the network. The target system must have a BMC which has physical connectivity to the JTAG pins as a minimum requirement of functionality.    

**How to use**

Here uses the RunBMC Olympus server as example.

1. switch JPC1 jumper on host to 2-3.
2. configure GPIO40(BMC_XDP_JTAG_SEL_N) to connect BMC JTAG to Host CPU
    ```
    echo 40 > /sys/class/gpio/export
    echo 0 > /sys/class/gpio/gpio40/value
    ```
3. Run ASD daemon on BMC
    ```
    asd -u -n eth1 --log-level=warning -p 5123
    ```
4. Launch CScripts with OpenIPC on debug host
    ```
    Assume CScripts source folder = $CS, OpenIPC source folder = $OIPC
    Edit $OIPC/openipc/Config/SKX/SKX_ASD_RC-Pins.xml
    - Change ip address to BMC ip address
    Edit $OIPC/openipc/Config/OpenIpcConfig.xml
    - Change DefaultIpcConfig tag as <DefaultIpcConfig Name="SKX_ASD_RC-Pins"/>
    export IPC_PATH=$OIPC/openipc/Bin
    export LD_LIBRARY_PATH=$IPC_PATH
    Go to $CS/cscripts, execute "python startCscripts.py -a ipc"
    ```
5. Execute OpenIPC idcode operation in CScripts command prompt. It will show the TAP device's idcode.
    ```
    >>> import ipccli
    >>> ipc = ipccli.baseaccess()
    >>> ipc.idcode(0)
        '0x3A114013'
    ```
6. Host must be power cycled before executing debug command. Shutdown/Power on host to power cycle host through BMC WebUI.
    ```
    >>> itp.halt
        [GPC core group] Halt on 12 devices (successful return message)
    >>> itp.go
        [GPC core group] Resuming on 12 devices (successful return message)
    ```

### CPLD Programming
The motherboard on server have a CPLD device that can be upgraded firmware on it. BMC can load svf file to program CPLD via JTAG.  

**How to use**

run loadsvf on Runbmc to program CPLD. Specify the svf file name with -s.
```
loadsvf -d /dev/jtag0 -s firmware.svf
```

**Maintainer**
* Stanley Chu

## BMC / BIOS In-Band Firmware Update
This is a secure flash update mechanism to update HOST/BMC firmware via LPC/PCI.

**Source URL**

* [https://github.com/openbmc/phosphor-ipmi-flash](https://github.com/openbmc/phosphor-ipmi-flash)
* [https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-olympus-nuvoton/ipmi-bios-update](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-olympus-nuvoton/ipmi-bios-update)
* [https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-olympus-nuvoton/ipmi-bmc-update](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-olympus-nuvoton/ipmi-bmc-update)

### HOST Tool
<img align="right" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/322d0d3/openbmc/in-band-fu.png">

The host-tool depends on `ipmi-blob-tool` and `pciutils`.

#### Building pciutils
Check out the [pciutils source](https://github.com/pciutils/pciutils).

Then run these commands in the source directory.
```
make SHARED=yes
make SHARED=yes install
make install-lib
```

#### Building ipmi-blob-tool
Check out the [ipmi-blob-tool source](https://github.com/openbmc/ipmi-blob-tool).
Then run these commands in the source directory.

```
./bootstrap.sh
./configure
make
make install
```

#### Building burn_my_bmc (the host-tool)
Check out the [phosphor-ipmi-flash source](https://github.com/openbmc/phosphor-ipmi-flash).
Then run these commands in the source directory.
If you choose "enable-nuvoton-p2a-vga", then the tool will support LPC and PCI-VGA.
If you choose "enable-nuvoton-p2a-mbox", then the tool will support LPC and PCI-MailBox

```
./bootstrap.sh
./configure --disable-build-bmc-blob-handler --enable-nuvoton-p2a-vga
make
make install
```

**How to use**
1. If you want to do firmware update over LPC, then you need to check the memory address which BIOS allocates for LPC.
You could use "ioport" tool and following commands to get the address.
    ```
    sudo outb 0x4e 0x07
    sudo outb 0x4f 0x0f

    # Host address a7-a0
    sudo outb 0x4e 0xF4
    sudo inb 0x4f

    # Host address a15-a8
    sudo outb 0x4e 0xF5
    sudo inb 0x4f

    # Host address a23-a16
    sudo outb 0x4e 0xF6
    sudo inb 0x4f

    # Host address a32-a24
    sudo outb 0x4e 0xF7
    sudo inb 0x4f

    # shm active?
    sudo outb 0x4e 0x30
    sudo inb 0x4f

    ```

2. Here is an example for upadting over LPC, and --length is fixed.
    ```
    sudo ./burn_my_bmc --command update --interface ipmilpc --image image-bmc --sig image-bmc.sig --type image --address 0x817e0000 --length 0x1000
    ```

3. Here is an example for upadting over PCI(both VGA and MailBox)and --type is fixed
    ```
    sudo ./burn_my_bmc --command update --interface ipmipci --image image-bmc --sig image-bmc.sig --type image
    ```

### IPMI Library
This is an OpenBMC IPMI Library (Handler) for In-Band Firmware Update.

**How to use**
1. You need to enable the way you want to transfer data from host to BMC
    ```
    nuvoton-lpc
    enable-nuvoton-p2a-mbox
    enable-nuvoton-p2a-vga
    ```

2. select the corresponding address for IPMI_FLASH_BMC_ADDRESS_nuvoton
    ```
    0x7F400000
    0xF0848000
    0xc0008000
    ```

**Maintainer**
* Medad CChien

## MCTP

### MCTP over PCIe VDM
Nuvoton BMC contains a VDM module that can send and receive VDM messages. \
It sends and receives MCTP messages over PCIe.

**Source URL**
* [mctpd, forked from intel/opnbmc](https://github.com/Nuvoton-Israel/pmci/tree/master/mctpd)
* [libmctp, forked from intel/opnbmc](https://github.com/Nuvoton-Israel/libmctp)
* [recipes, bb](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-phosphor/recipes-x86/pmci)
* [recipes, bbappend](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-x86/pmci)
* [driver-misc-nuvoton-vdm-support-openbmc-libmctp.patch](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-nuvoton/recipes-kernel/linux/linux-nuvoton/0015-driver-misc-nuvoton-vdm-support-openbmc-libmctp.patch)

**How to use**

* **Configuration** 
  * Enable Intel ME MCTP infrastructure

    In the Intel platform, the ME is the bus owner of MCTP topology. \
    Therefore, please make sure that `MCTPInfrastructure` is enabled.\
    In BIOS setup, on the `Platform Configuration` page, the `ME Firmware Features` should contain `MCTPInfrastructure`. \
    Please contact your BIOS team if your `MCTPInfrastructure` is not enabled. \
    <img align="bottomleft" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/MCTPinfra.png">

  * [Config File for MCTPD](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-x86/pmci/mctpd/mctp_config.json)
      ```
        "pcie": {
            "role": "endpoint",
            "default-eid":0,
            "PhysicalMediumID":"Pcie2",
            "bdf":0,
            "ReqToRespTimeMs":120,
            "ReqRetryCount":2,
            "GetRoutingInterval":5
        }
      ```
  * Prepare antoher endpoint device

    We use Intel i210 NIC card to verify the MCTP implementation. \
    We recommend preparing an endpoint device, that you can practice endpoint to endpoint communication.

* **Endpoint Device Enumeration**

  * Start the mctp pcie service

    ```
    systemctl start xyz.openbmc_project.mctpd@pcie.service
    ```

  * Enumerate Endpoint via BMCWEB Server
    
    The eid 97 is BMC and eid 96 is Intel i210 NIC card.
    ```
      https://<BCM_IP>/xyz/openbmc_project/mctp/enumerate

      "/xyz/openbmc_project/mctp": {
              "BDF": 33024,
              "BindingID": "xyz.openbmc_project.MCTP.Base.BindingTypes.MctpOverPcieVdm",
              "BindingMediumID": "xyz.openbmc_project.MCTP.Base.MctpPhysicalMediumIdentifiers.Pcie2",
              "BindingMode": "xyz.openbmc_project.MCTP.Base.BindingModeTypes.Endpoint",
              "DiscoveredFlag": "xyz.openbmc_project.MCTP.Binding.PCIe.DiscoveryFlags.Discovered",
              "Eid": 97,
              "StaticEid": false,
              "Uuid": []
        },
      "/xyz/openbmc_project/mctp/device/96": {
              "Ethernet": true,
              "MctpControl": true,
              "Mode": "xyz.openbmc_project.MCTP.Base.BindingModeTypes.Endpoint",
              "NCSI": true,
              "NVMeMgmtMsg": false,
              "NetworkId": 0,
              "PLDM": false,
              "SPDM": false,
              "UUID": "bab74068-1ad4-e011-ed86-00a0c9000000",
              "VDIANA": false,
              "VDPCI": true
        }

    ```  
  * Show interfaces, methods, properties and signals of the endpoint device via busctl

    ```
    busctl introspect xyz.openbmc_project.MCTP-pcie /xyz/openbmc_project/mctp
    busctl introspect xyz.openbmc_project.MCTP-pcie /xyz/openbmc_project/mctp/device/96
    ```

* **Endpoint Device Communication**
  * Send MCTP message via busctl

    ```
    busctl call xyz.openbmc_project.MCTP-pcie /xyz/openbmc_project/mctp xyz.openbmc_project.MCTP.Base SendMctpMessagePayload yybay 96 1 true 24 0x02 0x00 0x01 0x00 0x01 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00
    ```

  * A daemon to procress MCTP messages

    All MCTP control messages are already being processed by MCTPD. \
    Therefore, for the other message types, for example, NVME, NCSI, PLDM, etc. \
    We should have a specific daemon to interact with each different device type.

    <img align="bottomleft" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/openbmc-mctpd-vdm-arch.PNG">

  * Test utility

    When you encounter the configuration issue, there is a test utility that can help you to see the mctp infrastructure if works well. \
    [mctp-nupcie-discovery](https://github.com/Nuvoton-Israel/libmctp/blob/master/utils/mctp-nupcie-discovery.c)

### MCTP over SMBUS  
Most user-space implementation is the same as MCTP over PCIe VDM, the only difference is the message over SMBUS. \
Therefore, please go to [PLDM Sensors](#pldm-sensors), it is a MCTP over SMBUS implementation of OpenBMC.


## PLDM

### PLDM Sensors

This is a PLDM over MCTP over SMBUS feature.

[pldmsensors](https://github.com/Nuvoton-Israel/pldmsensors) daemon will periodically check the sensor reading and update on corresponding dbus property. This daemon will send PLDM request via dbus method registed by [mctpd](https://github.com/Nuvoton-Israel/pmci/tree/master/mctpd) daemon, and mctpd will pack the PLDM request into a MCTP message and pass the request to PLDM device via I2C or PCI according to SMBUS_BINDING in [mctpd.bbappend](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-x86/pmci/mctpd.bbappend). There is also a config file [mctp_config.json](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-x86/pmci/mctpd/mctp_config.json) for mctpd you should modify according to your configuration.

**Source URL**
* [pldmsensors](https://github.com/Nuvoton-Israel/pldmsensors)
* [mctpd, forked from intel/opnbmc](https://github.com/Nuvoton-Israel/pmci/tree/master/mctpd)
* [libpldm_intel, forked from intel/opnbmc](https://github.com/Nuvoton-Israel/pmci/tree/master/libpldm_intel)
* [libmctp, forked from intel/opnbmc](https://github.com/Nuvoton-Israel/libmctp)
* [recipes, bb](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-phosphor/recipes-x86/pmci)
* [recipes, bbappend](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-x86/pmci)
* [i2c-nuvoton-npcm750-runbmc-integrate-the-slave-mqueu.patch](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-nuvoton/recipes-kernel/linux/linux-nuvoton/0003-i2c-nuvoton-npcm750-runbmc-integrate-the-slave-mqueu.patch)

**How to use**

* **Configure sensors**

  * [Config File for MCTPD](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-x86/pmci/mctpd/mctp_config.json)
      ```
        "smbus": {
            "role": "endpoint",
            "default-eid":8,
            "eid-pool":[9,10,11,12,13,14,15,16,17,18,19,20],
            "bus":"/dev/i2c-2",
            "PhysicalMediumID":"Smbus3OrI2c400khzCompatible",
            "ARPMasterSupport": false,
            "BMCSlaveAddress":18,
            "ReqToRespTimeMs":100,
            "ReqRetryCount":2
        },
      ```

  * Add Sensor Configuration File
  
    All the sensors **temperature**, **adc**, **fan**, **voltage** and **power** use the same [pldmsensors.json](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-quanta/meta-olympus-nuvoton/recipes-phosphor/sensors/pldmsensors/pldmsensors.json) that defines the sensor name and its warning or critical thresholds ,etc. This file is located under **meta-quanta/meta-olympus-nuvoton/recipes-phosphor/sensors/pldmsensors/** .  

    Below is sensor config for a temperature sensor on PLDM device. The sensor ID is **4** and its name is **PLDM_Temp0**. It has warning and critical thresholds and fatal for **upper** and **lower** bound and sensorUnit, sensorDataSize, sensorScale, rearmEventState, powerState, hysteresis, factor, etc.
      ```
      "sensorID":4,
      "sensorTypeName":"temperature",
      "objectType":"xyz.openbmc_project.Configuration.PLDMtemperature",
      "sensorName":"PLDM_Temp0",
      "sensorUnit":"DegreesC",
      "sensorDataSize":"SINT8",
      "sensorScale":0,
      "factor":"1",
      "rearmEventState":"false",
      "powerState":"Standby",
      "upperThresholdWarning":"60",
      "upperThresholdCritical":"70",
      "upperThresholdFatal":"80",
      "lowerThresholdWarning":"15",
      "lowerThresholdCritical":"10",
      "lowerThresholdFatal":"5",
      "hysteresis":3
      ```
    Below is sensor config for a voltage on PLDM device.
      ```
      "sensorID":31,
      "sensorTypeName":"voltage",
      "objectType":"xyz.openbmc_project.Configuration.PLDMvoltage",
      "sensorName":"PLDM_3_3STDBY",
      "sensorUnit":"Volts",
      "sensorDataSize":"UINT16",
      "sensorScale":0,
      "factor":"0.016",
      "rearmEventState":"false",
      "powerState":"Standby",
      "upperThresholdWarning":"210",
      "upperThresholdCritical":"215",
      "upperThresholdFatal":"220",
      "lowerThresholdWarning":"190",
      "lowerThresholdCritical":"180",
      "lowerThresholdFatal":"170",
      "hysteresis":5
      ```

* **Monitor sensor and events**

  * Using WebUI  

    In `Sensors` page of **WebUI**, the sensors reading will show as below.

    <img align="bottomleft" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/pldmsensor_reading.png">  

  * Using Dbus

    Use **busctl** to send command for getting PLDM_Temp0 info.  
    ```
    $ busctl  introspect  xyz.openbmc_project.PLDMSensor /xyz/openbmc_project/sensors/temperature/PLDM_Temp0
    ```
    <img align="bottomleft" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/PLDM_Temp0.png">
    
    Use **busctl** to send command for getting PLDM_3_3STDBY info. 
    ```
    $ busctl  introspect  xyz.openbmc_project.PLDMSensor /xyz/openbmc_project/sensors/voltage/PLDM_3_3STDBY
    ```
     <img align="bottomleft" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/PLDM_3_3STDBY.png">

**Maintainer**

* Medad CChien


### BMC Reboot Cause

#### Last BMC Reboot Cause
<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/8199e33/openbmc/bmc_reboot_cause.png">

The BMC Reboot Cause feature facilitates customer to know the last reboot cause of BMC through OpenBMC WebUI.

**Source URL**

For this feature, we need to modify three recipes from OpenBMC github as below:  
* [OpenBMC/phosphor-dbus-interfaces](https://github.com/openbmc/phosphor-dbus-interfaces)  
* [OpenBMC/phosphor-state-manager](https://github.com/openbmc/phosphor-state-manager)  
* [OpenBMC/phosphor-webui](https://github.com/openbmc/phosphor-webui)  

**How to use**

* Currently, we have support two kinds of bmc reboot cause

  * **Power-On-Reset (Trigger by unplug/plug-in BMC power cable)**


  You can use below busctl command to verify last bmc reboot cause when unplug/plug-in BMC power cable.  
  The result will be **"xyz.openbmc_project.State.BMC.LastRebootCause.POR"** (POR i.e. Power-On-Reset).
  > _busctl get-property xyz.openbmc_project.State.BMC /xyz/openbmc_project/state/bmc0 xyz.openbmc_project.State.BMC LastRebootCause_

  Meanwhile, you can check `Reboot BMC` page from `Control` item of **WebUI** then the last bmc reboot cause will prefix to last bmc reboot date/time in `Current BMC boot status` title as below:

  <img align="bottomleft" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/8199e33/openbmc/bmc_reboot_cause.png">



  * **Watchdog (Trigger by reboot command)**

  You can use below busctl command to verify last bmc reboot cause when execute reboot command.  
  The result will be **"xyz.openbmc_project.State.BMC.LastRebootCause.Watchdog"**.
  > _busctl get-property xyz.openbmc_project.State.BMC /xyz/openbmc_project/state/bmc0 xyz.openbmc_project.State.BMC LastRebootCause_

  Meanwhile, you can check `Reboot BMC` page from `Control` item of WebUI then the last bmc reboot cause will prefix to last bmc reboot date/time in `Current BMC boot status` title as below:

  <img align="bottomleft" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/0860e7d/openbmc/bmc_reboot_cause_wd.png">

**Maintainer**
* Tim Lee

## OpenBMC Test Automation

**Source URL**

* [https://github.com/Nuvoton-Israel/openbmc-test-automation](https://github.com/Nuvoton-Israel/openbmc-test-automation)

In order to make sure all features and kernel drivers are running without problems, we introduce the [openbmc-test-automation](https://github.com/Nuvoton-Israel/openbmc-test-automation) to be our automated testing tool.

So far, we have been testing ipmi and redfish related items(Total 390 test items), and more test items will be planned.

<img width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/robot-test.PNG">

Please download the test report in the link below
* [Report](https://drive.google.com/open?id=10bmoyEAJvu00t-dwicChR67bND2_9Mzi)


## Features In Progressing
* Improve IPMI
* Improve Redfish
* Intel Platform related features
* Nuvoton IPMI OEM command


# IPMI Commands
[IPMI.md](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/IPMI.md)

# DCMI Commands
[DCMI.md](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/DCMI.md)

# Redfish APIs
[REDFISH.md](https://github.com/Nuvoton-Israel/openbmc/tree/runbmc/meta-quanta/meta-olympus-nuvoton/REDFISH.md)

# Image Size
Type          | Size    | Note                                                                                                     |
:-------------|:------- |:-------------------------------------------------------------------------------------------------------- |
image-uboot   |  450 KB | u-boot 2019.01 + bootblock for NPCM750 only                                                                       |
image-kernel  |  4.0 MB   | linux 5.4 version  + initramfs                                                                                      |
image-rofs    |  18.0 MB  | bottom layer of the overlayfs, read only                                                                 |
image-rwfs    |  0 MB  | middle layer of the overlayfs, rw files in this partition will be created at runtime,<br /> with a maximum capacity of 3 MB|

# Modifications

* 2019.10.01 First release ReadME.md
* 2019.11.20 First release VM, In-Band Firmware Update
* 2019.12.05 Add BIOS update function via web UI part
* 2019.12.09 Update Serail Over Lan(SOL) and BMC Firmware update
* 2019.12.11 Update Time settings of System/Time
* 2019.12.13 Update Sensors, and LED
* 2019.12.13 Update Fan, BIOS POST code, and FRU
* 2019.12.17 Update SOL IPMI, Image size, and server power operations
* 2019.12.18 Update Message Bridging, and VM application
* 2019.12.19 Rename Message Bridging to Host Power Budget Control
* 2019.12.19 Add Fan PID control
* 2019.12.23 Fix some typo and text format
* 2020.03.31 Add Open Test Automation and update Features In Progressing and Image Size
* 2020.04.06 Add Certificate Management
* 2020.04.20 Update LDAP for User Management
* 2020.05.15 Add VLAN
* 2020.06.03 Update BIOS POST Code
* 2020.06.29 Add Crash Dump
* 2020.07.21 Add SMBIOS
* 2020.07.27 Update Time Synce and Table of Contents
* 2020.08.03 Add Power Supply Unit Inventory
* 2020.08.03 Add ipmi.md, dcmi.md and redfish.md
* 2020.08.07 Add Virtual Media Performance
* 2020.11.06 Add PLDM sensors
* 2020.11.13 Add MCU Firmware Update
* 2020.12.03 Add MCTP
* 2021.03.18 Update Server power operations
* 2021.03.18 Add BMC Reboot Cause
* 2021.09.06 Update Intel Crash Dump and At-Scale Debug
