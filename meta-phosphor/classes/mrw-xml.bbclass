MRW_XML ??= "${MACHINE}.xml"
mrw_datadir = "${datadir}/obmc-mrw"
SKIP_BROKEN_MRW ?= "0"
EXTRA_MRW_SCRIPT_ARGS = "${@bb.utils.contains("SKIP_BROKEN_MRW", "0", "", "--skip-broken-mrw", d)}"
