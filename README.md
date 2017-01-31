# OpenBMC #

[![Build Status](https://openpower.xyz/buildStatus/icon?job=openbmc-build)](https://openpower.xyz/job/openbmc-build/)

The OpenBMC project can be described as a Linux distribution for embedded
devices that have a BMC; typically, but not limited to, things like servers,
top of rack switches or RAID appliances. The OpenBMC stack uses technologies
such as Yocto, Open-Embedded, Systemd and DBus to allow easy customization
for your server platform.


## Setting up your OpenBMC project ##

### 1) Prerequisite ###
- Ubuntu 14.04

```
sudo apt-get install -y git build-essential libsdl1.2-dev texinfo gawk chrpath diffstat
```

- Fedora 23

```
sudo dnf install -y git patch diffstat texinfo chrpath SDL-devel bitbake
sudo dnf groupinstall "C Development Tools and Libraries"
```
### 2) Download the source ###
```
git clone git@github.com:openbmc/openbmc.git
cd openbmc
```

### 3) Target your hardware ###
Any build requires an environment variable known as `TEMPLATECONF` to be set
to a hardware target.  OpenBMC has placed all known hardware targets in a
standard directory structure `meta-openbmc-machines/meta-openpower/[company]/[target]`.
You can see all of the known targets with `find meta-openbmc-machines -type d -name conf`.
Choose the hardware target and then move to the next step. Additional examples
can be found in the [OpenBMC Cheatsheet] (https://github.com/openbmc/docs/blob/master/cheatsheet.md)

Machine | TEMPLATECONF
--------|---------
Palmetto | ```meta-openbmc-machines/meta-openpower/meta-ibm/meta-palmetto/conf```
Barreleye | ```meta-openbmc-machines/meta-openpower/meta-rackspace/meta-barreleye/conf```
Zaius| ```meta-openbmc-machines/meta-openpower/meta-ingrasys/meta-zaius/conf```
Witherspoon| ```meta-openbmc-machines/meta-openpower/meta-ibm/meta-witherspoon/conf```


As an example target Palmetto
```
export TEMPLATECONF=meta-openbmc-machines/meta-openpower/meta-ibm/meta-palmetto/conf
```

### 3) Build ###

```
. openbmc-env
bitbake obmc-phosphor-image
```

Additional details can be found in the [docs](https://github.com/openbmc/docs)
repository.

## Build Validation and Testing ##
Commits submitted by members of the OpenBMC Github community are compiled and
tested via our [Jenkins](https://openpower.xyz/) server.  Commits are run
through two levels of testing.  At the repository level the makefile `make
check` directive is run.  At the system level, the commit is built into a
firmware image and run with a arm-softmmu QEMU model against a barrage of
[CI tests](https://openpower.xyz/job/openbmc-test-qemu-ci/).

Commits submitted by non-members do not automatically proceed through CI
testing. After visual inspection of the commit, a CI run can be manually
performed by the reviewer.

Automated testing against the QEMU model along with supported systems are
performed.  The OpenBMC project uses the [Robot Framework]
(http://robotframework.org/) for all automation.  Our complete test
repository can be found [here](https://github.com/openbmc/openbmc-test-automation).

## Submitting Patches ##
Support of additional hardware and software packages is always welcome.
Please follow the [contributing guidelines](https://github.com/openbmc/docs/blob/master/contributing.md)
when making a submission.  It is expected that contributions contain test
cases.

## Bug Reporting ##
[Issues](https://github.com/openbmc/openbmc/issues) are managed on
Github.  It is recommended you search through the issues before opening
a new one.

## Finding out more ##
Dive deeper in to OpenBMC by opening the [docs](https://github.com/openbmc/docs)
repository

## Contact ##
- Mail: openbmc@lists.ozlabs.org  [https://lists.ozlabs.org/listinfo/openbmc]
(https://lists.ozlabs.org/listinfo/openbmc)
- IRC: #openbmc on freenode.net
- Riot: [#openbmc:matrix.org](https://riot.im/app/#/room/#openbmc:matrix.org)
