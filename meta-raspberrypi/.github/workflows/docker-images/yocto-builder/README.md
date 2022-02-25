<!--
SPDX-FileCopyrightText: Andrei Gherzan <andrei.gherzan@huawei.com>

SPDX-License-Identifier: MIT
-->

# Docker image for builds

This defines the docker image for running Yocto/OE based operations/builds. It
privides multiple scripts for driving different operations.

## Configuration

The `entrypoint` scripts assumes at runtime that the repository to drive the
operation against is available under `/work`. This path is to be populated via
bind mounts when running the container.
