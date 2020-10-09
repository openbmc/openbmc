# OpenBMC

[![Build Status](https://openpower.xyz/buildStatus/icon?job=openbmc-build)](https://openpower.xyz/job/openbmc-build/)

This repository provides early access to Ampere Computing's BMC implementation
for Ampere's server platform reference designs. It is the intention to submit
all features in this repository to the OpenBMC main project (github/openbmc)
in due course.

For open questions and support, please contact support@amperecomputing.com.

## Setting up your OpenBMC project

### 1) Prerequisite
- Ubuntu 14.04
```
sudo apt-get install -y git build-essential libsdl1.2-dev texinfo gawk chrpath diffstat
```

- Fedora 28
```
sudo dnf install -y git patch diffstat texinfo chrpath SDL-devel bitbake \
    rpcgen perl-Thread-Queue perl-bignum perl-Crypt-OpenSSL-Bignum
sudo dnf groupinstall "C Development Tools and Libraries"
```

### 2) Download the source
```
git clone git@github.com:ampere-openbmc/openbmc.git
cd openbmc
```

### 3) Target your hardware
Source the setup script and use one of the following for Ampere's platform:
- mtjade

For example:
```
. setup mtjade
```

### 4) Build
```
bitbake obmc-phosphor-image
```

On successful build, the BMC image will be located in
```
tmp/work/mtjade-openbmc-linux-gnueabi/obmc-phosphor-image/1.0-r0/deploy-obmc-phosphor-image-image-complete/
```

as
```
obmc-phosphor-image-mtjade.static.mtd
obmc-phosphor-image-mtjade.static.mtd.tar
```

## Installing BMC firmware

If there is no firmware pre-installed on the system, you will need to program it to the BMC's SPI NOR using an external SPI programmer like Dediprog
using obmc-phosphor-image-mtjade.static.mtd.

If a previous version of Ampere's OpenBMC has been installed, perform the following steps to update BMC firmware.

- Open new Redfish token:
```
$ export token=`curl -k -H "Content-Type: application/json" -X POST https://${BMC_IP}/login -d '{"username" :  "root", "password" :  "0penBmc"}' | grep token | awk '{print $2;}' | tr -d '"'`
```

- Use Redfish UpdateService to flash the BMC firmware
```
$ curl -k -H "X-Auth-Token: $token" \
       -H "Content-Type: application/octet-stream" \
       -X POST -T ${BMC_FW}.mtd.tar https://${BMC_IP}/redfish/v1/UpdateService
```

- Reboot BMC to start the firmware update
```
$ curl -c cjar -b cjar -k -H "Content-Type: application/json" -X PUT \
       -d '{"data": "xyz.openbmc_project.State.BMC.Transition.Reboot"}' \
        https://root:0penBmc@${BMC_IP}/xyz/openbmc_project/state/bmc0/attr/RequestedBMCTransition`
```

## Installing SCP firmware

To install SCP firmware, need to copy it (in *.slim format) into the BMC via scp from BMC console:

```
# scp 10.38.12.53:/tftpboot/altra/altra_scp_1.01.20201019.slim /tmp
```

Then, use the ampere_firmware_upgrade.sh utility to flash the firmware into the Boot EEPROM:

```
# ampere_firmware_upgrade.sh smpmpro /tmp/altra_scp_1.01.20201019.slim 1
```

## Installing UEFI firmware

UEFI firmware can be flashed via Redfish UpdateService interface. It uses the tar format which include the firmware image (in *.img format) and a MANIFEST file. The following steps can be used to create the tar file and install UEFI firmware via Redfish

- Create MANIFEST file with the following content

```
purpose=xyz.openbmc_project.Software.Version.VersionPurpose.Host
version= jade_tianocore_atf_1.99.20201021.img
KeyType=OpenBMC
HashType=RSA-SHA256
MachineName=mtjade
```

- Then create tar file including the firmware image and MANIFEST file:

```
$ tar -cvf jade_tianocore_atf_1.99.20201021.tar jade_tianocore_atf_1.99.20201021.img MANIFEST
```

- Open new Redfish token:

```
$ export token=`curl -k -H "Content-Type: application/json" -X POST https://${BMC_IP}/login -d '{"username" :  "root", "password" :  "0penBmc"}' | grep token | awk '{print $2;}' | tr -d '"'`
```

- Use Redfish UpdateService to flash the UEFI firmware
```
$ curl -k -H "X-Auth-Token: $token" \
       -H "Content-Type: application/octet-stream" \
       -X POST -T jade_tianocore_atf_1.99.20201021.tar \
        https://${BMC_IP}/redfish/v1/UpdateService
```
