SUMMARY = "devtool test for overrides and patches"
LICENSE = "CLOSED"
INHIBIT_DEFAULT_DEPS = "1"
EXCLUDE_FROM_WORLD = "1"

SRC_URI = "file://source;subdir=${BP}"
SRC_URI_append_qemuarm = " file://arm.patch;striplevel=0"
SRC_URI_append_qemux86 = " file://x86.patch;striplevel=0"
