<!--
SPDX-FileCopyrightText: Andrei Gherzan <andrei.gherzan@huawei.com>

SPDX-License-Identifier: MIT
-->

# Docker images for CI

Each directory contains the files for a docker image.

## Building an image

When building a docker image, the build context is expected to be where this
`README.md` file resides. This means that building the images will require
passing the appropriate `-f` argument.

Here is an example for building the `dco-check` image:

```
docker build . -f dco-check/Dockerfile -t dco-check
```
