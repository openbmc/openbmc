# OE Image Files from SBoM

This is an example python script that will list the packaged files with their
checksums based on the SPDX 3.0.1 SBoM.

It can be used as a template for other programs to investigate output based on
OE SPDX SBoMs

## Installation

This project can be installed using an virtual environment:
```
python3 -m venv .venv
.venv/bin/activate
python3 -m pip install -e '.[dev]'
```

## Usage

After installing, the `oe-image-files` program can be used to show the files, e.g.:

```
oe-image-files core-image-minimal-qemux86-64.rootfs.spdx.json
```
