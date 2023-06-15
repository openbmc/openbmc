# meta-atp summary

The meta-atp layer supports building the following software components:

- Arm's implementation of the AMBA ATP specification, namely the [AMBA ATP Engine](https://github.com/ARM-software/ATP-Engine).
- Linux kernel modules and user API (UAPI) for programming ATP devices.
- Integration test suite for verification of kernel modules and UAPI.

It is also possible to build the AMBA ATP Engine as part of the final [gem5](https://www.gem5.org/) executable. For this, meta-atp extends the `gem5-aarch64-native` recipe to add the AMBA ATP engine code as extra sources.

Users should add the meta-atp layer and layer dependencies to `conf/bblayers.conf`. See `conf/layer.conf` for dependencies.
