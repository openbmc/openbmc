# meta-atp layer

The meta-atp layer supports building environments with traffic generation capabilities based on [AMBA Adaptive Traffic Profiles (ATP)](https://developer.arm.com/documentation/ihi0082/latest).

## Recipes

The meta-atp layer supports building the following software components:

- Arm's implementation of the AMBA ATP specification, namely the [AMBA ATP Engine](https://github.com/ARM-software/ATP-Engine).
- Linux kernel modules and user API (UAPI) for programming ATP devices.
- Integration test suite for verification of kernel modules and UAPI.

It is also possible to build the AMBA ATP Engine as part of the final [gem5](https://www.gem5.org/) executable. For this, meta-atp extends the `gem5-aarch64-native` recipe to add the AMBA ATP engine code as extra sources.

## Machines

The `gem5-atp-arm64` machine extends the `gem5-arm64` machine to instantiate a simulated platform with support for programmable AMBA ATP traffic generation. The platform includes the following models:

- `ProfileGen` model. This is the adapter layer between gem5 and the AMBA ATP Engine. It is the source of traffic into the gem5 host platform.
- `ATPDevice` model. Software can program it using the Linux kernel modules and UAPI to control traffic generation.

## Usage

Users should add the meta-atp layer and layer dependencies to `conf/bblayers.conf`. See `conf/layer.conf` for dependencies.

### Standalone Engine executable

Users can build the AMBA ATP Engine as a standalone native executable as follows:

```bash
bitbake atp-native
```

Users can run the executable through standard build scripts:

```bash
oe-run-native atp-native atpeng [--help | args...]
```

## Integration of the Engine in gem5

Users should select the `gem5-atp-arm64` platform in their `conf/local.conf` file.

Users can build the target image of preference, for example:

```bash
bitbake core-image-minimal
```

The resulting gem5 native executable contains the AMBA ATP Engine. The resulting target image contains the kernel modules, UAPI and test suite.

Users should run the environment as follows:

```bash
./tmp/deploy/tools/start-gem5-atp.sh
```

This script launches a fast simulation to fast-forward Linux boot. Once Linux boot is completed, the fast simulation switches into a detailed simulation for the final usable environment. Users can connect and interact with the environment as follows:

```bash
oe-run-native gem5-m5term-native m5term <PORT>
```

The connection PORT is announced by the deploy script as:

```bash
system.terminal: Listening for connections on port <PORT>
```

This is usually port 3456.

Users can verify access to the ATP device by running the integration test suite from within the simulated environment as follows:

```bash
test_atp.out
```
