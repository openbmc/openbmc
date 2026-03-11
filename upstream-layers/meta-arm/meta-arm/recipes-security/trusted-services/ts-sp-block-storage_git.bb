# SPDX-FileCopyrightText: <text>Copyright 2023 Arm Limited and/or its
# affiliates <open-source-office@arm.com></text>
#
# SPDX-License-Identifier: MIT

DESCRIPTION = "Trusted Services block storage service provider"

require ts-sp-common.inc

SP_UUID = "${BLOCK_STORAGE_UUID}"
TS_SP_BLOCK_STORAGE_CONFIG ?= "default"

OECMAKE_SOURCEPATH = "${S}/deployments/block-storage/config/${TS_SP_BLOCK_STORAGE_CONFIG}-${TS_ENV}"
