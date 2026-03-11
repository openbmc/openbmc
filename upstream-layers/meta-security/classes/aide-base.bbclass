#
# Copyright 2022 Armin Kuster <akuster808@gmail.com>
#

STAGING_AIDE_DIR ?= "${TMPDIR}/work-shared/${MACHINE}/aida"
AIDE_INCLUDE_DIRS ?= "/lib"
AIDE_SKIP_DIRS ?= "/lib/modules/.\*"

AIDE_SCAN_POSTINIT ?= "0"
AIDE_RESCAN_POSTINIT ?= "0"

