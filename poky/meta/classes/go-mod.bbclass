# Handle Go Modules support
#
# When using Go Modules, the the current working directory MUST be at or below
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
export GOBUILDFLAGS ?= "-v ${GO_LDFLAGS} -modcacherw"

inherit go

GO_WORKDIR ?= "${GO_IMPORT}"
do_compile[dirs] += "${B}/src/${GO_WORKDIR}"
