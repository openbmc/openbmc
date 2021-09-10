TUNEVALID[idiv] = "ARM-state integer division instructions"
TUNE_CCARGS_MARCH_OPTS .= "${@bb.utils.contains('TUNE_FEATURES', 'idiv', '+idiv', '', d)}"
