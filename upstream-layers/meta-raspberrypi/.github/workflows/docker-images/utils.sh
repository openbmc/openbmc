#!/bin/sh

# SPDX-FileCopyrightText: Andrei Gherzan <andrei.gherzan@huawei.com>
#
# SPDX-License-Identifier: MIT

_log() {
	_level="$1"
	_msg="$2"
	echo "[$_level] $_msg"
}

error() {
	_msg="$1"
	_log "ERR" "$1"
	exit 1
}

warn() {
	_msg="$1"
	_log "WRN" "$1"
	exit 1
}

log() {
	_msg="$1"
	_log "LOG" "$1"
}
