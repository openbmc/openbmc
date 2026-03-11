<!--
SPDX-FileCopyrightText: Andrei Gherzan <andrei.gherzan@huawei.com>

SPDX-License-Identifier: MIT
-->

# Docker image for DCO checks

This image provides the environment and the logic of running a DCO check
against a repository.

## Configuration

The `entrypoint.sh` script assumes at runtime that the repository to be checked
is available under `/work`. This path is to be populated via bind mounts when
running the container.
