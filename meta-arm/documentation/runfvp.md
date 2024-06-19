# Running Images with a FVP

The `runfvp` tool in meta-arm makes it easy to run Yocto Project disk images inside a [Fixed Virtual Platform (FVP)][FVP].  Some FVPs, such as the [Arm Architecture Models][AEM], are available free to download, but others need registration or are only available commercially.  The `fvp-base` machine in meta-arm-bsp uses one of these AEM models.

## Running images with `runfvp`

To build images with the FVP integration, the `fvpboot` image class needs to be inherited.  If the machine does not do this explicitly it can be done in `local.conf`:

```
IMAGE_CLASSES += "fvpboot"
```

The class will download the correct FVP and write a `.fvpconf` configuration file when an image is built.

To run an image in a FVP, pass either a machine name or a `.fvpconf` path to `runfvp`.

```
$ ./meta-arm/scripts/runfvp tmp/deploy/images/fvp-base/core-image-minimal-fvp-base.fvpconf
```

When a machine name is passed, `runfvp` will start the latest image that has been built for that machine. This requires that the BitBake environment has been initialized (using `oe-init-build-env` or similar) as it will start BitBake to determine where the images are.

```
$ ./meta-arm/scripts/runfvp fvp-base
```

Note that currently meta-arm's `scripts` directory isn't in `PATH`, so a full path needs to be used.

`runfvp` will automatically start terminals connected to each of the serial ports that the machine specifies.  This can be controlled by using the `--terminals` option, for example `--terminals=none` will mean no terminals are started, and `--terminals=tmux` will start the terminals in [`tmux`][tmux] sessions.  Alternatively, passing `--console` will connect the serial port directly to the current session, without needing to open further windows.

The default terminal can also be configured by writing a [INI-style][INI] configuration file to `~/.config/runfvp.conf`:

```
[RunFVP]
Terminal=tmux
```

Arbitrary options can be passed directly to the FVP by specifying them after a double dash, for example this will list all of the FVP parameters:

```
$ runfvp fvp-base -- --list-params
```

## Configuring machines with `fvpboot`

To configure a machine so that it can be ran inside `runfvp`, a number of variables need to be set in the machine configuration file (such as `meta-arm-bsp/conf/machine/fvp-base.conf`).

Note that at present these variables are not stable and their behaviour may be changed in the future.

### `FVP_EXE`

The name of the FVP binary itself, for example `fvp-base` uses `FVP_Base_RevC-2xAEMvA`.

### `FVP_PROVIDER`

The name of the recipe that provides the FVP executable set in `FVP_EXE`, for example `fvp-base` uses `fvp-base-a-aem-native`.  This *must* be a `-native` recipe as the binary will be executed on the build host.

There are recipes for common FVPs in meta-arm already, and writing new recipes is trivial.  For FVPs which are free to download `fvp-base-a-aem.bb` is a good example. Some FVPs must be downloaded separately as they need an account on Arm's website.

If `FVP_PROVIDER` is not set then it is assumed that `FVP_EXE` is installed on the host already.

### `FVP_CONFIG`

Parameters passed to the FVP with the `--parameter`/`-C` option.  These are expressed as variable flags so individual parameters can be altered easily. For example:

```
FVP_CONFIG[bp.flashloader0.fname] = "fip-fvp.bin"
```

### `FVP_DATA`

Specify raw data to load at the specified address, passed to the FVP with the `--data` option.  This is a space-separated list of parameters in the format `[INST=]FILE@[MEMSPACE:]ADDRESS`. For example:

```
FVP_DATA = "cluster0.cpu0=Image@0x80080000 \
            cluster0.cpu0=fvp-base-revc.dtb@0x83000000"
```

### `FVP_APPLICATIONS`

Applications to load on the cores, passed to the FVP with the `--application` option.  These are expressed as variable flags with the flag name being the instance and flag value the filename, for example:

```
FVP_APPLICATIONS[cluster0] = "linux-system.axf"
```

Note that symbols are not allowed in flag names, so if you need to use a wildcard in the instance then you'll need to use `FVP_EXTRA_ARGS` and `--application` directly.

### `FVP_TERMINALS`

Map hardware serial ports to abstract names. For example the `FVP_Base_RevC-2xAEMvA` FVP exposes four serial ports, `terminal_0` to `terminal_3`.  Typically only `terminal_0` is used in the `fvp-base` machine so this can be named `"Console"` and the others `""`.  When runfvp starts terminals it will only start named serial ports, so instead of opening four windows where only one is useful, it will only open one.

For example:
```
FVP_TERMINALS[bp.terminal_0] = "Console"
FVP_TERMINALS[bp.terminal_1] = ""
FVP_TERMINALS[bp.terminal_2] = ""
FVP_TERMINALS[bp.terminal_3] = ""
```

### `FVP_CONSOLES`

This specifies what serial ports can be used in oeqa tests, along with an alias to be used in the test cases. Note that the values have to be the FVP identifier but without the board prefix, for example:
```
FVP_CONSOLES[default] = "terminal_0"
FVP_CONSOLES[tf-a] = "s_terminal_0"
```

The 'default' console is also used when `--console` is passed to runfvp.

### `FVP_EXTRA_ARGS`

Arbitrary extra arguments that are passed directly to the FVP.  For example:

```
FVP_EXTRA_ARGS = "--simlimit 60"
```

### `FVP_ENV_PASSTHROUGH`

The FVP is launched with an isolated set of environment variables. Add the name of a Bitbake variable to this list to pass it through to the FVP environment. For example:

```
FVP_ENV_PASSTHROUGH = "ARMLMD_LICENSE_FILE FM_TRACE_PLUGINS"
```


[AEM]: https://developer.arm.com/tools-and-software/simulation-models/fixed-virtual-platforms/arm-ecosystem-models
[FVP]: https://developer.arm.com/tools-and-software/simulation-models/fixed-virtual-platforms
[tmux]: https://tmux.github.io/
[INI]: https://docs.python.org/3/library/configparser.html
