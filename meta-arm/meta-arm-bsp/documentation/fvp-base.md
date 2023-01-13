# Armv8-A Base Platform FVP Support in meta-arm-bsp

## Howto Build and Run

### Configuration:
In the local.conf file, `MACHINE` should be set:
```
MACHINE = "fvp-base"
```

### Build:
```
$ bitbake core-image-base
```

### Run:
The `fvp-base` machine has support for the `runfvp` script, so running is simple:

```
$ runfvp tmp/deploy/images/fvp-base/core-image-base-fvp-base.fvpconf
```
## Devices supported in the kernel
- serial
- virtio disk
- network
- watchdog
- rtc

## Devices not supported or not functional
None
