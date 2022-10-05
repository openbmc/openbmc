#!/bin/bash
#
# Copyright (c) 2022 Nuvoton Technology Corporation.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#	http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# This script to reload the hwmon/dimm/nvme sensors
# Verson : v3

# stop services
systemctl stop xyz.openbmc_project.hwmontempsensor.service
sleep 0.5s
systemctl stop xyz.openbmc_project.dimmsensor.service
sleep 0.5s
systemctl stop xyz.openbmc_project.nvmesensor.service
sleep 0.5s
systemctl stop xyz.openbmc_project.psusensor.service
sleep 0.5s

# reload i2c drivers
echo f0081000.i2c > /sys/bus/platform/drivers/nuvoton-i2c/unbind
sleep 0.5s
echo f008a000.i2c > /sys/bus/platform/drivers/nuvoton-i2c/unbind
sleep 0.5s
echo f0081000.i2c > /sys/bus/platform/drivers/nuvoton-i2c/bind
sleep 0.5s
echo f008a000.i2c > /sys/bus/platform/drivers/nuvoton-i2c/bind
sleep 0.5s

# start services
systemctl start xyz.openbmc_project.hwmontempsensor.service
sleep 0.5s
systemctl start xyz.openbmc_project.dimmsensor.service
sleep 0.5s
systemctl start xyz.openbmc_project.nvmesensor.service
sleep 0.5s
systemctl start xyz.openbmc_project.psusensor.service
sleep 0.5s
