# bitbake-setup

This package provides the `bitbake-setup` command and the Python modules
required to support BitBake setup and configuration.

## Usage

Instructions on uses of bitbake-setup can be found in
[Setting Up the Environment With bitbake-setup](https://docs.yoctoproject.org/bitbake/dev/bitbake-user-manual/bitbake-user-manual-environment-setup.html) from the Yocto Project manual.

List the available configurations;
```bash
bitbake-setup list
```

Show the help for the bitbake-setup init subcommand:
```bash
bitbake-setup init --help
usage: bitbake-setup init [-h] [--non-interactive] [--source-overrides SOURCE_OVERRIDES] [--setup-dir-name SETUP_DIR_NAME] [--skip-selection SKIP_SELECTION] [-L SOURCE_NAME PATH]
                          [config ...]

positional arguments:
  config                path/URL/id to a configuration file (use 'list' command to get available ids), followed by configuration options. Bitbake-setup will ask to choose from available
                        choices if command line doesn't completely specify them.

options:
  -h, --help            show this help message and exit
  --non-interactive     Do not ask to interactively choose from available options; if bitbake-setup cannot make a decision it will stop with a failure.
  --source-overrides SOURCE_OVERRIDES
                        Override sources information (repositories/revisions) with values from a local json file.
  --setup-dir-name SETUP_DIR_NAME
                        A custom setup directory name under the top directory.
  --skip-selection SKIP_SELECTION
                        Do not select and set an option/fragment from available choices; the resulting bitbake configuration may be incomplete.
  -L SOURCE_NAME PATH, --use-local-source SOURCE_NAME PATH
                        Symlink local source into a build, instead of getting it as prescribed by a configuration (useful for local development).
```

To initialize a workspace for the Poky reference distro using the development branch (ie. "master") of OpenEmbedded:
```bash
bitbake-setup init poky-master
```