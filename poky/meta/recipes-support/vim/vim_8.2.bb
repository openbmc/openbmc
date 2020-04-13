require vim.inc

PROVIDES = "xxd"

PACKAGECONFIG_class-native = ""
BBCLASSEXTEND = "native"

ALTERNATIVE_${PN}_append = " xxd"
ALTERNATIVE_TARGET[xxd] = "${bindir}/xxd"
ALTERNATIVE_LINK_NAME[xxd] = "${bindir}/xxd"
