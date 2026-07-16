# Development Instructions

## Requirements

- Python >= 3.9
- pip >= 19 (for installation)

## Testing

To lint the `bitbake-setup` pypi packaging, run the ruff tool.
```bash
ruff check bin/bitbake-setup packaging-pypi/bitbake-setup
```

The steps to build and test the `bitbake-setup` pypi packaging have been automated with the `bitbake-selftest` tool.  This tool automatically creates a Python virtual environment for you.

Run the bitbake-selftest
```bash
BB_SKIP_PYPI_TESTS=no bin/bitbake-selftest -v bb.tests.setup.PyPIPackagingTest
```

## Packaging

### Create the development sandbox

To create the development sandbox run:
```bash
packaging-pypi/bitbake-setup/package-bitbake-setup.py
cd packaging-workspace
```

### Updating the vendored modules

You may use the Python vendoring module to update the vendored modules. Its configuration can be found in the pyproject.toml under the tool.vendoring section.

This information includes:
- the destination directory where the vendored modules are stored (e.g. lib/bb/_vendor)
- the module names and versions to vendor are found in the requirements file vendor.txt
- the namespace to place the modules in (eg. bb._vendor)
- the patches to apply to the pristine source code (eg. vendor/patches)

To run the tool you may install it from pypi and run it from the top-level directory.

```bash
python3 -m pip install vendoring
vendoring sync .
```

### Building the package

To install the development tools manually run:
```bash
python3 -m pip install -e '.[dev]'
```

To build a wheel (.whl) then use:
```bash
python3 -m build
```

This produces a wheel (.whl) file in the dist directory.  This may be installed using pip.

### Installing the package

```bash
python3 -m pip install dist/bitbake_setup-*-py3-none-any.whl
```
