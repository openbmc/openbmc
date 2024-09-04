# OpenBMC

[![Build Status](https://jenkins.openbmc.org/buildStatus/icon?job=latest-master)](https://jenkins.openbmc.org/job/latest-master/)

OpenBMC is a Linux distribution for management controllers used in devices such
as servers, top of rack switches or RAID appliances. It uses
[Yocto](https://www.yoctoproject.org/),
[OpenEmbedded](https://www.openembedded.org/wiki/Main_Page),
[systemd](https://www.freedesktop.org/wiki/Software/systemd/), and
[D-Bus](https://www.freedesktop.org/wiki/Software/dbus/) to allow easy
customization for your platform.

## Setting up your OpenBMC project

### 1) Prerequisite

See the
[Yocto documentation](https://docs.yoctoproject.org/ref-manual/system-requirements.html#required-packages-for-the-build-host)
for the latest requirements

#### Ubuntu

```sh
sudo apt install git python3-distutils gcc g++ make file wget \
    gawk diffstat bzip2 cpio chrpath zstd lz4 bzip2
```

#### Fedora

```sh
sudo dnf install git python3 gcc g++ gawk which bzip2 chrpath cpio \
    hostname file diffutils diffstat lz4 wget zstd rpcgen patch
```

### 2) Download the source

```sh
git clone https://github.com/openbmc/openbmc
cd openbmc
```

### 3) Target your hardware

Any build requires an environment set up according to your hardware target.
There is a special script in the root of this repository that can be used to
configure the environment as needed. The script is called `setup` and takes the
name of your hardware target as an argument.

The script needs to be sourced while in the top directory of the OpenBMC
repository clone, and, if run without arguments, will display the list of
supported hardware targets, see the following example:

```text
$ . setup <machine> [build_dir]
Target machine must be specified. Use one of:
...
```

A more complete list of supported machines can be found under
[meta-phosphor/docs](https://github.com/openbmc/openbmc/blob/master/meta-phosphor/docs/supported-machines.md).

Once you know the target (e.g. romulus), source the `setup` script as follows:

```sh
. setup romulus
```

### 4) Build

```sh
bitbake obmc-phosphor-image
```

Additional details can be found in the [docs](https://github.com/openbmc/docs)
repository.

## OpenBMC Development

The OpenBMC community maintains a set of tutorials new users can go through to
get up to speed on OpenBMC development out
[here](https://github.com/openbmc/docs/blob/master/development/README.md)

## Build Validation and Testing

Commits submitted by members of the OpenBMC GitHub community are compiled and
tested via our [Jenkins](https://jenkins.openbmc.org/) server. Commits are run
through two levels of testing. At the repository level the makefile `make check`
directive is run. At the system level, the commit is built into a firmware image
and run with an arm-softmmu QEMU model against a barrage of
[CI tests](https://jenkins.openbmc.org/job/CI-MISC/job/run-ci-in-qemu/).

Commits submitted by non-members do not automatically proceed through CI
testing. After visual inspection of the commit, a CI run can be manually
performed by the reviewer.

Automated testing against the QEMU model along with supported systems are
performed. The OpenBMC project uses the
[Robot Framework](http://robotframework.org/) for all automation. Our complete
test repository can be found
[here](https://github.com/openbmc/openbmc-test-automation).

## Submitting Patches

Support of additional hardware and software packages is always welcome. Please
follow the
[contributing guidelines](https://github.com/openbmc/docs/blob/master/CONTRIBUTING.md)
when making a submission. It is expected that contributions contain test cases.

## Bug Reporting

[Issues](https://github.com/openbmc/openbmc/issues) are managed on GitHub. It is
recommended you search through the issues before opening a new one.

## Questions

First, please do a search on the internet. There's a good chance your question
has already been asked.

For general questions, please use the openbmc tag on
[Stack Overflow](https://stackoverflow.com/questions/tagged/openbmc). Please
review the
[discussion](https://meta.stackexchange.com/questions/272956/a-new-code-license-the-mit-this-time-with-attribution-required?cb=1)
on Stack Overflow licensing before posting any code.

For technical discussions, please see [contact info](#contact) below for Discord
and mailing list information. Please don't file an issue to ask a question.
You'll get faster results by using the mailing list or Discord.

### Will OpenBMC run on my Acme Server Corp. XYZ5000 motherboard?

This is a common question, particularly regarding boards from popular COTS
(commercial off-the-shelf) vendors such as Supermicro and ASRock. You can see
the list of supported boards by running `. setup` (with no further arguments) in
the root of the OpenBMC source tree. Most of the platforms supported by OpenBMC
are specialized servers operated by companies running large datacenters, but
some more generic COTS servers are supported to varying degrees.

If your motherboard is not listed in the output of `. setup` it is not currently
supported. Porting OpenBMC to a new platform is a non-trivial undertaking,
ideally done with the assistance of schematics and other documentation from the
manufacturer (it is not completely infeasible to take on a porting effort
without documentation via reverse engineering, but it is considerably more
difficult, and probably involves a greater risk of hardware damage).

**However**, even if your motherboard is among those listed in the output of
`. setup`, there are two significant caveats to bear in mind. First, not all
ports are equally mature -- some platforms are better supported than others, and
functionality on some "supported" boards may be fairly limited. Second, support
for a motherboard is not the same as support for a complete system -- in
particular, fan control is critically dependent on not just the motherboard but
also the fans connected to it and the chassis that the board and fans are housed
in, both of which can vary dramatically between systems using the same board
model. So while you may be able to compile and install an OpenBMC build on your
system and get some basic functionality, rough edges (such as your cooling fans
running continuously at full throttle) are likely.

See also
["Supported Machines"](https://github.com/openbmc/openbmc/blob/master/meta-phosphor/docs/supported-machines.md).

## Features of OpenBMC

### Feature List

- Host management: Power, Cooling, LEDs, Inventory, Events, Watchdog
- Full IPMI 2.0 Compliance with DCMI
- Code Update Support for multiple BMC/BIOS images
- Web-based user interface
- REST interfaces
- D-Bus based interfaces
- SSH based SOL
- Remote KVM
- Hardware Simulation
- Automated Testing
- User management
- Virtual media

### Features In Progress

- OpenCompute Redfish Compliance
- Verified Boot

### Features Requested but need help

- OpenBMC performance monitoring

## Finding out more

Dive deeper into OpenBMC by opening the [docs](https://github.com/openbmc/docs)
repository.

## Technical Steering Committee

The Technical Steering Committee (TSC) guides the project. Members are:

- Benjamin Fair, Google
- Patrick Williams, Meta
- Roxanne Clarke, IBM
- Sagar Dharia, Microsoft
- Samer El-Haj-Mahmoud, Arm
- Terry Duncan, Intel

## Contact

- Mail: openbmc@lists.ozlabs.org
  [https://lists.ozlabs.org/listinfo/openbmc](https://lists.ozlabs.org/listinfo/openbmc)
- Discord: [https://discord.gg/69Km47zH98](https://discord.gg/69Km47zH98)
