## ATP Engine integration in gem5, and the gem5-atp-arm64 machine

Users should select the `gem5-atp-arm64` machine in their `conf/local.conf` file.

Users can build the target image of preference, for example:

```bash
bitbake core-image-minimal
```

The resulting gem5 native executable contains the AMBA ATP Engine. The resulting target image contains the kernel modules, UAPI and test suite.

Users should run the environment as follows:

```bash
oe-run-native atp-gem5-native start-gem5-atp.sh
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
