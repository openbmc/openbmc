# Armv8-A Base Platform FVP (32-bit) Support in meta-arm-bsp

## Howto Build and Run

### Configuration:
In the local.conf file, `MACHINE` should be set:
```
MACHINE = "fvp-base-arm32"
```

### Build:
```
$ bitbake core-image-base
```

### Run:
The `fvp-base` machine has support for the `runfvp` script, so running is simple:

```
$ runfvp tmp/deploy/images/fvp-base-arm32/core-image-base-fvp-base-arm32.fvpconf
```
## Devices supported in the kernel
- serial
- virtio disk
- network
- watchdog
- rtc

## Devices not supported or not functional
None
