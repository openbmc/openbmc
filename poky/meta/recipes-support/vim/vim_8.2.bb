require vim.inc

PROVIDES = "xxd"

PACKAGECONFIG_class-native = ""
BBCLASSEXTEND = "native nativesdk"

ALTERNATIVE_${PN}_append = " xxd"
ALTERNATIVE_TARGET[xxd] = "${bindir}/xxd"
ALTERNATIVE_LINK_NAME[xxd] = "${bindir}/xxd"

# We override the default in security_flags.inc because vim (not vim-tiny!) will abort
# in many places for _FORTIFY_SOURCE=2.  Security flags become part of CC.
#
lcl_maybe_fortify = "${@oe.utils.conditional('DEBUG_BUILD','1','','-D_FORTIFY_SOURCE=1',d)}"
