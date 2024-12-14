#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Handle Go Modules support
#
# When using Go Modules, the current working directory MUST be at or below
# the location of the 'go.mod' file when the go tool is used, and there is no
# way to tell it to look elsewhere.  It will automatically look upwards for the
# file, but not downwards.
#
# To support this use case, we provide the `GO_WORKDIR` variable, which defaults
# to `GO_IMPORT` but allows for easy override.
#
# Copyright 2020 (C) O.S. Systems Software LTDA.

# The '-modcacherw' option ensures we have write access to the cached objects so
# we avoid errors during clean task as well as when removing the TMPDIR.
GOBUILDFLAGS:append = " -modcacherw"

inherit go

export GOMODCACHE = "${S}/pkg/mod"
GO_MOD_CACHE_DIR = "${@os.path.relpath(d.getVar('GOMODCACHE'), d.getVar('WORKDIR'))}"
do_unpack[cleandirs] += "${GOMODCACHE}"

GO_WORKDIR ?= "${GO_IMPORT}"
do_compile[dirs] += "${B}/src/${GO_WORKDIR}"

# Make go install unpack the module zip files in the module cache directory
# before the license directory is polulated with license files.
addtask do_compile before do_populate_lic
