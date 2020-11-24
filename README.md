# OpenBMC

[![Build Status](https://openpower.xyz/buildStatus/icon?job=openbmc-build)](https://openpower.xyz/job/openbmc-build/)

The OpenBMC project can be described as a Linux distribution for embedded
devices that have a BMC; typically, but not limited to, things like servers,
top of rack switches or RAID appliances. The OpenBMC stack uses technologies
such as [Yocto](https://www.yoctoproject.org/),
[OpenEmbedded](https://www.openembedded.org/wiki/Main_Page),
[systemd](https://www.freedesktop.org/wiki/Software/systemd/), and
[D-Bus](https://www.freedesktop.org/wiki/Software/dbus/) to allow easy
customization for your server platform.


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
git clone git@github.com:openbmc/openbmc.git
cd openbmc
```

### 3) Target your hardware
Any build requires an environment set up according to your hardware target.
There is a special script in the root of this repository that can be used
to configure the environment as needed. The script is called `setup` and
takes the name of your hardware target as an argument.

The script needs to be sourced while in the top directory of the OpenBMC
repository clone, and, if run without arguments, will display the list
of supported hardware targets, see the following example:

```
$ . setup <machine> [build_dir]
Target machine must be specified. Use one of:

centriq2400-rep         nicole                     stardragon4800-rep2
f0b                     olympus                    swift
fp5280g2                olympus-nuvoton            tiogapass
gsj                     on5263m5                   vesnin
hr630                   palmetto                   witherspoon
hr855xg2                qemuarm                    witherspoon-128
lanyang                 quanta-q71l                witherspoon-tacoma
mihawk                  rainier                    yosemitev2
msn                     romulus                    zaius
neptune                 s2600wf
```

Once you know the target (e.g. romulus), source the `setup` script as follows:

```
. setup romulus build
```

For evb-ast2500, please use the below command to specify the machine config,
because the machine in `meta-aspeed` layer is in a BSP layer and does not
build the openbmc image.

```
TEMPLATECONF=meta-evb/meta-evb-aspeed/meta-evb-ast2500/conf . openbmc-env
```

### 4) Build

```
bitbake obmc-phosphor-image
```

Additional details can be found in the [docs](https://github.com/openbmc/docs)
repository.

## OpenBMC Development

The OpenBMC community maintains a set of tutorials new users can go through
to get up to speed on OpenBMC development out
[here](https://github.com/openbmc/docs/blob/master/development/README.md)

## Build Validation and Testing
Commits submitted by members of the OpenBMC GitHub community are compiled and
tested via our [Jenkins](https://jenkins.openbmc.org/) server. Commits are run
through two levels of testing.  At the repository level the makefile `make
check` directive is run.  At the system level, the commit is built into a
firmware image and run with an arm-softmmu QEMU model against a barrage of
[CI tests](https://openpower.xyz/job/openbmc-test-qemu-ci/).

Commits submitted by non-members do not automatically proceed through CI
testing. After visual inspection of the commit, a CI run can be manually
performed by the reviewer.

Automated testing against the QEMU model along with supported systems are
performed.  The OpenBMC project uses the
[Robot Framework](http://robotframework.org/) for all automation.  Our
complete test repository can be found
[here](https://github.com/openbmc/openbmc-test-automation).

## Submitting Patches
Support of additional hardware and software packages is always welcome.
Please follow the [contributing guidelines](https://github.com/openbmc/docs/blob/master/CONTRIBUTING.md)
when making a submission.  It is expected that contributions contain test
cases.

## Bug Reporting
[Issues](https://github.com/openbmc/openbmc/issues) are managed on
GitHub.  It is recommended you search through the issues before opening
a new one.

## Questions

First, please do a search on the internet. There's a good chance your question
has already been asked.

For general questions, please use the openbmc tag on
[Stack Overflow](https://stackoverflow.com/questions/tagged/openbmc).
Please review the [discussion](https://meta.stackexchange.com/questions/272956/a-new-code-license-the-mit-this-time-with-attribution-required?cb=1)
on Stack Overflow licensing before posting any code.

For technical discussions, please see [contact info](#contact) below for
Discord and mailing list information. Please don't file an issue to ask a
question. You'll get faster results by using the mailing list or Discord.

## Features of OpenBMC

**Feature List**
* Host management: Power, Cooling, LEDs, Inventory, Events, Watchdog
* Full IPMI 2.0 Compliance with DCMI
* Code Update Support for multiple BMC/BIOS images
* Web-based user interface
* REST interfaces
* D-Bus based interfaces
* SSH based SOL
* Remote KVM
* Hardware Simulation
* Automated Testing
* User management
* Virtual media

**Features In Progress**
* OpenCompute Redfish Compliance
* Verified Boot

**Features Requested but need help**
* OpenBMC performance monitoring


## Finding out more

Dive deeper into OpenBMC by opening the
[docs](https://github.com/openbmc/docs) repository.

## Technical Steering Committee

The Technical Steering Committee (TSC) guides the project. Members are:

 * Brad Bishop (chair), IBM
 * Nancy Yuen, Google
 * Sai Dasari, Facebook
 * James Mihm, Intel
 * Sagar Dharia, Microsoft
 * Supreeth Venkatesh, Arm

## Contact
- Mail: openbmc@lists.ozlabs.org [https://lists.ozlabs.org/listinfo/openbmc](https://lists.ozlabs.org/listinfo/openbmc)
- Discord: https://discord.gg/69Km47zH98

