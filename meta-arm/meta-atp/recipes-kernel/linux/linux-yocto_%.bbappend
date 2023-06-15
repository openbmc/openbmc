FILESEXTRAPATHS:prepend:gem5-atp-arm64 := "${THISDIR}/files:"
SRC_URI:append:gem5-atp-arm64 = " file://no_ftrace.cfg file://smmuv3.cfg"
